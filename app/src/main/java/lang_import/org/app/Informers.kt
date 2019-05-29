package lang_import.org.app

class Informers() {
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
}