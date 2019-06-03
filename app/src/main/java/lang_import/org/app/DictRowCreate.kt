package lang_import.org.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import org.jetbrains.anko.db.*
import com.google.android.material.textfield.TextInputEditText as EditTxt
import org.jetbrains.anko.startActivity as start


class DictRowCreate : AppCompatActivity() {
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dict_row_create)
        viewManager = LinearLayoutManager(this)
        val dictName = intent.extras.getString("dictName")

        val completeBtn = findViewById<Button>(R.id.dict_complete)
        val ref = findViewById<EditTxt>(R.id.newRef)
        val translate = findViewById<EditTxt>(R.id.newTranslate)

        val allRows = database.use {
            select(dictName).exec { parseList(classParser<DictRowParser>()) }
        }


        completeBtn.setOnClickListener {

            //TODO Text max lenght + not null text checks + id generate

            database.use {
                insert(dictName,
                        "id" to allRows.count()+1,
                        "ref" to ref.getText().toString(),
                        "translate" to translate.getText().toString()
                )
            }

            finish()
            start<DictShowActivity>("dictName" to dictName)
        }
    }

}

