package lang_import.org.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.webkit.WebView

class ArticleActivity : AppCompatActivity() {
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.article_activity)
        viewManager = LinearLayoutManager(this)
        val webView = findViewById<WebView>(R.id.article_description)

        setTitle(intent.extras.getString("title"))

        var readedTxt = importLang(clearText(intent.extras.getString("discript")))
        readedTxt = "<html><body>${readedTxt}</body></html>"
        webView.settings.javaScriptEnabled = false
        webView.loadDataWithBaseURL(null, readedTxt, "text/html", "UTF-8", null)
    }

    fun clearText(txt: String): String {
        return fixImg(txt)
    }


    fun importLang(txt: String): String {
        var rep = txt
        //TODO: customize part
        val part = 1

        val words = "\\w+".toRegex().findAll(txt).map({ it.value }).sorted().distinct().toList()
        val toReplace = words.takeLast((words.size * part).toInt())

        //TODO: customize language(s)
        toReplace.forEach {
            defaultProvider.Translate("", it.toLowerCase(), "").ifPresent { newWord ->
                if (it[0] == it[0].toUpperCase()) {
                    rep = rep.replace(it, newWord.capitalize())
                } else {
                    rep = rep.replace(it, newWord)
                }
            }
        }
        return rep
    }


}

val imgPattern = "\\<img.*?src=\"([^\"]+)\".*?\\>".toRegex()
fun fixImg(txt: String): String {
    return imgPattern.replace(txt, "<img src=\"$1\" style=\"width: 100%; height: auto\" />")
}
