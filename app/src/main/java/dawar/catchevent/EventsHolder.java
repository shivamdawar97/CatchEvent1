package dawar.catchevent;

import android.graphics.Bitmap;
import android.os.Bundle;

import java.util.ArrayList;

public class EventsHolder {
    public ArrayList<String> Titles,keys;
    public ArrayList<Bitmap> imgs;
    EventsHolder(){
        Titles=new ArrayList<>();
        imgs=new ArrayList<>();
        keys=new ArrayList<>();
    }
}
