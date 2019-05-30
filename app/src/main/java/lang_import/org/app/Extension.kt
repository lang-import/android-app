package lang_import.org.app

import android.content.Context
import DictSqlHelper

val Context.database: DictSqlHelper
    get() = DictSqlHelper.getInstance(applicationContext)

class DictRowParserUrl(val name: String, val url: String) {
    fun getLst(): List<String> {
        return listOf(name, url)
    }
}

class DictRowParser(val id: Int, val ref: String, val translate: String) {
    fun getLst(): List<String> {
        return listOf(ref, translate)
    }
}