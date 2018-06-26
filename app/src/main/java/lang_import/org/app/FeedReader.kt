package lang_import.org.app

import android.content.Context
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import org.simpleframework.xml.core.Persister
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

@Root(strict = false)
data class Item(@Element var title: String = "",
                @Element var link: String = "",
                @Element var summary: String = "")

@Root(strict = false)
data class Channel(@Element var title: String = "",
                   @Element var item: Array<Item> = arrayOf())

@Root(strict = false)
data class Feed(@Element var channel: Channel = Channel())

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