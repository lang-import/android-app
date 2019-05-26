package lang_import.org.app

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.TextView
import database
import kotlinx.android.synthetic.main.article_activity.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.select
import org.jsoup.Jsoup
import org.jsoup.nodes.Node
import java.io.File
import java.net.URL
import java.util.regex.Pattern

class ArticleActivity : AppCompatActivity() {
    private lateinit var viewManager: RecyclerView.LayoutManager
    var part = 0
    var targetLang = ""
    var usedDict = ""
    //TODO need save css to another place
    val css = """
             <style>
        .tooltip {
          position: relative;
          display: inline-block;
          border-bottom: 1px dotted black;
        }

        .tooltip .tooltiptext {
          visibility: hidden;
          width: 120px;
          background-color: black;
          color: #fff;
          text-align: center;
          border-radius: 6px;
          padding: 5px 0;

          /* Position the tooltip */
          position: absolute;
          z-index: 1;
        }

        .tooltip:hover .tooltiptext {
          visibility: visible;
        }
        </style>

    """.trimIndent()

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


    //TODO find were we storage parsed link from FeedReader and use it link for getFullArticle(link)
    fun fullArticle(link: String) {
        val webView = findViewById<WebView>(R.id.article_description)
        status = "loading..."
        launch {
            val res = importLang(clearText(getFullArticle(link)))
            status = "preparing..."
            // TODO Need some parse...

            val content = "<html><body>${res}<br/><br/><br/><br/></body></html>"
            runOnUiThread {
                webView.settings.javaScriptEnabled = false
                webView.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null)
            }
            done()
        }
    }

    suspend fun getFullArticle(url: String): String {
        val result = async { URL(url).readText() }
        return result.await()
    }

    fun clearText(txt: String): String {
        return fixImg(txt)
    }


    suspend fun importLang(txt: String): String {
        var rep = txt
        val factor = part.toDouble() / 100
        // TODO change words getter (need get from articleStr)
        val words = "[\\w\\-]{2,}".toRegex().findAll(Jsoup.parse(txt).text()).map({ it.value }).sorted().distinct().toList().shuffled()
        val articleLst: MutableList<AcrticlePart> = ArrayList()
        var nodesLst: List<Node> = ArrayList()
        nodesLst = NodeIter(Jsoup.parse(txt).body(), nodesLst)

        for (node in nodesLst) {
            if (arrayOf("#text").contains(node.nodeName().trim())) {
                val articleObj = AcrticlePart()
                articleObj.oldText = node.toString()
                articleObj.tagName = node.nodeName()
                articleLst += articleObj
            }
        }
        val toReplace = words.takeLast((words.size * factor).toInt())
        status = "translating..."
        val lock = Object()
        val context: Context = getApplicationContext()
        for (originalWord in toReplace) {
            val newWord = exchange(originalWord, lock, context, originalWord)
            for (obj in articleLst) {
                if (originalWord in obj.oldText) {
                    obj.newText = obj.oldText.replace(originalWord, newWord)
                    rep = rep.replace(obj.oldText.trim(), obj.newText.trim())
                    obj.oldText = obj.newText
                }
            }
        }
        //stage local translate
        if (usedDict != "") {
            Log.i("replace", "Start translate with local dict ${usedDict}")
            rep = localTranslater(rep)
        }
        rep = css + rep
        return rep
    }

    fun NodeIter(srcNode: Node, myNodeList: List<Node>): List<Node> {
        var lst = myNodeList
        for (node in srcNode.childNodes()) {
            if (arrayOf("#text").contains(node.nodeName().trim())) {
                lst += node
            } else {
                lst = NodeIter(node, lst);
            }
        }
        return lst
    }

    suspend fun exchange(originalWord: String, lock: Any, context: Context, txt: String): String {
        var rep = txt
        val str = defaultProvider.Translate(context, "", originalWord.toLowerCase(), targetLang)
        var word = str
        if (originalWord[0].isUpperCase()) {
            word = str.capitalize()
        }
        synchronized(lock) {
            Log.i("replace", "$originalWord -> $word")
            // TODO get list of marked txt and change it all in one rq?
            rep = rep.replace(originalWord, "<div class=\"tooltip\">$word<span class=\"tooltiptext\">$originalWord</span></div>")
        }
        return rep
    }

    private fun localTranslater(rep: String): String {
        var res = rep
        val allRows = database.use {
            select(usedDict).exec { parseList(classParser<DictRowParser>()) }
        }
        for (rowObj in allRows) {
            val rowLst = rowObj.getLst()
            //TODO update getting word for replace
            // (exampe "word" cases: "word, ..." or ")password" )
            if (rowLst[0] in rep) {
                res = res.replace(rowLst[0], rowLst[1])
                Log.i("replace(local)", "${rowLst[0]} -> ${rowLst[1]}")
            }
        }
        return res
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

}

val imgPattern = "\\<img.*?src=\"([^\"]+)\".*?\\>".toRegex()
fun fixImg(txt: String): String {
    return imgPattern.replace(txt, "<img src=\"$1\" style=\"width: 100%; height: auto\" />")
}
