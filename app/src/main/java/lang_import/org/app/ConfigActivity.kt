package lang_import.org.app

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.SeekBar
import kotlinx.android.synthetic.main.config_activity.*


class ConfigActivity : AppCompatActivity() {
    private lateinit var viewManager: RecyclerView.LayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val env = PreferenceManager.getDefaultSharedPreferences(this)
        var part = env.getInt("part", 0)
        setContentView(R.layout.config_activity)
        viewManager = LinearLayoutManager(this)

        visualPercent.text = "Процент слов на замену: $part%"

        //TODO add title
        //setTitle(intent.extras.getString("title"))

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

    }

}



