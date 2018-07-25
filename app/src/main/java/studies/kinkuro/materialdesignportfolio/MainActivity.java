package studies.kinkuro.materialdesignportfolio;

import android.*;
import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    Toolbar toolbar;
    ImageView menuNaviBtn;

    Intent intent;

    ImageView[] btns = new ImageView[8];

    AdView adView;
    AdRequest adRequest;

    Toast t = null;

    Animation ani;

    Timer timer = new Timer();
    TimerTask task;

    boolean doubleBack = false;
    final long FINISH_INTERVAL_TIME = 2000;
    long backPressedTime = 0;

    MusicService service;
    boolean isMusicPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //동적퍼미션
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED){
                String[] permissions = {Manifest.permission.INTERNET};
                requestPermissions(permissions, 10);
            }
            if(checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_DENIED){
                String[] permissions = {Manifest.permission.ACCESS_NETWORK_STATE};
                requestPermissions(permissions, 11);
            }
        }

        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigationview);
        navigationView.setItemIconTintList(null);

        adView = findViewById(R.id.adview);
        MobileAds.initialize(this, getResources().getString(R.string.ad_appid));
        adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setButtons();

        menuNaviBtn = findViewById(R.id.btn_menu_nav);
        menuNaviBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(navigationView, true);
            }
        });

        navigationView.setNavigationItemSelectedListener(new MyNavigationListener());
    }

    @Override
    protected void onResume() {
        Intent intent = new Intent(MainActivity.this, MusicService.class);
        if(service == null){
            startService(intent);
            bindService(intent, conn, 0);
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {

        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime){
            super.onBackPressed();
        }else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if(service != null){
            service.stopMusic();
            unbindService(conn);
            service = null;

            Intent intent = new Intent(this, MusicService.class);
            stopService(intent);
        }

        super.onDestroy();
    }

    //버튼 findview 및 리스너달기 및 클릭시 변화하는 메소드(예정)
    public void setButtons(){

        btns[0] = findViewById(R.id.news_cnn);
        btns[1] = findViewById(R.id.news_donga);
        btns[2] = findViewById(R.id.news_hankuk);
        btns[3] = findViewById(R.id.news_hankyoreh);
        btns[4] = findViewById(R.id.news_inews);
        btns[5] = findViewById(R.id.news_kukmin);
        btns[6] = findViewById(R.id.news_nocut);
        btns[7] = findViewById(R.id.news_sbs);

        for(int i = 0 ; i < btns.length; i++){
            btns[i].setOnClickListener(btnClickListener);
            btns[i].setOnTouchListener(btnTouchListener);
        }

    }//setButtons()...

    //클릭리스너 이너클래스...
    View.OnClickListener btnClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View view) {

            intent = new Intent(MainActivity.this, NewsActivity.class);

            String newsUrl = null;
            String newsName = null;
            String newsEncoding = null;
            int newsImg = 0;

            switch(view.getId()){
                case R.id.news_cnn:
                    newsUrl = getResources().getString(R.string.url_cnn);
                    newsName = getResources().getString(R.string.name_cnn);
                    newsEncoding = getResources().getString(R.string.encording_utf8);
                    newsImg = R.drawable.logo_cnn;
                    break;

                case R.id.news_donga:
                    newsUrl = getResources().getString(R.string.url_donga);
                    newsName = getResources().getString(R.string.name_donga);
                    newsEncoding = getResources().getString(R.string.encording_utf8);
                    newsImg = R.drawable.logo_donga;
                    break;

                case R.id.news_hankuk:
                    newsUrl = getResources().getString(R.string.url_hankuk);
                    newsName = getResources().getString(R.string.name_hankuk);
                    newsEncoding = getResources().getString(R.string.encording_euc_kr);
                    newsImg = R.drawable.logo_hankuk;
                    break;

                case R.id.news_hankyoreh:
                    newsUrl = getResources().getString(R.string.url_hankyoreh);
                    newsName = getResources().getString(R.string.name_hankyoreh);
                    newsEncoding = getResources().getString(R.string.encording_utf8);
                    newsImg = R.drawable.logo_hankyoreh;
                    break;

                case R.id.news_inews:
                    newsUrl = getResources().getString(R.string.url_inews);
                    newsName = getResources().getString(R.string.name_inews);
                    newsEncoding = getResources().getString(R.string.encording_euc_kr);
                    newsImg = R.drawable.logo_inews24;
                    break;

                case R.id.news_kukmin:
                    newsUrl = getResources().getString(R.string.url_kukmin);
                    newsName = getResources().getString(R.string.name_kukmin);
                    newsEncoding = getResources().getString(R.string.encording_euc_kr);
                    newsImg = R.drawable.logo_kukmin;
                    break;

                case R.id.news_nocut:
                    newsUrl = getResources().getString(R.string.url_nocut);
                    newsName = getResources().getString(R.string.name_nocut);
                    newsEncoding = getResources().getString(R.string.encording_utf8);
                    newsImg = R.drawable.logo_nocut;
                    break;

                case R.id.news_sbs:
                    newsUrl = getResources().getString(R.string.url_sbs);
                    newsName = getResources().getString(R.string.name_sbs);
                    newsEncoding = getResources().getString(R.string.encording_utf8);
                    newsImg = R.drawable.logo_sbs;
                    break;

            }//switch...

            intent.putExtra("NewsUrl", newsUrl);
            intent.putExtra("NewsName", newsName);
            intent.putExtra("NewsEncoding", newsEncoding);
            intent.putExtra("NewsImg", newsImg);

            /*
            if(t != null){                    t.cancel();                    t = null;                }

            t = Toast.makeText(MainActivity.this, newsName+" 뉴스를 읽어옵니다.", Toast.LENGTH_SHORT);
            t.show();
            */

            ani = AnimationUtils.loadAnimation(MainActivity.this, R.anim.click_main_button);
            view.startAnimation(ani);

            task = new TimerTask() {
                @Override
                public void run() {
                    startActivity(intent);
                }
            };

            timer.schedule(task, 1000);

        }//onClick()...

    };//btnClickListener class...

    //터치리스너 이너클래스(selector가 더 편하대...)
    View.OnTouchListener btnTouchListener = new View.OnTouchListener(){

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
    };//btnTouchListener class...

    //네비게이션뷰 리스너

    class MyNavigationListener implements NavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            if(t != null){ t.cancel();      t = null;}

            switch (item.getItemId()){
                case R.id.item_sound:
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    r.play();
                    t = Toast.makeText(MainActivity.this, "pip sound", Toast.LENGTH_SHORT);
                    break;
                case R.id.item_store:       //playstore 가기
                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                    t = Toast.makeText(MainActivity.this, "go to PlayStore", Toast.LENGTH_SHORT);
                    break;
                case R.id.item_music:
                    Intent intent = new Intent(MainActivity.this, MusicService.class);
                    if(!isMusicPlaying){
                        if(service == null){
                            startService(intent);
                            bindService(intent, conn, 0);
                        }
                        if(service != null)                            service.playMusic();
                        Toast.makeText(MainActivity.this, "Eine_Kleine_Nachtmusik_by_Mozart", Toast.LENGTH_SHORT).show();
                    }else {
                        if(service != null)                            service.pauseMusic();
                        Toast.makeText(MainActivity.this, "음악 일시중지", Toast.LENGTH_SHORT).show();
                    }
                    isMusicPlaying = !isMusicPlaying;
                    break;
                case R.id.item_exit:
                    onBackPressed();
                    return false;
            }
            drawerLayout.closeDrawer(navigationView, true);

            return false;
        }
    }

    //bind 통로
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.ServiceBinder binder = (MusicService.ServiceBinder)iBinder;
            service = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {        }
    };




}
