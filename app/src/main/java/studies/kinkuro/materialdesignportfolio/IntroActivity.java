package studies.kinkuro.materialdesignportfolio;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class IntroActivity extends AppCompatActivity {

    ImageView ivLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        ivLogo = findViewById(R.id.iv_intro_logo);

        //StatusBar 숨기기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //ActionBar 숨기기 - 나는 이미 NoActionBar라서 필요없어
//        getSupportActionBar().hide();

        //로고 돌게하기
        Animation ani = AnimationUtils.loadAnimation(this, R.anim.appear_logo);
        ivLogo.startAnimation(ani);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 4000);
    }
}
