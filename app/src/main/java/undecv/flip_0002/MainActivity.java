package undecv.flip_0002;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    static TextView Cons01, Cons01_T;
    Button btnOpenAudio, btnOpenLRC;
    ImageButton btnPlay, btnPause;
    Button btnStop;
    TextView tvAudioFile, tvLRCFile;
    TextView LineB1, Line00, Line01;
    SeekBar sbPlay;

    File fileAudio, fileLRC;
    private Timer timer;
    LRC lrc;
    MediaPlayer mediaPlayer;
    boolean isPause = false;

    int lrc_p = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Cons01 = (TextView)findViewById(R.id.Cons_01);
        Cons01.setText("");

        Cons01_T = (TextView)findViewById(R.id.textView_UI_Statue);
        Cons01_T.setOnClickListener(tvListener_01);

        tvAudioFile = (TextView)findViewById(R.id.textView_AudioFile);
        tvLRCFile = (TextView)findViewById(R.id.textView_LRCFile);

        LineB1 = (TextView)findViewById(R.id.textView_LRC_0);
        Line00 = (TextView)findViewById(R.id.textView_LRC_1);
        Line01 = (TextView)findViewById(R.id.textView_LRC_2);

        sbPlay = (SeekBar)findViewById(R.id.seekBar_Play);
        sbPlay.setOnSeekBarChangeListener(sbListener_01);

        btnOpenAudio = (Button)findViewById(R.id.button_OpenAudio);
        btnOpenLRC = (Button)findViewById(R.id.button_OpenLRC);

        btnPlay = (ImageButton)findViewById(R.id.button_Play);
        btnPause = (ImageButton)findViewById(R.id.button_Pause);
        btnStop = (Button)findViewById(R.id.button_Stop);

        btnOpenAudio.setOnClickListener(btnListener_01);
        btnOpenLRC.setOnClickListener(btnListener_01);
        btnPlay.setOnClickListener(btnListener_01);
        btnPause.setOnClickListener(btnListener_01);
        btnStop.setOnClickListener(btnListener_01);

        mediaPlayer = new MediaPlayer();

        timer = new Timer(true);
        timer.schedule(timerTask,0,300);

        CheckSDPermission();

    }

    /**
     * TextView 按下動作 監聽事件
     */
    private TextView.OnClickListener tvListener_01 = new TextView.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.textView_UI_Statue: // textView_UI_Statue 控制清除 Cons01（狀態顯示）
                    Cons01.setText("");
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Button 按下動作 監聽事件
     */
    private Button.OnClickListener btnListener_01 = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.button_OpenAudio: // 打開音樂文件
                    if (NotSDPermission()){
                        Toast.makeText(MainActivity.this, R.string.ERR_Permission, Toast.LENGTH_SHORT).show();
                    } else {
                        openFileIntent("audio/*", 1);
                    }
                    break;
                case R.id.button_OpenLRC: // 打開歌詞文件
                    if (NotSDPermission()){
                        Toast.makeText(MainActivity.this, R.string.ERR_Permission, Toast.LENGTH_SHORT).show();
                    } else {
                        openFileIntent("*/*", 2);
                    }
                    break;
                case R.id.button_Play: // 播放
                    if (fileAudio == null){
                        ConsMsg("NULL file.");
                        return;
                    } else if(isPause){
                        mediaPlayer.start();
                        isPause = false;
                    } else if (mediaPlayer.isPlaying()){

                    } else {
                        playSong(fileAudio.getPath());
                    }
                    break;
                case R.id.button_Pause: // 暫停
                        if(isPause == false){
                            mediaPlayer.pause();
                            isPause = true;
                            ConsMsg("mediaPlayer pause.");
                        }
                    break;
                case R.id.button_Stop: // 停止
                    musicStop();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * SeekBar 進度條監聽事件，跳轉到該處
     */
    SeekBar.OnSeekBarChangeListener sbListener_01 = new  SeekBar.OnSeekBarChangeListener(){
        boolean isSeekBarChanging = false;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = true;
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = false;
            if (mediaPlayer!=null) mediaPlayer.seekTo(seekBar.getProgress());
        }
    };

    /**
     * 音樂播放的動作
     */
    private void playSong(String path){
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();

            mediaPlayer.start();
            ConsMsg("mediaPlayer start.");
            sbPlay.setMax(mediaPlayer.getDuration());
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    ConsMsg("mediaPlayer compiled.");
                }
            });
        } catch (IOException e) {
            ConsMsg("Play song fail.");
            e.printStackTrace();
        }
        lrc_p = 0;

    }

    /**
     * 音樂停止的動作
     */
    private void musicStop(){
        if (mediaPlayer == null) return;
        if(mediaPlayer.isPlaying()){
            mediaPlayer.reset();
            ConsMsg("mediaPlayer stop.");
        }
    }

    /**
     * Timer 事件，同步進度條與歌詞
     */
    private TimerTask timerTask = new TimerTask(){
        @Override
        public void run() {
            if (mediaPlayer.isPlaying()){
                sbPlay.setProgress(mediaPlayer.getCurrentPosition());
                Message msg = new Message();
                msg.what = mediaPlayer.getCurrentPosition();
                handler.sendMessage(msg);
            }
        }
    };

    /**
     * Timer 處理 Handler 同步進度條與歌詞
     * 由於不能跨 Thread 控制 View
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (lrc != null){
                LineB1.setText(lrc.getLine(msg.what,-1));
                Line00.setText(lrc.getLine(msg.what,0));
                Line01.setText(lrc.getLine(msg.what,1));
            }
        }
    };

    /**
     * 打開文件的方法，調用系統的 ACTION_GET_CONTENT
     *
     * @param MIME
     * @param requestCode
     */
    private void openFileIntent(String MIME, int requestCode){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(MIME);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, ""), requestCode);
    }

    /**
     * 接收系統的 ACTION_GET_CONTENT 打開文件方法的回傳
     * 不同 程式有不同 URI 的回傳，使用網路上找的程式碼處理
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data == null) return;
        Uri uri = data.getData();
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case 1:
                    ConsMsg("OpenAudio:");
                    ConsMsg("URI: " + MagicFileChooser.getAbsolutePathFromUri(this, uri));
                    fileAudio = new File(MagicFileChooser.getAbsolutePathFromUri(this, uri));
                    ConsMsg("File Open: " + fileAudio.getPath());
                    tvAudioFile.setText(fileAudio.getName());
                    break;
                case 2:
                    ConsMsg("OpenLRC:");
                    ConsMsg("URI: " + MagicFileChooser.getAbsolutePathFromUri(this, uri));
                    fileLRC = new File(MagicFileChooser.getAbsolutePathFromUri(this, uri));
                    ConsMsg("File Open: " + fileLRC.getAbsolutePath());
                    ConsMsg("File Open: " + fileLRC.getPath());
                    tvLRCFile.setText(fileLRC.getName());

                    try {
                        lrc = flip.Parse(fileLRC);
                        lrc.Show_C(10);
                    } catch (IOException e) {
                        ConsMsg("Parse Fail.");
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    /**
     * 檢查 SD 卡存取權限。
     */
    protected boolean NotSDPermission(){
        return ActivityCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 檢查 SD 卡存取權限。
     */
    public void CheckSDPermission(){
        if (NotSDPermission()){
                Toast.makeText(MainActivity.this, R.string.ERR_Permission, Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                        android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
    }

    /**
     * 顯示訊息于 控制台 Cons01。
     */
    public static void ConsMsg(String Msg){
        Cons01.append(Msg + "\n");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            musicStop();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
