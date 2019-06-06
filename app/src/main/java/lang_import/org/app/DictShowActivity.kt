package lang_import.org.app

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.content.ContextCompat
import android.widget.*
import com.google.android.material.button.MaterialButton
import org.jetbrains.anko.db.*
import android.widget.LinearLayout.LayoutParams as lParams
import android.widget.TableRow.LayoutParams as tParams
import org.jetbrains.anko.startActivity as start
import androidx.core.widget.TextViewCompat


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

        val addBtn=generateHollowBtn(this,"Добавить")
        layout.addView(addBtn, lParams(lParams.MATCH_PARENT, lParams.WRAP_CONTENT))
        addBtn.setOnClickListener {
            finish()
            start<DictRowCreate>("dictName" to dictName)
        }

        val rmBtn = generateHollowBtn(this,"delete")
        layout.addView(rmBtn, lParams(lParams.MATCH_PARENT, lParams.WRAP_CONTENT))

        rmBtn.setOnClickListener {
            finish()
            start<DictRowDelete>("dictName" to dictName)
        }

    }

    private fun generateHollowBtn(ctx: Context, name: String): MaterialButton {
        val btn = MaterialButton(ctx)
        TextViewCompat.setTextAppearance(btn, R.style.Widget_MaterialComponents_Button)

        val hard_dark = getResources().getColor(R.color.hard_dark)
        val blue = getResources().getColor(R.color.blue)
        val light_blue = getResources().getColor(R.color.light_blue)
        val hollow =  getResources().getColor(R.color.hollow)

        btn.text = name
        btn.strokeWidth = R.string.ms
        btn.cornerRadius = R.string.bs
        btn.highlightColor = blue
        btn.setTextColor(hard_dark)
        btn.setLinkTextColor(light_blue)
        btn.setStrokeColorResource(R.color.stroke)
        btn.setRippleColorResource(R.color.dark_pointer)
        btn.setBackgroundColor(hollow)

        return btn
    }

}

