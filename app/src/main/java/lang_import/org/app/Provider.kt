package lang_import.org.app

import java.util.*

interface TranslateProvider {
    fun Translate(originalLanguage: String, originalWord: String, targetLanguage: String): Optional<String>
}


class HardCodedTranslator : TranslateProvider {
    private val knowledge = mapOf(
            "новости" to "news",
            "страна" to "country",
            "компания" to "company"
    )

    override fun Translate(originalLanguage: String, originalWord: String, targetLanguage: String) = Optional.ofNullable(knowledge.get(originalWord))

}


val defaultProvider by lazy {
    HardCodedTranslator()
}