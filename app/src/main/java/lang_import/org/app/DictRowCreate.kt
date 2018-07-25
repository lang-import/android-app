package lang_import.org.app

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import database
import org.jetbrains.anko.db.*


class DictRowCreate : AppCompatActivity() {
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dict_row_create)
        viewManager = LinearLayoutManager(this)
        val dictName = intent.extras.getString("dictName")
        val completeBtn = findViewById<Button>(R.id.dict_complete)
        val ref = findViewById<android.support.design.widget.TextInputEditText>(R.id.newRef)
        val translate = findViewById<android.support.design.widget.TextInputEditText>(R.id.newTranslate)

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


            val baggage = Bundle()
            baggage.putString("dictName", dictName)
            val intent = Intent(this, DictShowActivity::class.java)
            intent.putExtras(baggage)
            startActivity(intent)
        }
    }

}

