package lang_import.org.app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import java.util.logging.Logger
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_reader.*

class ReaderActivity : AppCompatActivity(){
    val reader by lazy { FeedReader("https://habr.com/rss/all/", this) }
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)
        update()

        viewManager = LinearLayoutManager(this)

        setSupportActionBar(toolbar)

        // Side menu
        val drawerToggle:ActionBarDrawerToggle = object : ActionBarDrawerToggle(
                this,
                drawer_layout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ){}
        drawerToggle.isDrawerIndicatorEnabled = true
        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        fun crtBtn(txt: String){
            //TODO logic
        }

        fun openDictsMenu(){
            //TODO read dicts from file
            //test dict for develop
            val dict = HashMap<String,Any>()
            val engl = HashMap<String,String>()
            engl.put("hello","привет")
            dict.put("EnglishHH",engl)
            //TODO dynamically generate buttons
            for (lang in dict.keys){
                crtBtn(lang)
            }

            val intent = Intent(this, DictActivity::class.java)
            startActivity(intent)
        }

        // draft actions
        navigation_view.setNavigationItemSelectedListener{
            when (it.itemId){
                R.id.action_lang -> openDictsMenu()
                R.id.action_conf -> toast("Меню общих настроек")
                R.id.action_wcolor ->{drawer_layout.setBackgroundColor(Color.WHITE)}
                R.id.action_bcolor ->{drawer_layout.setBackgroundColor(Color.BLACK)}

            }
            drawer_layout.closeDrawer(GravityCompat.START)
            true
        }

    }

    private fun Context.toast(message:String){
        Toast.makeText(applicationContext,message,Toast.LENGTH_SHORT).show()
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
        val actionBar = supportActionBar
        actionBar?.title = text
    }
}
