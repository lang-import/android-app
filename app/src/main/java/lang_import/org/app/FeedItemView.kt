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

            getImage(imgSrc)

            var text = doc.wholeText().split("\n").get(0).trim()
            if (text.isBlank()) {
                text = feedItem.title
            }

            summaryView.text = text

            setOnClickListener {
                val baggage = Bundle()
                baggage.putString("discript", feedItem.summary)
                baggage.putString("title", feedItem.title)

                Dialog(context).setContentView(R.layout.article_activity)
                val intent = Intent(context, ArticleActivity::class.java)
                intent.putExtras(baggage)
                context.startActivity(intent)
            }
        }

    fun getImage(url: String?) {
        val img = findViewById<ImageView>(R.id.feed_item_image)
        //Log.i("getting image", url)
        Picasso.get().load(url)
                .centerCrop()
                .placeholder(R.drawable.noimg) //dummy need another image
                .fit()
                .into(img, object : Callback {
                    override fun onSuccess() {
                        img.visibility = View.VISIBLE
                        Log.e("image fetcher", url)
                    }

                    override fun onError(e: Exception?) {
                        img.visibility = View.GONE
                        //TODO check this block View.GONE mb not working
                        Log.e("image fetcher", url, e)
                    }

                })

    }
}
