package lang_import.org.app
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.TextView
import android.text.method.ScrollingMovementMethod

class SecondActivity : AppCompatActivity() {
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.second_activity)
        viewManager = LinearLayoutManager(this)
        val activityText = findViewById<TextView>(R.id.fullDicriptTextBlock)

        setTitle(intent.extras.getString("title"))
        val txt = intent.extras.getString("discript")

        val readedTxt = clearText(txt)
        activityText.setText(readedTxt)
        activityText.setMovementMethod(ScrollingMovementMethod())
    }

    fun clearText(txt: String):String {
        val lst=mutableListOf("<img src=\"","<a href=\"","\">","<br>","<h2>","<h3>","</h2>",
                "</h3>","</a>","<i>","</i>","<b>","</b>")
        var res = txt
        for (item in lst) {
            res = res.replace(item, " ")
        }
        return res
    }

//    fun importLang(txt: String):String {
//        //TODO env hashMap
//
//        val lst=mutableListOf()
//        var res = txt
//        for (item in lst) {
//            res = res.replace(item, " ")
//        }
//        return res
//    }


}
