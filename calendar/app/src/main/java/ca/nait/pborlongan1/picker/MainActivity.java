package ca.nait.pborlongan1.picker;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    Button btnTest, btnTest2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTest = (Button)findViewById(R.id.test);
        btnTest.setOnClickListener(this);
        btnTest2 = (Button)findViewById(R.id.test2);
        btnTest2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
      switch(view.getId())
      {
          case R.id.test:
          {
              scheduleNotification(getNotification("This is a notification 1"), 5000, 1);
              Toast.makeText(this, "Notification Set", Toast.LENGTH_LONG).show();
              break;
          }
          case R.id.test2:
          {
              scheduleNotification(getNotification("This is a notification 2"), 10000, 2);
              Toast.makeText(this, "Notification Set", Toast.LENGTH_LONG).show();
              break;
          }
      }
    }

    private void scheduleNotification(Notification notification, int delay, int id)
    {
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = System.currentTimeMillis() + delay;

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification getNotification(String content)
    {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("This is a reminder lol");
        builder.setContentText(content);
        builder.setSmallIcon(android.R.drawable.ic_lock_lock);
        builder.setPriority(Notification.PRIORITY_HIGH);//important!!

        if (Build.VERSION.SDK_INT >= 21)
            builder.setVibrate(new long[0]);
        return builder.build();
    }

}
