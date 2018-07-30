package lang_import.org.app

import android.content.Context
import kotlinx.coroutines.experimental.async
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import org.simpleframework.xml.core.Persister
import java.net.URL


@Root(name = "item", strict = false)
data class Item(@field:Element(name = "title", required = false) var title: String = "",
                @field:Element(name = "link", required = false) var link: String = "",
                @field:Element(name = "description", required = false) var summary: String = "")


@Root(name = "channel", strict = false)
data class Channel(@field:Element(name = "title") var title: String = "", @field:ElementList(name = "item", inline = true) var item: MutableList<Item> = mutableListOf<Item>())

@Root(name = "rss", strict = false)
data class Feed(@field:Element(name = "channel") var channel: Channel = Channel())


class FeedReader(val url: String, context: Context) {

    suspend fun fetch(): Feed {
        val result = async { URL(url).readText() }
        return FeedParser().parseContent(result.await())
    }

    fun merge(old: Feed, new: Feed): Feed {
        if (old.channel.title != ""){
            //TODO Some merge names
            new.channel.title="Все новости"
        }
        new.channel.item.addAll(old.channel.item)
        return new
    }
}


class FeedParser {
    suspend fun parseContent(content: String): Feed {
        val serializer = Persister()
        val obj = async { serializer.read(Feed::class.java, content) }
        return obj.await()
    }
}