package dawar.catchevent;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FirebaseService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(remoteMessage.getData().size()>0) {
            Map<String, String> data = remoteMessage.getData();
            Log.i("notification recieved",data.get("name"));
            sendNotification(data);
        }

    }

    private void sendNotification(Map<String, String> data) {
        Intent i=new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pIntent =PendingIntent.getActivity(this,0,i,
                PendingIntent.FLAG_ONE_SHOT);

       /* if (message.length() > NOTIFICATION_MAX_CHARACTERS) {
            message = message.substring(0, NOTIFICATION_MAX_CHARACTERS) + "\u2026";
        }*/

       String name=data.get("name");
       String key=data.get("key");

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.catchevent)
                .setContentTitle(String.format("New Event: %1$s",name))
                .setContentText(key)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pIntent);

        NotificationManager manager= (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        if (manager != null) {
            final  AtomicInteger c = new AtomicInteger(0);
            manager.notify(c.incrementAndGet(),notiBuilder.build());
        }

    }

}
