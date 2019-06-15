package lang_import.org.app.sites

import org.jsoup.nodes.Element
import java.net.URL


fun fetchContent(url: String): Element {
    val fUrl = URL(url)
    return when (fUrl.host) {
        "news.mail.ru" -> mailRu(url)
        "www.goha.ru" -> gohaSimple(url)
        "tproger.ru" -> tprogerSimple(url)
        "3dnews.ru" -> threeDnews(url)
        "habr.com" -> habr(url)
        else -> fallback(url)
    }
}