package lang_import.org.app


import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast


class DictShowActivity : AppCompatActivity() {
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dictName=intent.extras.getString("dictName")
        setContentView(R.layout.dict_show_activity)
        viewManager = LinearLayoutManager(this)
        val txt = findViewById<TextView>(R.id.textView1)
        txt.text= dictName
        //TODO Generate from table with key-value (prefer use sqlite)
    }

}