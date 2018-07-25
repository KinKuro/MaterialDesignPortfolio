package studies.kinkuro.materialdesignportfolio;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by alfo6-2 on 2018-03-20.
 */

public class NewsAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<NewsItem> items;

    public NewsAdapter(Context context, ArrayList<NewsItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.item_news_recycler, parent, false);
        VH holder = new VH(itemView);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        VH vh = (VH)holder;
        NewsItem item = items.get(position);

        vh.tvTitle.setText(item.getTitle());
        vh.tvCategory.setText(item.getCategory());
        vh.tvAuthor.setText(item.getAuthor());
        vh.tvDesc.setText(item.getDesc());
        vh.tvPubDate.setText(item.getPubDate());

        Glide.with(context).load(item.getImgNews()).into(vh.imgNews);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class VH extends RecyclerView.ViewHolder{

        TextView tvTitle, tvCategory, tvAuthor, tvDesc, tvPubDate;
        ImageView imgNews;

        public VH(final View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            tvPubDate = itemView.findViewById(R.id.tv_pubDate);
            imgNews = itemView.findViewById(R.id.iv_news);

            //클릭리스너
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent articleIntent = new Intent(context, ArticleActivity.class);

                    articleIntent.putExtra("Link", items.get(getLayoutPosition()).getLink());

                    Log.i("link", items.get(getLayoutPosition()).getLink());

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((NewsActivity)context, new Pair<View, String>(itemView, "Article"));
                        context.startActivity(articleIntent, options.toBundle());
                    }else{
                        context.startActivity(articleIntent);
                    }
                }
            });

            //터치리스너
            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {

                    if(event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE){
                        view.setBackgroundResource(R.drawable.frame_button_clicked);
                    }
                    if(event.getAction() == MotionEvent.ACTION_UP ||
                            event.getAction() == MotionEvent.ACTION_OUTSIDE ||
                            event.getAction() == MotionEvent.ACTION_BUTTON_RELEASE ||
                            event.getAction() == MotionEvent.ACTION_CANCEL) {
                        view.setBackgroundResource(R.drawable.frame_button);
                    }

                    return false;
                }
            });

        }
    }//VH class..
}
