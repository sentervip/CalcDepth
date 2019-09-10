package marvoto.com.z1;

import android.Manifest;
import android.app.Notification;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Handler mHandler;
    private Bitmap mBitmap;
    ImageView imageView;
    Button btnStart,btnStop;
    private int depth;
    private int IMG_WIDTH = 320;
    private int IMG_HEIGHT = 480;
    private int g_status = 0;
    private static final String tag = "MAVTJ";
    private int[] data;

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==0){
                //depth = MeasureDepth.DrawBitmap((Bitmap) msg.obj,data);
                //Log.i(tag,"depth="+depth);
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
        isPermissionsAllGranted(107);
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
        data = new int[320 * 480];
        int val = (0xff<<24)|(0x1<<16)|(0x2<<8)|0xff;
        for ( int i = 0; i < 480; i++)
            for ( int j = 0; j < 320; j++) {
                data[i * 320 + j] = val;
            }
        MeasureDepth.ParaSet(3,32);

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
                   // MeasureDepth.DrawBitmap(mBitmap,data);
                   // int[] temp = new int[320*480];
                    //for (int i = 0; i < temp.length; i++) {
                    //    temp[i] = Color.argb(255, 1, 2, 3);
                    //}
                    // mBitmap  = Bitmap.createBitmap(temp,320,480,Bitmap.Config.ARGB_8888);

                    Message message = new Message();
                    message.obj=mBitmap;
                    message.what=0;
                    handler.sendMessage(message);
                    Log.i("tag", "run: ");
                    g_status = 0;
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
//                byte[] data2 = new byte[320 * 480];
//                byte val = 120;
//                for ( int i = 0; i < 480; i++)
//                    for ( int j = 0; j < 320; j++) {
//                        data2[i * 320 + j] = val;
//                    }
                //MeasureDepth.SaveBmp(data2,320,480, "/sdcard/1/test.bmp");
                //MeasureDepth.TestString();
                byte[] img = readFileToByteArray("/sdcard/1/1.txt");
                //Log.i(tag,"length:"+img.length);
                float depth = MeasureDepth.DrawBitmap(mBitmap, img);
                Log.i(tag,"length:"+img.length+",depth:"+depth+" mm");
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
    private void writeAndFlush(String path, byte[] buffer) {
        try {
            FileOutputStream out = new FileOutputStream(path);//指定写到哪个路径中
            FileChannel fileChannel = out.getChannel();
            fileChannel.write(ByteBuffer.wrap(buffer)); //将字节流写入文件中
            fileChannel.force(true);//强制刷新
            fileChannel.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private byte[] readFileToByteArray(String path) {
        File file = new File(path);
        if(!file.exists()) {
            Log.e(tag,"File doesn't exist!");
            return null;
        }

        try {
            FileInputStream in = new FileInputStream(file);
            long inSize = in.getChannel().size();//判断FileInputStream中是否有内容
            if (inSize == 0) {
                Log.d(tag,"The FileInputStream has no content!");
                return null;
            }

            byte[] buffer = new byte[in.available()];//in.available() 表示要读取的文件中的数据长度
            in.read(buffer);  //将文件中的数据读到buffer中
            return buffer;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            /*try {
                //in.close();
            } catch (IOException e) {
                return null;
            }*/
            //或IoUtils.closeQuietly(in);
        }
    }


    //权限查询
    private String[] permissions = new String[]{
            //Manifest.permission.ACCESS_COARSE_LOCATION,//要得到wifi搜索列表，就必须要此权限，android6.0
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            //Manifest.permission.CAMERA,
            //Manifest.permission.READ_PHONE_STATE,
            //Manifest.permission.CALL_PHONE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            //Manifest.permission.BLUETOOTH_ADMIN,
            //Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_NETWORK_STATE,
            //Manifest.permission.WAKE_LOCK,
            Manifest.permission.CHANGE_NETWORK_STATE
            //  Manifest.permission.VIBRATE,
            //  Manifest.permission.GET_ACCOUNTS,
            //   Manifest.permission.RECORD_AUDIO
    };


    //检查权限
    private boolean isPermissionsAllGranted(int questCode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        //获得批量请求但被禁止的权限列表
        List<String> deniedPerms = new ArrayList<String>();
        for (int i = 0; permissions != null && i < permissions.length; i++) {
            if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(permissions[i])) {
                deniedPerms.add(permissions[i]);
            }
        }
        //进行批量请求
        int denyPermNum = deniedPerms.size();
        if (denyPermNum != 0) {
            requestPermissions(deniedPerms.toArray(new String[denyPermNum]), questCode);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        if (requestCode == 107) {
            doPermissionAll(grantResults);
        }
    }

    private void doPermissionAll(int[] grantResults) {
        int grantedPermNum = 0;
        int totalPermissons = permissions.length;
        int totalResults = grantResults.length;
        if (totalPermissons == 0 || totalResults == 0) {
            return;
        }
        Map<String, Integer> permResults = new HashMap<>();
        //初始化Map容器，用于判断哪些权限被授予
        for (String perm : permissions) {
            permResults.put(perm, PackageManager.PERMISSION_DENIED);
        }
        //根据授权的数目和请求授权的数目是否相等来判断是否全部授予权限
        for (int i = 0; i < totalResults; i++) {
            permResults.put(permissions[i], grantResults[i]);
            if (permResults.get(permissions[i]) == PackageManager.PERMISSION_GRANTED) {
                grantedPermNum++;
            }
            Log.d("Debug", "权限：" + permissions[i] + "-->" + grantResults[i]);
        }
        if (grantedPermNum == totalResults) {
            //用于授予全部权限
            Toast.makeText(MainActivity.this, "权限申请成功", Toast.LENGTH_SHORT).show();
            /*if (click==1){
                if (SPUtil.getBoolean(SelectLoginActivity.this, OtherFinals.IS_PHONE_LOGIN, true)) {
                    startActivity(new Intent(SelectLoginActivity.this, LoginPhoneActivity.class));
                } else {
                    startActivity(new Intent(SelectLoginActivity.this, LoginEmailActivity.class));
                }
            }else if (click==2){
                startActivity(new Intent(SelectLoginActivity.this, CameraActivity.class));
            }*/
        } else {
            Toast.makeText(MainActivity.this, "请在应用管理中给予此应用存储和获取位置的权限", Toast.LENGTH_SHORT).show();
            //isPermission=false;
        }
    }


}
