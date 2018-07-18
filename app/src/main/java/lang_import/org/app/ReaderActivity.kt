package lang_import.org.app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.preference.PreferenceManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_reader.*
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.util.logging.Logger


class ReaderActivity : AppCompatActivity() {
    //draft BD
    val informersMap: HashMap<String, String> = hashMapOf(
            "HABR" to "https://habr.com/rss/all/",
            "Yandex.science" to "https://news.yandex.ru/science.rss",
            "mail.ru" to "https://news.mail.ru/rss/"
    )
    var informerURL = ""
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    val reader: FeedReader
        get() = FeedReader(informerURL, this)

    override fun onRestart() {
        super.onRestart()
        val env = PreferenceManager.getDefaultSharedPreferences(this)
        val refresh = env.getBoolean("needRefresh", false)
        if (refresh) {
            env.edit().putBoolean("needRefresh", false).apply()
            finish()
            startActivity(getIntent())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val context: Context = getApplicationContext()
        super.onCreate(savedInstanceState)
        val env = PreferenceManager.getDefaultSharedPreferences(this)
        //draft dummy for one url
        //TODO multiple URL
        val envInformers = env.getStringSet("informers", mutableSetOf())

        //first App Launch
        if (envInformers.isEmpty()) {
            val intent = Intent(this, InformersMenu::class.java)
            startActivity(intent)
        }
        for (informer in envInformers) {
            if (informersMap.containsKey(informer)) {
                informerURL = informersMap.getValue(informer)
            } else {
                envInformers.remove(informer)
                env.edit().putStringSet("informers", envInformers).apply()
                //TODO Force update env
            }
        }

        setContentView(R.layout.activity_reader)
        update(context)
        viewManager = LinearLayoutManager(this)

        setSupportActionBar(toolbar)

        // Side menu
        val drawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
                this,
                drawer_layout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ) {}
        drawerToggle.isDrawerIndicatorEnabled = true
        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        fun crtBtn(txt: String) {
            //TODO logic
        }

        fun openConfigMenu() {
            val intent = Intent(this, ConfigActivity::class.java)
            startActivity(intent)
        }

        fun openDictsMenu() {
            //TODO read dicts from file
            //test dict for develop
            val dict = HashMap<String, Any>()
            val engl = HashMap<String, String>()
            engl.put("hello", "привет")
            dict.put("EnglishHH", engl)
            //TODO dynamically generate buttons
            for (lang in dict.keys) {
                crtBtn(lang)
            }

            val intent = Intent(this, DictActivity::class.java)
            startActivity(intent)
        }

        // draft actions
        navigation_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_lang -> openDictsMenu()
                R.id.action_conf -> openConfigMenu() // toast("Меню общих настроек")
                R.id.action_wcolor -> {
                    drawer_layout.setBackgroundColor(Color.WHITE)
                }
                R.id.action_bcolor -> {
                    drawer_layout.setBackgroundColor(Color.BLACK)
                }

            }
            drawer_layout.closeDrawer(GravityCompat.START)
            true
        }

    }

    private fun Context.toast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    fun update(context: Context) {
        setTitle("loading ${reader.url}...")
        status = "loading..."
        launch{
            val feed = async{FeedReader(informerURL, context).fetch()}
            val completeFeed = feed.await()
            runOnUiThread {showData(completeFeed)}
            Logger.getLogger("READER").info("fetch complete: data=${completeFeed}, exception={TODO}")
            done()
        }
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

    var status: CharSequence = ""
        set(d) {
            val view = findViewById<TextView>(R.id.reader_progress_status)
            runOnUiThread {
                view.text = d
                findViewById<ViewGroup>(R.id.reader_progress).visibility = View.VISIBLE
            }

        }

    fun done() {
        runOnUiThread {
            findViewById<ViewGroup>(R.id.reader_progress).visibility = View.GONE
        }
    }
}
