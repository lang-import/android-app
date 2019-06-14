package lang_import.org.app.sites

import android.util.Log
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.net.URL

fun fallback(url: String): Element {
    return Jsoup.parse(URL(url).readText())
}

fun mailRu(url: String): Element {
    val content = URL(url.replace("news.mail.ru/", "news.mail.ru/go-mobile/")).readText()
    val page = Jsoup.parse(content)
    val article = page.selectFirst(".article")
    article.select("script").remove()
    article.select(".block.margin_horizontal_m10").remove()
    article.select(".hdr.hdr_nobg.hdr_noborder.hdr_html.margin_vertical_20").remove()
    article.select(".shares").remove()

    // add sub-text from article
    article.append(page.selectFirst(".article__text").toString())

    val src = "<br/><a href=\"${url}\">Полная версия статьи.</a>"
    article.append(src)
    return article
}

fun gohaSimple(url: String): Element {
    val content = URL(url).readText()
    val page = Jsoup.parse(content)
    val article = page.select("div.news").first()
    val src = "<br/><a href=\"${url}\">Полная версия статьи.</a>"
    article.append(src)
    return article
}

fun tprogerSimple(url: String): Element {
    val content = URL(url).readText()
    val page = Jsoup.parse(content)
    val article = page.select("article").first()
    val src = "<br/><a href=\"${url}\">Полная версия статьи.</a>"
    article.append(src)
    return article
}