package lang_import.org.app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.support.v7.widget.Toolbar
import java.util.logging.Logger

class ReaderActivity : AppCompatActivity() {
    val reader by lazy { FeedReader("https://habr.com/rss/all/", this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)
        update()
    }


    fun update() {
        setTitle("loading ${reader.url}...")
        reader.fetch().whenComplete({ it, ex ->
            if (ex != null) {
                runOnUiThread {
                    setTitle(ex.localizedMessage)
                }
            } else {
                runOnUiThread {
                    setTitle(it.channel.title)
                }
            }
            Logger.getLogger("READER").info("fetch complete: data=${it}, exception=${ex}")
        })
    }

    fun setTitle(text: String) {
        val toolbar = findViewById<View>(R.id.action_bar) as Toolbar
        val textView = toolbar.getChildAt(0) as TextView
        textView.text = text
    }
}
