package lang_import.org.app

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.util.Log
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.select

class Informers {
    val map: HashMap<String, String> = hashMapOf(
            "HABR" to "https://habr.com/rss/all/",
            "mail.ru" to "https://news.mail.ru/rss/",
            "goha" to "https://www.goha.ru/rss/news",
            "tproger" to "https://tproger.ru/feed/",
            "lenta" to "https://lenta.ru/rss/news",
            "stopgame" to "https://rss.stopgame.ru/rss_news.xml",
            "RT" to "https://russian.rt.com/rss",
            "lifehacker" to "https://lifehacker.ru/feed/"
    )
    //TODO BD SQLite => type:name:site:ischecked
    // TODO save user custom rss

//    fun get_info() {
//        val allRows = database.use {
//            select(INFORMERS_DB).exec { parseList(classParser<DictRowParser>()) }
//        }
//        Log.i("DB_ACCESS", allRows.toString())
//    }

}

