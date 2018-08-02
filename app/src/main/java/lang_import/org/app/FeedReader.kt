package lang_import.org.app

import android.content.Context
import android.util.Log
import kotlinx.coroutines.experimental.async
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import org.simpleframework.xml.core.Persister
import java.net.URL


@Root(name = "item", strict = false)
data class Item(@field:Element(name = "title", required = false) var title: String = "",
                @field:Element(name = "link", required = false) var link: String = "",
                @field:Element(name = "description", required = false) var summary: String = "",
                @field:Element(name = "pubDate", required = false) var pubDate: String = "",
                var logo: String = "")

@Root(name = "image", strict = false)
data class Image(@field:Element(name = "url") var imgUrl: String = "")

@Root(name = "channel", strict = false)
data class Channel(@field:Element(name = "title") var title: String = "",
                   @field:Element(name = "image", required = false) var imgBlock: Image = Image(),
                   @field:ElementList(name = "item", inline = true) var item: MutableList<Item> = mutableListOf<Item>())

@Root(name = "rss", strict = false)
data class Feed(@field:Element(name = "channel") var channel: Channel = Channel())


class FeedReader(val url: String, context: Context) {

    suspend fun fetch(): Feed {
        val result = async { URL(url).readText() }
        return fixItemDate(FeedParser().parseContent(result.await()))
    }

    private fun fixItemDate(feed: Feed): Feed {
        for (item in feed.channel.item) {
            val dt = item.pubDate.trim()
            val firstWord = dt.split(" ")[0]
            // Crop start words for strings like "Tue, 31 Jul 2018 20:11:12 GMT"
            if (firstWord.length > 2) {
                item.pubDate = dt.replace(firstWord, "").trim()
            }
        }
        return feed
    }

    fun merge(old: Feed, new: Feed): Feed {
        if (old.channel.title != "") {
            //TODO Some merge for names
            new.channel.title = "Все новости"
        }
        new.channel.item.addAll(old.channel.item)
        //sort items by Date
        new.channel.item = new.channel.item.sortedWith(compareBy({ it.pubDate })).toMutableList().asReversed()
        return new
    }
}


class FeedParser {
    suspend fun parseContent(content: String): Feed {
        val serializer = Persister()
        try {
            val obj = async { serializer.read(Feed::class.java, content) }.await()
            for (item in obj.channel.item) {
                //clear some url's
                item.logo = obj.channel.imgBlock.imgUrl
                if (item.logo.startsWith("//www.")) {
                    item.logo = item.logo.replace("//ww", "http://ww")
                }
            }
            return obj
        } catch (ex: Exception) {
            Log.e("Parsing error", "", ex)
            return Feed()
        }

    }
}