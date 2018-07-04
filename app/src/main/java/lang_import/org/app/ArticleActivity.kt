package lang_import.org.app

import android.opengl.Visibility
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.TextView
import org.jsoup.Jsoup
import java.util.concurrent.CompletableFuture
import java.util.regex.Pattern

class ArticleActivity : AppCompatActivity() {
    private lateinit var viewManager: RecyclerView.LayoutManager
    var part = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val env = PreferenceManager.getDefaultSharedPreferences(this)
        part = env.getInt("part", 0)
        setContentView(R.layout.article_activity)
        viewManager = LinearLayoutManager(this)
        val webView = findViewById<WebView>(R.id.article_description)

        //TODO add title for ArticleActivity
        setTitle(intent.extras.getString("title"))
        status = "loading..."
        importLang(clearText(intent.extras.getString("discript"))).whenComplete { readedTxt, ex ->
            status = "preparing..."
            if (ex != null) {
                Log.e("import", "translate", ex)
                return@whenComplete
            }
            val content = "<html><body>${readedTxt}</body></html>"
            runOnUiThread {
                webView.settings.javaScriptEnabled = false
                webView.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null)
            }
            done()
        }


    }

    fun clearText(txt: String): String {
        return fixImg(txt)
    }


    fun importLang(txt: String): CompletableFuture<String> {
        var rep = txt
        val factor = part.toDouble() / 100

        val words = "[\\w\\-]{2,}".toRegex().findAll(Jsoup.parse(txt).text()).map({ it.value }).sorted().distinct().toList().shuffled()
        val toReplace = words.takeLast((words.size * factor).toInt())
        status = "translating..."
        //TODO: customize language(s)
        val lock = Object()
        return CompletableFuture.allOf(*toReplace.map { originalWord ->
            defaultProvider.Translate(this, "", originalWord.toLowerCase(), "en").thenApply {
                var word = it
                if (originalWord[0].isUpperCase()) {
                    word =it.capitalize()
                }
                synchronized(lock) {
                    Log.i("replace", "$originalWord -> $word")
                    rep = rep.replace(("([^\\w]+)(" + Pattern.quote(originalWord) + ")([^\\w]+)").toRegex(), "$1$word$3")
                }
            }
        }.toTypedArray()).thenApply {
            rep
        }
    }

    var status: CharSequence = ""
        set(d) {
            val view = findViewById<TextView>(R.id.article_progress_status)
            runOnUiThread {
                view.text = d
                findViewById<ViewGroup>(R.id.article_progress).visibility = View.VISIBLE
            }

        }

    fun done() {
        runOnUiThread {
            findViewById<ViewGroup>(R.id.article_progress).visibility = View.GONE
        }
    }
    //get() = findViewById<TextView>(R.id.article_progress_status).text

}

val imgPattern = "\\<img.*?src=\"([^\"]+)\".*?\\>".toRegex()
fun fixImg(txt: String): String {
    return imgPattern.replace(txt, "<img src=\"$1\" style=\"width: 100%; height: auto\" />")
}
