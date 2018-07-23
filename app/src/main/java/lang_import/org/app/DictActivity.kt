package lang_import.org.app

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.widget.TextView
import android.widget.LinearLayout


class DictActivity : AppCompatActivity() {
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val env = PreferenceManager.getDefaultSharedPreferences(this)
        val dictsList = env.getStringSet("customDicts", mutableSetOf())
        setContentView(R.layout.dict_activity)
        viewManager = LinearLayoutManager(this)
        val newBtn = findViewById<TextView>(R.id.dict_new_btn)

    //TODO refresh
//        //refresh
//        finish()
//        startActivity(getIntent())

        for (item in dictsList) {
            val btn = Button(this)
            btn.text = item
            val layout = findViewById(R.id.custom_dicts_layout) as LinearLayout
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            layout.addView(btn, params)
            btn.setOnClickListener {
                val baggage = Bundle()
                baggage.putString("dictName", item)
                val intent = Intent(this, DictShowActivity::class.java)
                intent.putExtras(baggage)
                startActivity(intent)
            }
        }



        //TODO add title
        //setTitle(intent.extras.getString("title"))
        newBtn.setOnClickListener {
            val intent = Intent(this, DictCreateActivity::class.java)
            startActivity(intent)
        }
    }


}