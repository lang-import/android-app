package lang_import.org.app

import android.content.Context
import android.util.Log
import kotlinx.coroutines.async
import java.net.URL
import java.net.URLEncoder

interface TranslateProvider {
    suspend fun Translate(ctx: Context, originalLanguage: String, originalWord: String, targetLanguage: String): String
}


class HardCodedTranslator : TranslateProvider {
    private val knowledge = mapOf(
            "новости" to "news",
            "страна" to "country",
            "компания" to "company"
    )

    override suspend fun Translate(ctx: Context, originalLanguage: String, originalWord: String, targetLanguage: String): String {
        val t = knowledge.get(originalWord)
        if (t != null)
            return t
        else
            return "[not in base]"
    }

}


class ServerTranslator(val url: String) : TranslateProvider {

    override suspend fun Translate(ctx: Context, originalLanguage: String, originalWord: String, targetLanguage: String): String {
        //TODO: add cache
        val req = url + "translate/" + URLEncoder.encode(originalWord, "UTF-8") + "/to/" + URLEncoder.encode(targetLanguage, "UTF-8")
        val res = async { URL(req).readText() }
        return res.await()
    }
    // TODO: do it normally

    suspend fun MassTranslate(originalWords: List<String>, targetLanguage: String): String {
        var urlParams = ""
        for (word in originalWords) {
            urlParams += word.toLowerCase() + ","
        }
        val req = url + "batch-translate/to/" + URLEncoder.encode(targetLanguage, "UTF-8") + "?words=" + urlParams
        val res = async { URL(req).readText() }.await()
        return res
    }
}


val defaultProvider by lazy {
    ServerTranslator("http://78.155.219.118:7878/")
}





