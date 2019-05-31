package lang_import.org.app

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import com.beust.klaxon.Klaxon
import kotlinx.android.synthetic.main.article_activity.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import lang_import.org.app.sites.fetchContent
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.select
import org.jsoup.Jsoup
import org.jsoup.nodes.Node
import java.net.URL
import java.util.regex.Pattern

class ArticleActivity : AppCompatActivity() {
    private lateinit var viewManager: RecyclerView.LayoutManager
    var part = 0
    var targetLang = ""
    var usedDict = ""
    private val css: String
        get() = getString(R.string.css).trimIndent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val env = PreferenceManager.getDefaultSharedPreferences(this)
        usedDict = env.getString("usedDict", "")
        targetLang = env.getString("targetLang", "en")
        part = env.getInt("part", 0)
        setContentView(R.layout.article_activity)
        viewManager = LinearLayoutManager(this)
        val webView = findViewById<WebView>(R.id.article_description)

        back_btn.setOnClickListener { view ->
            this.finish()
        }


        more_btn.setOnClickListener {
            more_btn.hide()
            val link = intent.extras.getString("link")
            fullArticle(link)
        }


        //TODO add title for ArticleActivity
        setTitle(intent.extras.getString("title"))
        status = "loading..."
        launch {
            val res = importLang(clearText(intent.extras.getString("discript")))

            status = "preparing..."
            val content = "<html><body>${res}<br/><br/><br/><br/></body></html>"
            runOnUiThread {
                webView.settings.javaScriptEnabled = false
                webView.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null)
            }
            done()
        }


    }


    fun fullArticle(link: String) {
        val webView = findViewById<WebView>(R.id.article_description)
        status = "loading..."
        launch {
            val res = importLang(clearText(getFullArticle(link)))
            status = "preparing..."
            val content = "<html><body>${res}<br/><br/><br/><br/></body></html>"
            runOnUiThread {
                webView.settings.javaScriptEnabled = false
                webView.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null)
            }
            done()
        }
    }

    suspend fun getFullArticle(url: String): String {
        return fetchContent(url).html()
    }

    fun clearText(txt: String): String {
        return fixImg(txt)
    }

    var status: CharSequence = ""
        set(d) {
            val view = findViewById<TextView>(R.id.article_progress_status)
            runOnUiThread {
                view.text = d
                findViewById<ViewGroup>(R.id.article_progress).visibility = View.VISIBLE
            }

            field = d
        }

    fun done() {
        runOnUiThread {
            findViewById<ViewGroup>(R.id.article_progress).visibility = View.GONE
        }
    }

    suspend fun importLang(rawText: String): String {
        // Get only text-lines from strict list of tags (skip hrefs and tag attrs)
        var goodLines = mutableListOf<AcrticlePart>()
        val nodesLst = NodeIter(Jsoup.parse(rawText).body(), mutableListOf())

        // Collect Nodes from informers
        for (node in nodesLst) {
            val articleObj = AcrticlePart()
            articleObj.oldText = node.toString()
            articleObj.newText = articleObj.oldText
            articleObj.tagName = node.nodeName()
            if (articleObj.oldText.trim().split(" ").size > 2) {
                goodLines.add(articleObj)
            }
        }
        // factor is optional word "%" from configs
        val factor = part.toDouble() / 100

        // Prepare random list of all available words for translate
        val replaceWords = prepareWords(goodLines)
        val toReplace = replaceWords.takeLast((replaceWords.size * factor).toInt())
        status = "translating..."

        // Translate
        val importWords = massExchange(toReplace)
        for (word in importWords) {
            // Export back in Nodes
            goodLines = replaceInAllNodes(word, goodLines)
        }

//        // DEBUG view
//        for (ln in goodLines){
//            Log.i("NEW_LINES:", ln.newText)
//        }

        // Export Nodes to the Source informer
        var workText = css + " \n " + rawText
        for (node in goodLines) {
            workText = workText.replaceFirst(node.oldText, node.newText, true)
        }
        return workText
    }

    class ImportWord(var original: String, var lang: String, var word: String, val spell: String)

    suspend fun massExchange(originalWords: List<String>): MutableList<ImportWord> {
        val importWords = mutableListOf<ImportWord>()
        val translateResult = defaultProvider.MassTranslate(originalWords, targetLang)

        // Parse json answer from our service
        val jsonResponse = Klaxon().parseArray<ImportWord>(translateResult)
        if (jsonResponse != null) {

            for (rs in jsonResponse) {
                if (rs.word == "") { // TODO TMP FIX
                    continue        // TODO TMP FIX
                }                   // TODO TMP FIX

                Log.i("translate::", rs.original + "==>" + rs.word)

                rs.lang = " <div class=\"tooltip\">${rs.word}<span class=\"tooltiptext\">${rs.original}\n${rs.spell}</span></div> "
                importWords.add(rs)
            }
        }
        return importWords
    }


    private fun localTranslater(rep: String): String {
        var res = rep

        if (usedDict == "") {
            return rep

        }
        val allRows = getLocalDict()
        for (rowObj in allRows) {
            val rowLst = rowObj.getLst()
            val original = rowLst[0].trim().toLowerCase()
            val import = rowLst[1].trim().toLowerCase()

            if (original in rep) {
                res = res.replace(("([^\\w]+)(" + Pattern.quote(original) + ")([^\\w]+)").toRegex(),"$1${import}$3")

                Log.i("replace(local)", "${rowLst[0]} -> ${rowLst[1]}")
            }
        }
        return res
    }

    private fun getLocalDict(): List<DictRowParser> {
        var allRows: List<DictRowParser> = emptyList()
        try {
            val allRows = database.use {
                select(usedDict).exec { parseList(classParser<DictRowParser>()) }
            }
            return allRows
        } catch (e: Exception) {
            Log.e("LocalDictError:", e.toString())
            val env = PreferenceManager.getDefaultSharedPreferences(this)
            usedDict = "" //local clear
            env.edit().putString("usedDict", "").apply() //env clear
        }
        return allRows
    }

    private fun replaceInAllNodes(w: ImportWord, goodLines: MutableList<AcrticlePart>): MutableList<AcrticlePart> {
        for (node in goodLines) {
            node.newText = node.newText.replace(("([^\\w]+)(" + Pattern.quote(w.original) + ")([^\\w]+)").toRegex(),
                    "$1${w.lang}$3")
            // check in local dicts
            node.newText = localTranslater(node.newText)
        }
        return goodLines
    }


    private fun prepareWords(articleLst: MutableList<AcrticlePart>): MutableList<String> {
        var workText = ""
        for (obj in articleLst) {
            workText += obj.oldText.trim() + " "
        }
        val words = "[\\w\\-]{2,}".toRegex().findAll(workText).map({ it.value }).sorted().distinct().toList().shuffled()
        val toReplace = filterWords(words)

        return toReplace
    }

    private fun filterWords(words: List<String>): MutableList<String> {
        val result = mutableListOf<String>()
        for (w in words) {
            if (w.trim().length > 2) {
                result.add(w.trim())
            }
        }
        return result
    }

    private fun NodeIter(srcNode: Node, lst: MutableList<Node>): MutableList<Node> {
        var workList = lst
        for (node in srcNode.childNodes()) {
            val isValidParent = (node.parent().nodeName().trim() in arrayOf("div", "p", "span", "h1", "h2", "h3", "body"))
            val isText = node.nodeName().trim() == "#text"

            if (isText and isValidParent) {
                workList.add(node)
            } else {
                workList = NodeIter(node, workList);
            }
        }
        return workList
    }

}

val imgPattern = "\\<img.*?src=\"([^\"]+)\".*?\\>".toRegex()
fun fixImg(txt: String): String {
    return imgPattern.replace(txt, "<img src=\"$1\" style=\"width: 100%; height: auto\" />")
}
