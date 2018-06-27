package lang_import.org.app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import android.support.v7.widget.Toolbar
import java.util.logging.Logger

class ReaderActivity : AppCompatActivity() {
    val reader by lazy { FeedReader("https://habr.com/rss/all/", this) }
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)
        update()

        viewManager = LinearLayoutManager(this)


    }


    fun update() {
        setTitle("loading ${reader.url}...")
        reader.fetch().whenComplete({ it, ex ->
            if (ex != null) {
                runOnUiThread {
                    setTitle(ex.localizedMessage)
                }
            } else {
                runOnUiThread { showData(it) }
            }
            Logger.getLogger("READER").info("fetch complete: data=${it}, exception=${ex}")
        })
    }

    fun showData(feed: Feed) {
        setTitle(feed.channel.title)
        viewAdapter = FeedAdapter(feed)
        recyclerView = findViewById<RecyclerView>(R.id.feed_recycle_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    fun setTitle(text: String) {
        val toolbar = findViewById<View>(R.id.action_bar) as Toolbar
        val textView = toolbar.getChildAt(0) as TextView
        textView.text = text
    }
}
