package studies.kinkuro.materialdesignportfolio;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity {

    Intent intent;

    ArrayList<NewsItem> items = new ArrayList<>();

    RecyclerView recyclerView;
    NewsAdapter adapter;

    AppBarLayout appBarLayout;
    Toolbar toolbar;
    TextView tvToolbar;
    ImageView ivToolbarImage;

    String newsUrl, newsName, newsEncoding;
    int newsImg;

    SwipeRefreshLayout refreshLayout;

    FloatingActionButton fab;
    boolean isFabTurned = false;
    Animation aniFab;
    FloatingActionButton[] fabs = new FloatingActionButton[3];
    Animation aniFabs;

    AdView adView;
    AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        appBarLayout = findViewById(R.id.appbarlayout);
        toolbar = findViewById(R.id.toolbar);
        ivToolbarImage = findViewById(R.id.iv_toolbar_image);
        tvToolbar = findViewById(R.id.tv_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        adView = findViewById(R.id.adview);
        adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        //Collapse 상태에 따라서 tvToolbar 없애기(최대일때만 보이게 하기
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()/5*4) {
                    // AppBar가 아직 많으면 숨기자
                    tvToolbar.setVisibility(View.VISIBLE);
                } else {
                    // 보여주기
                    tvToolbar.setVisibility(View.GONE);
                }
            }
        });

        intent = getIntent();

        recyclerView = findViewById(R.id.recycler);
        adapter = new NewsAdapter(this, items);
        recyclerView.setAdapter(adapter);

        readRSS();

        refreshLayout = findViewById(R.id.swipe);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                items.clear();
                readRSS();
            }
        });

        fab = findViewById(R.id.fab);
        for(int i = 0; i < fabs.length; i++){
            fabs[i] = findViewById(R.id.fab_01+i);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFabsStatus();
            }
        });

    }

    public void readRSS(){

        newsUrl = intent.getStringExtra("NewsUrl");
        newsName = intent.getStringExtra("NewsName");
        newsEncoding = intent.getStringExtra("NewsEncoding");
        newsImg = intent.getIntExtra("NewsImg", R.drawable.no_image);

        ivToolbarImage.setImageResource(newsImg);
        tvToolbar.setText(newsName);

        try {
            URL url = new URL(newsUrl);

            ParsingRss parsingRss = new ParsingRss();
            parsingRss.execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    class ParsingRss extends AsyncTask<URL, Void, String>{

        @Override
        protected String doInBackground(URL... urls) {

            URL url = urls[0];

            try {

                InputStream is = url.openStream();

                XmlPullParser xpp = XmlPullParserFactory.newInstance().newPullParser();

                //인코딩 타입도 넘겨받음
                xpp.setInput(is, newsEncoding);

                int eventType = xpp.next();

                NewsItem item = null;
                String tagName = null;

                while(eventType != XmlPullParser.END_DOCUMENT){

                    switch (eventType){

                        case XmlPullParser.START_TAG:

                            tagName = xpp.getName();

                            if(tagName.equals("item")){
                                item = new NewsItem();
                                item.setImgNews(newsImg);
                            }else if(tagName.equals("title")){
                                xpp.next();
                                if(item != null){
                                    item.setTitle(Html.fromHtml(xpp.getText()).toString());
                                }
                            }else if(tagName.equals("link")){
                                xpp.next();
                                if(item != null){
                                    item.setLink(xpp.getText());
                                }
                            }else if(tagName.equals("description")){
                                xpp.next();
                                if(item != null){
                                    item.setDesc(Html.fromHtml(xpp.getText()).toString());
                                }
                            }else if(tagName.equals("pubDate")){
                                xpp.next();
                                if(item != null){
                                    item.setPubDate(Html.fromHtml(xpp.getText()).toString());
                                }
                            }else if(tagName.equals("author")){
                                xpp.next();
                                if(item != null){
                                    if(newsName.equals(getResources().getString(R.string.name_sbs))){
                                        item.setAuthor(xpp.getText());
                                    }else {
                                        item.setAuthor(Html.fromHtml(xpp.getText()).toString());
                                    }
                                }
                            }else if(tagName.equals("category")){
                                xpp.next();
                                if(item != null){
                                    item.setCategory(Html.fromHtml(xpp.getText()).toString());
                                }
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            tagName = xpp.getName();
                            if(tagName.equals("item")){
                                items.add(item);
                                item = null;

                                publishProgress();
                            }
                            break;
                    }

                    eventType = xpp.next();

                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

            return "load complete";

        }//doInBackground()...

        @Override
        protected void onProgressUpdate(Void... values) {

            adapter.notifyDataSetChanged();

            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {

            Snackbar snackbar;
            snackbar = Snackbar.make(recyclerView, s, Snackbar.LENGTH_SHORT);
            View snackView = snackbar.getView();
            snackView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            snackbar.show();

            refreshLayout.setRefreshing(false);

            super.onPostExecute(s);
        }
    }

    public void clickFabs(View v){
        switch(v.getId()){
            case R.id.fab_01:
                items.clear();
                readRSS();
                break;
            case R.id.fab_02:
                finish();
                break;
            case R.id.fab_03:
                Toast.makeText(this, "검색 기능은 개발중입니다.", Toast.LENGTH_SHORT).show();
                break;
        }

        changeFabsStatus();

    }//clickFabs()...

    void changeFabsStatus(){

        if(!isFabTurned){
            aniFab = AnimationUtils.loadAnimation(this, R.anim.rotate_fab_turned);
            fab.startAnimation(aniFab);
            aniFabs = AnimationUtils.loadAnimation(this, R.anim.translate_fab_visible);
            for(int i =0 ; i < fabs.length; i++){
                fabs[i].setVisibility(View.VISIBLE);
                fabs[i].startAnimation(aniFabs);
            }
            fab.setImageResource(R.drawable.ic_fab_hide);
        }else {
            aniFab = AnimationUtils.loadAnimation(this, R.anim.rotate_fab_unturned);
            fab.startAnimation(aniFab);
            aniFabs = AnimationUtils.loadAnimation(this, R.anim.translate_fab_gone);
            for(int i =0 ; i < fabs.length; i++){
                fabs[i].startAnimation(aniFabs);
                fabs[i].setVisibility(View.GONE);
            }
            fab.setImageResource(R.drawable.ic_fab_show);
        }

        isFabTurned = !isFabTurned;

    }//changeFabsStatus()...

}
