package lang_import.org.app

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.TextView
import org.jsoup.Jsoup

/**
 * TODO: document your custom view class.
 */
class FeedItemView(context: Context?) : LinearLayout(context) {

    var feed: Item = Item()
        set(feedItem) {
            findViewById<TextView>(R.id.feed_item_view_title).text = feedItem.title
            val summaryView = findViewById<TextView>(R.id.feed_item_view_summary)
            val doc = Jsoup.parse("<html><body>" + feedItem.summary + "</body></html>")
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
}
