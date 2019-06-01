package lang_import.org.app

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.widget.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.db.dropTable
import org.jetbrains.anko.toast
import android.widget.LinearLayout.LayoutParams as lParams
import android.widget.TableRow.LayoutParams as tParams
import org.jetbrains.anko.startActivity as start


class DictActivity : AppCompatActivity() {
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val env = PreferenceManager.getDefaultSharedPreferences(this)
        val dictsList = env.getStringSet("customDicts", mutableSetOf())
        setContentView(R.layout.dict_activity)
        viewManager = LinearLayoutManager(this)
        val newBtn = findViewById<TextView>(R.id.dict_new_btn)

        //use only in run_once events
        fun refresh() {
            val intent = getIntent()
            overridePendingTransition(0, 0)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
        }


        for (item in dictsList) {
            val choose = Button(this)
            val crtBtn = Button(this)
            val dltBtn = Button(this)
            val subll = LinearLayout(this)
            val layout = findViewById(R.id.custom_dicts_layout) as LinearLayout
            val width = lParams.MATCH_PARENT
            val height = lParams.WRAP_CONTENT
            val params = lParams(width, height)

            choose.setLayoutParams(tParams(width, height, 3f))
            crtBtn.setLayoutParams(tParams(width, height, 1f))
            dltBtn.setLayoutParams(tParams(width, height, 3f))

            choose.text = "use"
            crtBtn.text = item
            dltBtn.text = "Del"

            //Build buttons string
            subll.addView(choose)
            subll.addView(crtBtn)
            subll.addView(dltBtn)
            layout.addView(subll, params)

            crtBtn.setOnClickListener {
                val baggage = Bundle()
                baggage.putString("dictName", item)
                val intent = Intent(this, DictShowActivity::class.java)
                intent.putExtras(baggage)
                startActivity(intent)
            }

            fun forceUpdateEnv() {
                env.edit().putInt("dummy", 0).apply()
                env.edit().putInt("dummy", 1).apply()
            }

            dltBtn.setOnClickListener {
                fun dltDB() {
                    dictsList.remove(item)
                    env.edit().putStringSet("customDicts", dictsList).apply()
                    forceUpdateEnv()
                    database.use { dropTable(item, true) }
                    Log.i("db deleted", item)
                    refresh()
                }

                alert("Вы действительно хотите удалить словарь ${item}", "Внимание") {
                    positiveButton("Да") { dltDB() }
                    negativeButton("Нет") { }
                }.show()
            }

            choose.setOnClickListener {
                val usedDict = env.getString("usedDict", "")
                if (usedDict.trim() != item) {
                    env.edit().putString("usedDict", item.trim()).apply()
                    Log.i("local_dicts","enable local dict  ${item}")
                    toast("Активирован словарь ${item}")
                } else {
                    env.edit().putString("usedDict", "").apply()
                    Log.i("local_dicts","disable local dict ${item}")
                    toast("Словарь не используется")
                }
            }
        }


        //TODO add title
        //setTitle(intent.extras.getString("title"))
        newBtn.setOnClickListener {
            finish()
            start<DictCreateActivity>()
        }
    }


}