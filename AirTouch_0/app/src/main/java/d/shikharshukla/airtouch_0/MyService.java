package d.shikharshukla.airtouch_0;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class MyService extends Service implements SensorEventListener {

    public SensorManager sm;
    public Sensor proxySensor;
    public int count=0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
       // Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.values[0] < proxySensor.getMaximumRange()){
            //getWindow().getDecorView().setBackgroundColor(Color.RED);
            count = count + 1;

            if(count == 3){
              //Toast.makeText(this, "Congrats !", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), GyroActivity.class);
                startActivity(intent);
                stopSelf();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    //to unregister sensor
    public void onDestroy() {
        //Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
        sm.unregisterListener((SensorEventListener) this);
    }

    @Override
    //register sensor and write onStartCommand not onStart
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        sm=(SensorManager)getSystemService(SENSOR_SERVICE);
        proxySensor=sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sm.registerListener((SensorEventListener) this, proxySensor, SensorManager.SENSOR_DELAY_NORMAL);

        // make service foreground so it will keep working even if app closed

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent bIntent = new Intent(MyService.this, MainActivity.class);
        PendingIntent pbIntent = PendingIntent.getActivity(MyService.this, 0 , bIntent, 0);
        NotificationCompat.Builder bBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Title")
                        .setContentText("Subtitle")
                        .setAutoCancel(true)
                        .setOngoing(true)
                        .setContentIntent(pbIntent);
        Notification barNotif = bBuilder.build();
        this.startForeground(1, barNotif);

        //then you should return sticky
        return Service.START_STICKY;
    }

}