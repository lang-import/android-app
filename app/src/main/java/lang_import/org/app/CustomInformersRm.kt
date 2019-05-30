package lang_import.org.app

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Button
import org.jetbrains.anko.db.*
import org.jetbrains.anko.toast
import java.lang.Exception
import android.support.design.widget.TextInputEditText as EditTxt
import org.jetbrains.anko.startActivity as start


class CustomInformersRm : AppCompatActivity() {
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_informer_rm)
        viewManager = LinearLayoutManager(this)
        val env = PreferenceManager.getDefaultSharedPreferences(this)
        val envInformers = env.getStringSet("informers", mutableSetOf())

        fun forceUpdateEnv() {
            env.edit().putInt("dummy", 0).apply()
            env.edit().putInt("dummy", 1).apply()
        }

        val informerName = findViewById<EditTxt>(R.id.informerName)
        val completeBtn = findViewById<Button>(R.id.complete_Button)

        completeBtn.setOnClickListener {
            toast(informerName.getText().toString())
            dbCrt()
            val name = informerName.getText().toString()
            envInformers.remove(name)
            env.edit().putStringSet("informers", envInformers).apply()
            forceUpdateEnv()
            dbRemove(name)
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

    private fun dbRemove(name: String) {
        try {
            database.use {
                delete(INFORMERS_DB, "name = {KEY}", "KEY" to name)
            }
        } catch (e: Exception) {
            Log.e("DB_RM_ERROR", e.toString())
        }
    }

}

