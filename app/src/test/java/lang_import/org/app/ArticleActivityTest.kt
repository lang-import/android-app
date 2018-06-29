package lang_import.org.app

import org.junit.Test

import org.junit.Assert.*

class ArticleActivityTest {

    @Test
    fun fixImg() {
        val txt = "aaaa <img src=\"bbb.jpg\"  sxyz=1123/> llll"
        println(lang_import.org.app.fixImg(txt))
    }
}