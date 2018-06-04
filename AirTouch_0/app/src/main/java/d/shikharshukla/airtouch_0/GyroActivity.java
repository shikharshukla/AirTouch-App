package d.shikharshukla.airtouch_0;

import android.Manifest;
import android.app.Instrumentation;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.SystemClock;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class GyroActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private SensorEventListener sensorEventListener;

    TextView textView1,textView2,textView3;

    public static final int SYSTEM_ALERT_WINDOW_PERMISSION = 7;
    public static final String GYRO_BROADCAST = "d.shikharshukla.airtouch_0.gyro";
    public static final String EXTRA_GYRO_X = "x_pos";
    public static final String EXTRA_GYRO_Y = "y_pos";
    private static int HALF_HEIGHT = 0;
    private static int HALF_WIDTH = 0;
    Instrumentation m_Instrumentation = new Instrumentation();
    private int POS_X = 0;
    private int POS_Y = 0;
    private int height = 0;
    private int width = 0;
    private String KEY_NAME = "somekey";

    int a=0,b=0;

    private BroadcastReceiver mPositionBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            POS_X = HALF_WIDTH + intent.getIntExtra(FloatingWidgetShowService.EXTRA_POS_X, 0);
            POS_Y = HALF_HEIGHT + intent.getIntExtra(FloatingWidgetShowService.EXTRA_POS_Y, 0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyro);
        calculateDisplayMetrics();
        setUpFingerPrint();

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        gyroscopeSensor= sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        textView1=(TextView)findViewById(R.id.textView1);
        textView2=(TextView)findViewById(R.id.textView2);
        textView3=(TextView)findViewById(R.id.textView3);

//        findViewById(R.id.startService).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                initiateClick();
//            }
//        });


