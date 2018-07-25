package lang_import.org.app


import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v4.content.ContextCompat
import android.widget.*
import database
import org.jetbrains.anko.db.*


class DictShowActivity : AppCompatActivity() {
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dictName = intent.extras.getString("dictName")
        setContentView(R.layout.dict_show_activity)
        viewManager = LinearLayoutManager(this)
        val txt = findViewById<TextView>(R.id.textView1)
        txt.text = dictName
        val layout = findViewById(R.id.show_dict) as LinearLayout

        fun configureTextView(string: String, item: TextView): TextView {
            //Convert text to one half of table row
            val cell = ContextCompat.getDrawable(this, R.drawable.cell)
            item.text = string
            item.background = cell
            val width = LinearLayout.LayoutParams.MATCH_PARENT
            val height = LinearLayout.LayoutParams.WRAP_CONTENT
            item.setLayoutParams(TableRow.LayoutParams(width, height, 1f))
            return item
        }

        fun createDisplayRow(ref: String, translate: String) {
            val refItem = configureTextView(ref, TextView(this))
            val translateItem = configureTextView(translate, TextView(this))
            val subll = LinearLayout(this)

            subll.addView(refItem)
            subll.addView(translateItem)
            val opts = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layout.addView(subll, opts)
        }

        val allRows = database.use {
            select(dictName).exec { parseList(classParser<DictRowParser>()) }
        }

        for (rowObj in allRows) {
            val rowLst = rowObj.getLst()
            createDisplayRow(rowLst[0], rowLst[1])
        }


        val btn = Button(this)
        btn.text = "добавить"
        layout.addView(btn,
                LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        )

        btn.setOnClickListener {
            val baggage = Bundle()
            baggage.putString("dictName", dictName)
            val intent = Intent(this, DictRowCreate::class.java)
            intent.putExtras(baggage)
            startActivity(intent)
        }

    }

}

class DictRowParser(val id: Int, val ref: String, val translate: String) {
    fun getLst(): List<String> {
        return listOf(ref, translate)
    }
}