package studies.kinkuro.materialdesignportfolio;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class ArticleActivity extends AppCompatActivity {

    WebView webView;

    InterstitialAd interstitialAd = new InterstitialAd(this);
    AdRequest adRequest;

    int adPrequency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        interstitialAd.setAdUnitId(getResources().getString(R.string.ad_interstitial));
        adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);

        adPrequency = (int)(Math.random()*10);

        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                if (adPrequency == 0) {
                    Log.i("tag", "광고성공");
                    interstitialAd.show();
                } else {
                    Log.i("tag", "광고실패");
                }
            }
        });

        webView = findViewById(R.id.webview);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            webView.setTransitionName("Article");
        }

        Intent intent = getIntent();
        String link = intent.getStringExtra("Link");

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl(link);
    }

    @Override
    protected void onResume() {
        adPrequency = (int)(Math.random()*10);
        super.onResume();
    }
}
