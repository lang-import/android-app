package lang_import.org.app

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
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
        }
}
