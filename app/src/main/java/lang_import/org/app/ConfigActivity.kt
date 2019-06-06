package lang_import.org.app

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.config_activity.*
import android.content.Intent


class ConfigActivity : AppCompatActivity() {
    private lateinit var viewManager: RecyclerView.LayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val env = PreferenceManager.getDefaultSharedPreferences(this)
        var part = env.getInt("part", 0)
        var targetLang = env.getString("targetLang", "en")
        setContentView(R.layout.config_activity)
        viewManager = LinearLayoutManager(this)

        visualPercent.text = "Процент слов на замену: $part%"


        confPartBar?.setProgress(part)
        confPartBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(partBar: SeekBar, progress: Int, fromUser: Boolean) {
                part = progress
                visualPercent.text = "Процент слов на замену: $part%"
            }
            override fun onStartTrackingTouch(partBar: SeekBar) {}
            override fun onStopTrackingTouch(partBar: SeekBar) {
                env.edit().putInt("part", part).apply()
            }
        })

        visualTargetLang.text = "Целевой язык:"
        val lst=resources.getStringArray(R.array.langs)
        //get saved language if exist
        var defPosition=0
        if (targetLang in lst) {
            defPosition=lst.indexOf(targetLang)
        }
        val spinner = findViewById<Spinner>(R.id.spinner)
        if (spinner != null) {
            spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, lst)
            spinner.setSelection(defPosition)

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>) {}
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    targetLang = lst[position]
                    env.edit().putString("targetLang", targetLang).apply()
                }
            }
        }

        informers.setOnClickListener {
            val intent = Intent(this, InformersMenu::class.java)
            startActivity(intent)
        }

        customInformersAdd.setOnClickListener {
            val intent = Intent(this, CustomInformersAdd::class.java)
            startActivity(intent)
        }

        customInformersRm.setOnClickListener {
            val intent = Intent(this, CustomInformersRm::class.java)
            startActivity(intent)
        }
    }

}



