package lang_import.org.app

import android.content.Context
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import org.simpleframework.xml.core.Persister
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

@Root(name = "item", strict = false)
data class Item(@field:Element(name = "title",required = false) var title: String = "",
                @field:Element(name = "link",required = false) var link: String = "",
                @field:Element(name="description", required = false) var summary: String = "")


@Root(name = "channel",strict = false)
data class Channel(@field:Element(name = "title") var title: String = "",
                   @field:Element(name = "item") var item: Array<Item> = arrayOf())

@Root(name = "rss",strict = false)
data class Feed(@field:Element(name = "channel") var channel: Channel = Channel())



class FeedReader(val url: String, val context: Context) {
    private val feedParserPool = Executors.newCachedThreadPool()
    private val queue = Volley.newRequestQueue(context)

    fun fetch(): CompletableFuture<Feed> {
        val cf = CompletableFuture<String>()

        queue.add(StringRequest(url, {
            cf.complete(it)
        }, {
            cf.completeExceptionally(it)
        }))

        return cf.thenCompose(this::parseContent)
    }

    private fun parseContent(content: String): CompletableFuture<Feed> {
        val cf = CompletableFuture<Feed>()
        feedParserPool.submit {
            val serializer = Persister()
            try {
                val obj = serializer.read(Feed::class.java, content)
                cf.complete(obj)
            } catch (ex: Exception) {
                cf.completeExceptionally(ex)
            }
        }
        return cf
    }

}