//        findViewById(R.id.button).setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                Toast.makeText(GyroActivity.this, "Long", Toast.LENGTH_SHORT).show();
//                return true;
//            }
//        });

        if(gyroscopeSensor == null){
            Toast.makeText(this, "Gyroscope sensor is not available !",Toast.LENGTH_LONG).show();
            finish();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            RuntimePermissionForUser();
        }

        Intent intent = new Intent(GyroActivity.this, FloatingWidgetShowService.class);
        intent.putExtra("height", height);
        intent.putExtra("width", width);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

            startService(intent);

        } else if (Settings.canDrawOverlays(GyroActivity.this)) {

            startService(intent);

        } else {
            RuntimePermissionForUser();

            Toast.makeText(GyroActivity.this, "System Alert Window Permission Is Required For Floating Widget.", Toast.LENGTH_LONG).show();
        }

        sensorEventListener=new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                Intent intent = new Intent();
                intent.setAction(GYRO_BROADCAST);
                intent.putExtra(EXTRA_GYRO_X, (int)(500 * sensorEvent.values[1]));
                intent.putExtra(EXTRA_GYRO_Y, (int)(500 * sensorEvent.values[0]));
                sendBroadcast(intent);

                if(sensorEvent.values[2] > 0.5f){
                    getWindow().getDecorView().setBackgroundColor(Color.YELLOW);

                }

                else if(sensorEvent.values[2]< -0.5f){
                    getWindow().getDecorView().setBackgroundColor(Color.GRAY);
                }

                for(int i = 0; i < 3; i++) {
                    sensorEvent.values[i] = (float)(Math.toDegrees(sensorEvent.values[i]));
                }

                if(sensorEvent.values[0]>5) {

                    textView1.setText("" + sensorEvent.values[0]);
                }
                if(sensorEvent.values[1]>5) {

                    textView2.setText("" + sensorEvent.values[1]);
                }
                if(sensorEvent.values[2]>5) {

                    textView3.setText("" + sensorEvent.values[2]);
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };


        //to do a demo

        final Button teamA = (Button)findViewById(R.id.button);
        final Button teamB = (Button)findViewById(R.id.button2);
        final Switch sButton = (Switch) findViewById(R.id.switch1);
        final TextView tView =(TextView)findViewById(R.id.textView);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(GyroActivity.this, "Madrid Scores", Toast.LENGTH_SHORT).show();
                a=a+1;
                teamA.setText(""+ (a) +"");
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(GyroActivity.this, "Barca Scores", Toast.LENGTH_SHORT).show();
                b=b+1;
                teamB.setText(""+ (b) +"");
            }
        });

        sButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean on){
                if(on)
                {
                    //Do something when Switch button is on/checked
                    tView.setText("Hello World!");
                }
                else
                {
                    //Do something when Switch is off/unchecked
                    tView.setText("ByeBye World!");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener,gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        registerReceiver(mPositionBroadcastReceiver, new IntentFilter(FloatingWidgetShowService.POSITION_BROADCAST));
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
        unregisterReceiver(mPositionBroadcastReceiver);
    }

    public void RuntimePermissionForUser() {

        Intent PermissionIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));

        startActivityForResult(PermissionIntent, SYSTEM_ALERT_WINDOW_PERMISSION);
    }

    void calculateDisplayMetrics() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        HALF_HEIGHT = displayMetrics.heightPixels / 2;
        HALF_WIDTH = displayMetrics.widthPixels / 2;

        Log.d("Metrics", "H H = " + HALF_HEIGHT + " H W = " + HALF_WIDTH);
    }

    private void initiateClick() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                int x = POS_X ;
                int y = POS_Y-20 ;

                Log.d("Clicked", "X = " + POS_X + ", Y = " + POS_Y);

                m_Instrumentation.sendPointerSync(MotionEvent.obtain(
                        SystemClock.uptimeMillis(), SystemClock.uptimeMillis() + 200,
                        MotionEvent.ACTION_DOWN, x, y, 0));
                m_Instrumentation.sendPointerSync(MotionEvent.obtain(
                        SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_UP, x, y, 0));
            }
        }.start();
    }

    private void setUpFingerPrint() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        if(!fingerprintManager.isHardwareDetected()){
            Log.e("Hardware", "Fingerprint Hardware not Detected");
            return ;
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED){
            Log.e("Permission", "Fingerprint permission rejected");
            return;
        }

        if(!keyguardManager.isKeyguardSecure()){
            Log.e("Keyguard","Keyguard not enabled");
            return;
        }

        KeyStore keyStore;

        try{
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        }catch(Exception e){
            Log.e("KeyStore", e.getMessage());
            return;
        }

        KeyGenerator keyGenerator;

        try{
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

        }catch(Exception e){
            Log.e("KeyGenerator",e.getMessage());
            return;
        }

        try{
            keyStore.load(null);
            keyGenerator.init(
                    new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                            .setUserAuthenticationRequired(true)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                            .build()        );

            keyGenerator.generateKey();

        }catch(Exception e){
            Log.e("Generating keys", e.getMessage());
            return;
        }

        Cipher cipher;

        try{
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + '/' + KeyProperties.BLOCK_MODE_CBC + '/' + KeyProperties.ENCRYPTION_PADDING_PKCS7);

        }catch(Exception e){
            Log.e("Cipher", e.getMessage());
            return;
        }

        try{
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);

        }catch(Exception e){
            Log.e("Secret key", e.getMessage());
            return;
        }

        FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);

        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, new AuthenticationHandler(this), null);
    }


    class AuthenticationHandler extends FingerprintManager.AuthenticationCallback {

        private GyroActivity mainActivity;
        public AuthenticationHandler(GyroActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);

          //  Toast.makeText(mainActivity, "Auth Error" + errString, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            super.onAuthenticationHelp(helpCode, helpString);

           // Toast.makeText(mainActivity, "Auth Help" + helpString, Toast.LENGTH_SHORT).show();
            initiateClick();
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);

            Toast.makeText(mainActivity, "Auth Succeeded", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();

            Toast.makeText(mainActivity, "Auth Failed", Toast.LENGTH_SHORT).show();
        }
    }
}
