package dawar.catchevent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static dawar.catchevent.CatchEvent.sdatabase;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_TABLE;
        final String SQL_CREATE_PLACES_TABLE = "CREATE TABLE Events" + " (" +
                "id_ INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Name TEXT ,"+
                " ImageRes BLOB NOT NULL, " +
                "EventKey TEXT"+
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_PLACES_TABLE);

        //create gallery table for event
        SQL_CREATE_TABLE = "Create table  Gallery"+
                "( imgKey TEXT PRIMARY KEY ,"+
                "bytes blob NOT NULL, captn TEXT , eventKey TEXT"+");";

        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);

        //create alerts table for event
        SQL_CREATE_TABLE= "Create table Alerts"+
                "(altKey TEXT PRIMARY KEY,imgkeys blob, eventKey TEXT);";
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Events");
        onCreate(sqLiteDatabase);
    }
}
