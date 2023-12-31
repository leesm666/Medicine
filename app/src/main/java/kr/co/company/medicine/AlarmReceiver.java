package kr.co.company.medicine;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    String CHANNEL_ID = "Medicine";

    @Override
    public void onReceive(Context context, Intent intent) {
        int req = intent.getExtras().getInt("req", 0);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, req, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setContentTitle("지금이약").setContentText("약 복용할 시간입니다.")
                .setWhen(Calendar.getInstance().getTimeInMillis()).setContentIntent(pendingIntent).setAutoCancel(true);

        notificationManager.notify(0, builder.build());
    }
}
