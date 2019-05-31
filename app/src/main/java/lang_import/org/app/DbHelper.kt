import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class DictSqlHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "translateDicts") {

    companion object {
        private var instance: DictSqlHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): DictSqlHelper {
            if (instance == null) {
                instance = DictSqlHelper(ctx.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //db.dropTable("Test", true)
    }

}


