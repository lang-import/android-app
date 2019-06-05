package lang_import.org.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.preference.PreferenceManager
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_reader.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.select
import java.lang.Exception
import java.util.logging.Logger


class ReaderActivity : AppCompatActivity() {
    //draft BD
    var informersMap: HashMap<String, String> = Informers().map
    var informerURLList = mutableSetOf<String>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    val readerList: MutableList<FeedReader>
        get() = FeedReaderList()

    fun FeedReaderList(): MutableList<FeedReader> {
        val lst = mutableListOf<FeedReader>()
        for (url in informerURLList) {
            lst.add(FeedReader(url, this))
        }
        return lst
    }

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
        val envInformers = env.getStringSet("informers", mutableSetOf())
        updateInformers(readUrlDB())

        fun forceUpdateEnv() {
            env.edit().putInt("dummy", 0).apply()
            env.edit().putInt("dummy", 1).apply()
        }

        //first App Launch
        if (envInformers.isEmpty()) {
            val intent = Intent(this, InformersMenu::class.java)
            startActivity(intent)
        }

        //safe clear envInformers from trash
        for (informer in envInformers) {
            if (!informersMap.containsKey(informer)) {
                envInformers.remove(informer)
                env.edit().putStringSet("informers", envInformers).apply()
                forceUpdateEnv()
                Log.e("RM_RSS:", informer.toString())
            }
        }

        for (informer in envInformers) {
            if (informersMap.containsKey(informer)) {
                informerURLList.add(informersMap.getValue(informer))
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


        fun openConfigMenu() {
            val intent = Intent(this, ConfigActivity::class.java)
            startActivity(intent)
        }

        fun openDictsMenu() {
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

    private fun readUrlDB(): HashMap<String, String> {
        val customUrls: HashMap<String, String> = hashMapOf()
        try {
            val allRows = database.use {
                select(INFORMERS_DB).exec { parseList(classParser<DictRowParserUrl>()) }
            }
            for (row in allRows) {
                val resLst = row.getLst()
                customUrls[resLst[0]] = resLst[1]
            }
        } catch (e: Exception) {
            Log.e("DB_ACCESS_ERROR:", e.toString())
        }
        return customUrls
    }

    private fun updateInformers(mp: HashMap<String, String>) {
        for (key in mp.keys) {
            val v = mp.get(key)
            if (v != null) {
                informersMap.put(key, v)
            }
        }
    }


    fun update(context: Context) {
        var allFeed = Feed()
        //setTitle("loading ${reader.url}...")
        status = getString(R.string.loading).trimIndent()
        launch {
            for (reader in readerList) {
                val feed = async { FeedReader(reader.url, context).fetch() }
                val completeFeed = feed.await()
                Logger.getLogger("READER").info("fetch complete: data=${completeFeed}")
                allFeed = FeedReader(reader.url, context).merge(allFeed, completeFeed)
            }
            runOnUiThread { showData(allFeed) }
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

            field = d
        }

    fun done() {
        runOnUiThread {
            findViewById<ViewGroup>(R.id.reader_progress).visibility = View.GONE
        }
    }
}
