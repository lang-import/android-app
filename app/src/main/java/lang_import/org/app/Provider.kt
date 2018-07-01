package lang_import.org.app

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.lang.RuntimeException
import java.net.URLEncoder
import java.util.*
import java.util.concurrent.CompletableFuture

interface TranslateProvider {
    fun Translate(ctx: Context, originalLanguage: String, originalWord: String, targetLanguage: String): CompletableFuture<String>
}


class HardCodedTranslator : TranslateProvider {
    private val knowledge = mapOf(
            "новости" to "news",
            "страна" to "country",
            "компания" to "company"
    )

    override fun Translate(ctx: Context, originalLanguage: String, originalWord: String, targetLanguage: String) = CompletableFuture<String>().also {
        val t = knowledge.get(originalWord)
        if (t != null)
            it.complete(t)
        it.obtrudeException(RuntimeException("unknown word $originalWord"))
    }

}


class ServerTranslator(val url: String) : TranslateProvider {
    private var queue: RequestQueue? = null
    private var sctx: Context? = null
    private fun getVolley(ctx: Context): RequestQueue {
        if (ctx == sctx && sctx != null) {
            return queue!!
        }
        synchronized(this) {
            if (ctx == sctx && sctx != null) {
                return queue!!
            }
            queue = Volley.newRequestQueue(ctx)
            sctx = ctx
            return queue!!
        }
    }


    override fun Translate(ctx: Context, originalLanguage: String, originalWord: String, targetLanguage: String): CompletableFuture<String> {
        //TODO: add cache
        val queue = getVolley(ctx)
        val req = url + "/" + URLEncoder.encode(originalWord, "UTF-8") + "/to/" + URLEncoder.encode(targetLanguage, "UTF-8")
        val cf = CompletableFuture<String>()

        queue.add(StringRequest(req, {
            cf.complete(it)
        }, {
            cf.completeExceptionally(it)
        }))

        return cf
    }
}


// TODO: do it normally

val defaultProvider by lazy {
    ServerTranslator("http://importlang.reddec.net:10101/translate")
}





