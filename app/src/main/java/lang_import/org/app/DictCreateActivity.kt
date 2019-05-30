package lang_import.org.app


import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import org.jetbrains.anko.db.*
import org.jetbrains.anko.startActivity as start


class DictCreateActivity : AppCompatActivity() {
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val env = PreferenceManager.getDefaultSharedPreferences(this)
        val dictsList = env.getStringSet("customDicts", mutableSetOf())
        setContentView(R.layout.dict_create_activity)
        viewManager = LinearLayoutManager(this)
        val completeBtn = findViewById<Button>(R.id.dict_complete)
        val txtInput = findViewById<EditText>(R.id.newDictName)

        //TODO this is repeate need create common function
        fun forceUpdateEnv() {
            env.edit().putInt("dummy", 0).apply()
            env.edit().putInt("dummy", 1).apply()
        }

        fun failMsg(txt: String): Boolean {
            val toast = Toast.makeText(getApplicationContext(),
                    txt, Toast.LENGTH_SHORT);
            toast.show()
            return true
        }

        completeBtn.setOnClickListener {
            val resTxt = txtInput.text.trim()
            var fail = false
            if (resTxt.length > 15) {
                fail = failMsg("Имя слишком длинное")
            }
            if (resTxt.length < 1) {
                fail = failMsg("Пустое значение")
            }
            if (resTxt in dictsList) {
                fail = failMsg("Словарь уже существует")
            }
            if (!fail) {
                dictsList.add(resTxt.toString())
                env.edit().putStringSet("customDicts", dictsList).apply()
                forceUpdateEnv()

                database.use {
                    createTable(resTxt.toString(), true,
                            "id" to INTEGER + PRIMARY_KEY + UNIQUE,
                            "ref" to TEXT,
                            "translate" to TEXT)
                }

                finish()
                start<DictActivity>()

            }
        }

    }

}