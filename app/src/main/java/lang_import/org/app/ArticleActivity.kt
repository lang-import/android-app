package lang_import.org.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.webkit.WebView
import java.util.concurrent.CompletableFuture
import java.util.regex.Pattern

class ArticleActivity : AppCompatActivity() {
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.article_activity)
        viewManager = LinearLayoutManager(this)
        val webView = findViewById<WebView>(R.id.article_description)

        //TODO add title for ArticleActivity
        setTitle(intent.extras.getString("title"))

        importLang(clearText(intent.extras.getString("discript"))).whenComplete { readedTxt, ex ->
            if (ex != null) {
                Log.e("import", "translate", ex)
                return@whenComplete
            }
            val content = "<html><body>${readedTxt}</body></html>"
            runOnUiThread {
                webView.settings.javaScriptEnabled = false
                webView.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null)
            }
        }


    }

    fun clearText(txt: String): String {
        return fixImg(txt)
    }


    fun importLang(txt: String): CompletableFuture<String> {
        var rep = txt
        //TODO: customize part
        val part = 0.1 // 10%

        val words = "\\w+".toRegex().findAll(txt).map({ it.value }).sorted().distinct().toList()
        val toReplace = words.takeLast((words.size * part).toInt())

        //TODO: customize language(s)
        val lock = Object()
        return CompletableFuture.allOf(*toReplace.map { originalWord ->
            defaultProvider.Translate(this, "", originalWord.toLowerCase(), "en").thenApply {
                synchronized(lock) {
                    Log.i("replace", "$originalWord -> $it")
                    rep = rep.replace(("([^\\w]+)(" + Pattern.quote(originalWord) + ")([^\\w]+)").toRegex(), "$1$it$3")
                }
            }
        }.toTypedArray()).thenApply {
            rep
        }
    }

}

val imgPattern = "\\<img.*?src=\"([^\"]+)\".*?\\>".toRegex()
fun fixImg(txt: String): String {
    return imgPattern.replace(txt, "<img src=\"$1\" style=\"width: 100%; height: auto\" />")
}
