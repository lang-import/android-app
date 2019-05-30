package lang_import.org.app

import android.content.Context
import DictSqlHelper

val Context.database: DictSqlHelper
    get() = DictSqlHelper.getInstance(applicationContext)