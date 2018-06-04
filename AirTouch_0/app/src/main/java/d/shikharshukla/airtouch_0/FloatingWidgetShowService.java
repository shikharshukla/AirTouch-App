package d.shikharshukla.airtouch_0;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class FloatingWidgetShowService extends Service{

    WindowManager windowManager;
    View floatingView, collapsedView;
    WindowManager.LayoutParams params;

    private int height = 0;
    private int width = 0;


    public static final String POSITION_BROADCAST = "d.shikharshukla.airtouch_0.position";
    public static final String EXTRA_POS_X = "x_pos";
    public static final String EXTRA_POS_Y = "y_pos";

    int X_Axis, Y_Axis;
    float TouchX, TouchY;

    private BroadcastReceiver gyroBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Action", intent.getAction());
            int posX = intent.getIntExtra(GyroActivity.EXTRA_GYRO_X, 0);
            int posY = intent.getIntExtra(GyroActivity.EXTRA_GYRO_Y, 0);

            Log.d("Positions", "X = " + posX + ", Y = " + posY);

            //  to constraint in screen size, by width
            if (params.x + posX > width) {
                params.x = width;
            } else if (params.x + posX < -width) {
                params.x = -width;
            } else {
                params.x = (params.x + posX);
            }

            //   to constraint by height
            if (params.y + posY > height) {
                params.y = height;
            } else if (params.y + posY < -height) {
                params.y = -height;
            } else {
                params.y = (params.y + posY);
            }

            Log.d("Params", "X = " + params.x + " Y = " + params.y);

            windowManager.updateViewLayout(floatingView, params);

            Intent posIntent = new Intent(POSITION_BROADCAST);
            posIntent.putExtra(EXTRA_POS_X, params.x);
            posIntent.putExtra(EXTRA_POS_Y, params.y);
            sendBroadcast(posIntent);
        }
    };

    public FloatingWidgetShowService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        registerReceiver(gyroBroadcastReceiver, new IntentFilter(GyroActivity.GYRO_BROADCAST));

        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_widget_layout, null);

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        windowManager.addView(floatingView, params);


        collapsedView = floatingView.findViewById(R.id.Layout_Collapsed);

        floatingView.findViewById(R.id.Widget_Close_Icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stopSelf();

            }
        });

        floatingView.findViewById(R.id.MainParentRelativeLayout).setOnTouchListener(new View.OnTouchListener() {

            //to tweak cursor location by manual touch
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        X_Axis = params.x;
                        Y_Axis = params.y;
                        TouchX = event.getRawX();
                        TouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_UP:

                        return true;

                    case MotionEvent.ACTION_MOVE:

                        params.x = X_Axis + (int) (event.getRawX() - TouchX);
                        params.y = Y_Axis + (int) (event.getRawY() - TouchY);
                        windowManager.updateViewLayout(floatingView, params);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        height = intent.getIntExtra("height", 0/*1920/2*/);
        width = intent.getIntExtra("width", 0/*1080/2*/);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(gyroBroadcastReceiver);
        if (floatingView != null) windowManager.removeView(floatingView);
    }
}