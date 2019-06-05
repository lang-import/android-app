package lang_import.org.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.TextView
import org.jetbrains.anko.db.*
import com.google.android.material.textfield.TextInputEditText as EditTxt
import org.jetbrains.anko.startActivity as start


class AboutActivity : AppCompatActivity() {
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about)
        viewManager = LinearLayoutManager(this)

//        val info = findViewById<TextView>(R.id.textView)


    }

}

