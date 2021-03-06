package lang_import.org.app

import android.content.Context
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*
import java.io.File

val sampleText = File("src/test/resources/habr.rss.xml").readText()


class FeedReaderTest {

    @Test
    fun parseContent() {
        val fp = FeedParser()
        val feed = runBlocking { fp.parseContent(sampleText) }
    }
}