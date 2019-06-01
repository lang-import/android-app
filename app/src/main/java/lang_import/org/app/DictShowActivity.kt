package lang_import.org.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.content.ContextCompat
import android.widget.*
import org.jetbrains.anko.db.*
import android.widget.LinearLayout.LayoutParams as lParams
import android.widget.TableRow.LayoutParams as tParams
import org.jetbrains.anko.startActivity as start


class DictShowActivity : AppCompatActivity() {
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dictName = intent.extras.getString("dictName")
        setContentView(R.layout.dict_show_activity)
        viewManager = LinearLayoutManager(this)
        val dbName = findViewById<TextView>(R.id.db_name)
        dbName.text = "База данных: ${dictName}"
        val layout = findViewById(R.id.show_dict) as LinearLayout

        fun configureTextView(string: String, item: TextView): TextView {
            //Convert text to one half of table row
            val cell = ContextCompat.getDrawable(this, R.drawable.cell)
            item.text = string
            item.background = cell
            val width = lParams.MATCH_PARENT
            val height = lParams.WRAP_CONTENT
            item.setLayoutParams(tParams(width, height, 1f))
            return item
        }

        fun createDisplayRow(ref: String, translate: String) {
            val refItem = configureTextView(ref, TextView(this))
            val translateItem = configureTextView(translate, TextView(this))
            val subll = LinearLayout(this)
            val opts = lParams(lParams.MATCH_PARENT, lParams.WRAP_CONTENT)

            subll.addView(refItem)
            subll.addView(translateItem)

            layout.addView(subll, opts)
        }

        val allRows = database.use {
            select(dictName).exec { parseList(classParser<DictRowParser>()) }
        }

        for (rowObj in allRows) {
            val rowLst = rowObj.getLst()
            createDisplayRow(rowLst[0], rowLst[1])
        }


        val addBtn = Button(this)
        addBtn.text = "добавить"
        layout.addView(addBtn, lParams(lParams.MATCH_PARENT, lParams.WRAP_CONTENT))

        addBtn.setOnClickListener {
            finish()
            start<DictRowCreate>("dictName" to dictName)
        }

        val rmBtn = Button(this)
        rmBtn.text = "delete"
        layout.addView(rmBtn, lParams(lParams.MATCH_PARENT, lParams.WRAP_CONTENT))

        rmBtn.setOnClickListener {
            finish()
            start<DictRowDelete>("dictName" to dictName)
        }

    }

}


