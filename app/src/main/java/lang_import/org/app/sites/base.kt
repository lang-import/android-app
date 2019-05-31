package lang_import.org.app.sites

import org.jsoup.nodes.Element
import java.net.URL


fun fetchContent(url: String): Element {
    val fUrl = URL(url)
    return when (fUrl.host) {
        "news.mail.ru" -> mailRu(url)
        else -> fallback(url)
    }
}