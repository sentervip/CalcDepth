package marvoto.com.z1;

import android.app.Notification;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.Toast;
import android.util.Log;
import android.graphics.Bitmap;
import com.marvoto.fat.MeasureDepth;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Handler mHandler;
    private Bitmap mBitmap;
    ImageView imageView;
    Button btnStart,btnStop;
    private int IMG_WIDTH = 640;
    private int IMG_HEIGHT = 480;
    private int g_status = 0;
    private static final String tag = "MAVTJ";
    private int[] data;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==0){
                imageView.setImageBitmap((Bitmap) msg.obj);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        imageView = (ImageView) findViewById(R.id.ImageViewShow);
        mBitmap = Bitmap.createBitmap(IMG_WIDTH, IMG_HEIGHT, Bitmap.Config.ARGB_8888);
        findViewById(R.id.btnStart).setOnClickListener(this);
        findViewById(R.id.btnStop).setOnClickListener(this);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Example of a call to a native method
        //MeasureTest measureTest = new MeasureTest();
        //byte[] data= {(byte)0x71,(byte)0x72,(byte)0x73,(byte)0x74};
        data = new int[640 * 480];
        int val = (0xff<<24)|(0x1<<16)|(0x2<<8)|0xff;
        for ( int i = 0; i < 480; i++)
            for ( int j = 0; j < 640; j++) {
                data[i * 640 + j] = val;
            }
        // measureTest.SaveBmp(data,640,480, "/sdcard/1/test.bmp");

        Thread mRefreshThread = new Thread() {
            @Override
            public void run() {
                for(;;){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (g_status == 1) {
                    MeasureDepth.DrawBitmap(mBitmap,data);
                   // int[] temp = new int[640*480];
                    //for (int i = 0; i < temp.length; i++) {
                    //    temp[i] = Color.argb(255, 1, 2, 3);
                    //}
                   // mBitmap  = Bitmap.createBitmap(temp,640,480,Bitmap.Config.ARGB_8888);

                    Message message = new Message();
                    message.obj=mBitmap;
                    message.what=0;
                    handler.sendMessage(message);
                    Log.i("tag", "run: ");
                }
            }}
        };
        mRefreshThread.start();
    }
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnStart:
                g_status = 1;
                Log.i(tag, "btnStart");
                break;
            case R.id.btnStop:
                g_status = 0;
                Log.i(tag, "btnStop");
                break;
            default:
                break;
        }
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */

}
