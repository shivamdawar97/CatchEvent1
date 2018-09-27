package dawar.catchevent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class FirebaseReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Broadcast","called");
        Toast.makeText(context,"Broadcasr called",Toast.LENGTH_LONG).show();
//        Intent i=new Intent(context,FirebaseService.class);
//      context.startService(i);
    }
}
