package lang_import.org.app

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView

/**
 * TODO: document your custom view class.
 */
class FeedItemView(context: Context?) : LinearLayout(context) {

    var feed: Item = Item()
        set(feedItem) {
            findViewById<TextView>(R.id.feed_item_view_title).text = feedItem.title
            findViewById<TextView>(R.id.feed_item_view_summary).text = feedItem.summary.substring(0, 100) + "..."

            setOnClickListener{
                val baggage = Bundle()
                baggage.putString("discript",feedItem.summary)
                baggage.putString("title",feedItem.title)

                Dialog(context).setContentView(R.layout.article_activity)
                val intent = Intent(context,ArticleActivity::class.java)
                intent.putExtras(baggage)
                context.startActivity(intent)
            }
        }
}
