package lang_import.org.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import org.jetbrains.anko.db.*
import org.jetbrains.anko.toast
import com.google.android.material.textfield.TextInputEditText as EditTxt
import org.jetbrains.anko.startActivity as start

val INFORMERS_DB = "CustomInformers_SrcDataBase"

class CustomInformersAdd : AppCompatActivity() {
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_informer)
        viewManager = LinearLayoutManager(this)


        val informerName = findViewById<EditTxt>(R.id.informerName)
        val urlSetter = findViewById<EditTxt>(R.id.urlSetter)
        val completeBtn = findViewById<Button>(R.id.complete_Button)

        completeBtn.setOnClickListener {
            toast(informerName.getText().toString())
            dbCrt()
            dbInsert(informerName.getText().toString(), urlSetter.getText().toString())
            finish()
        }
    }

    private fun dbCrt() {
        database.use {
            createTable(INFORMERS_DB, true,
                    "name" to TEXT,
                    "url" to TEXT)
        }
    }

    private fun dbInsert(name: String, url: String) {
        database.use {
            insert(INFORMERS_DB,
                    "name" to name,
                    "url" to url
            )
        }
    }

}

