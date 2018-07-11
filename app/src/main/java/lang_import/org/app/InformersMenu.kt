package lang_import.org.app

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.*
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.informers_menu.*


class InformersMenu : AppCompatActivity() {
    private lateinit var viewManager: RecyclerView.LayoutManager

    var names = arrayOf("HABR", "Yandex.science", "mail.ru")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val env = PreferenceManager.getDefaultSharedPreferences(this)
        val currentInformersList = env.getStringSet("informers", mutableSetOf())

        setContentView(R.layout.informers_menu)
        viewManager = LinearLayoutManager(this)
        val adapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_multiple_choice, names)
        informersList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE)
        informersList.setAdapter(adapter)

        //view checkbox
        val isSelected = { x: Int -> informersList.getCheckedItemPositions().get(x) }
        //get item from ListView by position
        val getItem = { x: Int -> informersList.getItemAtPosition(x).toString() }

        //preset checkbox from env
        for (i in 0..informersList.getCount()-1) {
            if (getItem(i) in currentInformersList) {
                informersList.setItemChecked(i,true)
            }
        }

        //helper func() for hard resave SharedPreferences (update StringSet)
        fun forceUpdateEnv(){
            env.edit().putInt("dummy", 0).apply()
            env.edit().putInt("dummy", 1).apply()
        }

        informersList.setOnItemClickListener { adapterView, view, i, len ->
            if (isSelected(i)) {
                currentInformersList.add(informersList.getItemAtPosition(i).toString())
            } else {
                if (getItem(i) in currentInformersList) {
                    currentInformersList.remove(getItem(i))
                }
            }
            env.edit().putStringSet("informers", currentInformersList).apply()
            env.edit().putBoolean("needRefresh",true).apply()
            forceUpdateEnv()


        }
        //TODO add title
        //setTitle(intent.extras.getString("title"))
    }

}



