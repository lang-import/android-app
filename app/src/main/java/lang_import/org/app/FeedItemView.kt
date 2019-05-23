package lang_import.org.app

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import org.jsoup.Jsoup
import java.lang.Exception


/**
 * TODO: document your custom view class.
 */
class FeedItemView(context: Context?) : LinearLayout(context) {


    var feed: Item = Item()
        set(feedItem) {
            findViewById<TextView>(R.id.feed_item_view_title).text = feedItem.title
            val summaryView = findViewById<TextView>(R.id.feed_item_view_summary)
            val doc = Jsoup.parse("<html><body>" + feedItem.summary + "</body></html>")
            val imgUrl = doc.getElementsByTag("img")
            val imgSrc = imgUrl.map { it.attr("src") }.firstOrNull() { it.isNotEmpty() }

            getImage(imgSrc, findViewById(R.id.feed_item_image))
            getImage(feedItem.logo, findViewById(R.id.feed_item_logo), true)

            var text = doc.wholeText().split("\n").get(0).trim()
            if (text.isBlank()) {
                text = feedItem.title
            }

            summaryView.text = text

            setOnClickListener {
                val baggage = Bundle()
                baggage.putString("discript", feedItem.summary)
                baggage.putString("title", feedItem.title)
                baggage.putString("link", feedItem.link)

                Dialog(context).setContentView(R.layout.article_activity)
                val intent = Intent(context, ArticleActivity::class.java)
                intent.putExtras(baggage)
                context.startActivity(intent)
            }
            field = feedItem
        }

    fun getImage(url: String?, v: ImageView, logo: Boolean = false) {
        var imgUrl: String? = null
        if (url.toString().trim() != "") {
            imgUrl = url
        }
        var rootPicasso = Picasso.get().load(imgUrl).centerCrop()
        if (logo) rootPicasso = Picasso.get().load(imgUrl).centerInside()
        rootPicasso.placeholder(R.drawable.noimg) //dummy need another image
                .fit()
                .into(v, object : Callback {
                    override fun onSuccess() {
                        v.visibility = View.VISIBLE
                        Log.i("image fetcher", imgUrl)
                    }

                    override fun onError(e: Exception?) {
                        v.visibility = View.GONE
                        Log.e("image fetcher", imgUrl, e)
                    }

                })

    }
}
