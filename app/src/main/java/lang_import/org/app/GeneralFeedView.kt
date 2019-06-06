package lang_import.org.app

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

class FeedAdapter(private val feed: Feed) : RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    class ViewHolder(val feedView: FeedItemView) : RecyclerView.ViewHolder(feedView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val fv = FeedItemView(parent.context)
        View.inflate(parent.context, R.layout.feed_item_view, fv);
        return ViewHolder(fv)
    }

    override fun getItemCount() = feed.channel.item.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.feedView.feed = feed.channel.item[position]
    }
}