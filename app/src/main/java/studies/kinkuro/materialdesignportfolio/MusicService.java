package studies.kinkuro.materialdesignportfolio;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by alfo6-2 on 2018-04-06.
 */

public class MusicService extends Service {

    MediaPlayer mp;

    void playMusic(){
        if(mp == null){
            mp = MediaPlayer.create(this, R.raw.music);
            mp.setLooping(true);
            mp.setVolume(1,1);
        }
        if(!mp.isPlaying())            mp.start();
    }
    void pauseMusic(){
        if(mp != null && mp.isPlaying())            mp.pause();
    }
    void stopMusic(){
        if(mp != null){
            mp.stop();            mp.release();            mp = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ServiceBinder();
    }

    public class ServiceBinder extends Binder{
        public MusicService getService(){
            return MusicService.this;
        }
    }
}
