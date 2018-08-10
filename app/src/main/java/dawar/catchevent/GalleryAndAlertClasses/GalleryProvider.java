package dawar.catchevent.GalleryAndAlertClasses;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static dawar.catchevent.CatchEvent.sdatabase;

public class GalleryProvider extends ContentProvider {
    static final String  AUTHORITY="dawar.catchevent.GalleryAndAlertClasses";

    static final String PATH_ALL_IMAGES="ALL_IMAGES";
    static final String PATH_EVENT_IMAGES="EVENT_IMAGES";
    static final String PATH_ALL_ALERTS="ALL_ALERTS";
    static final String PATH_EVENT_ALERTS="EVENT_ALERTS";

    static final Uri Content_Uri_1 = Uri.parse("content://"+AUTHORITY+"/"+PATH_ALL_IMAGES);
    static final Uri Content_Uri_2 = Uri.parse("content://"+AUTHORITY+"/"+PATH_EVENT_IMAGES);
    static final Uri Content_Uri_3 = Uri.parse("content://"+AUTHORITY+"/"+PATH_ALL_ALERTS);
    static final Uri Content_Uri_4 = Uri.parse("content://"+AUTHORITY+"/"+PATH_EVENT_ALERTS);


    public static final int ALL_IMAGES=1;
    public static final int EVENT_IMAGES=2;
    public static final int ALL_ALERTS=3;
    public static final int EVENT_ALERTS=4;

    static final UriMatcher MATCHER= new UriMatcher(UriMatcher.NO_MATCH);


    static {
        MATCHER.addURI(AUTHORITY,PATH_ALL_IMAGES,ALL_IMAGES);
        MATCHER.addURI(AUTHORITY,PATH_EVENT_IMAGES,EVENT_IMAGES);
        MATCHER.addURI(AUTHORITY,PATH_ALL_ALERTS,ALL_ALERTS);
        MATCHER.addURI(AUTHORITY,PATH_EVENT_ALERTS,EVENT_ALERTS);
    }


    static final String CONTENT_TYPE_1 = ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+ PATH_ALL_IMAGES;
    static final String CONTENT_TYPE_2 = ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+ PATH_EVENT_IMAGES;
    static final String CONTENT_TYPE_3 = ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+ PATH_ALL_ALERTS;
    static final String CONTENT_TYPE_4 = ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+ PATH_EVENT_ALERTS;

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (MATCHER.match(uri)){
            case  ALL_IMAGES : return CONTENT_TYPE_1;
            case EVENT_IMAGES: return CONTENT_TYPE_2;
            case ALL_ALERTS : return CONTENT_TYPE_3;
            case EVENT_ALERTS: return CONTENT_TYPE_4;

        }
        return null;
    }

    @Override
    public boolean onCreate() {
        return false;

    }

    @Nullable
    @Override
    // @strings:column of table. @s:where clause. @strings1:selection arguments
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
       Cursor cursor;
        switch (MATCHER.match(uri)){
            case ALL_IMAGES:
            case EVENT_IMAGES:

                cursor=sdatabase.query("Gallery",strings,s,strings1,null,null,null);
                return cursor;

            case ALL_ALERTS:
            case EVENT_ALERTS:

                cursor=sdatabase.query("Alerts",strings,s,strings1,null,null,null);
                return cursor;

        }

        return null;
    }



    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) throws UnsupportedOperationException{
        long uriId;
        switch (MATCHER.match(uri)){
            case ALL_IMAGES:
               uriId= sdatabase.insert("Gallery",null,contentValues);
                getContext().getContentResolver().notifyChange(uri,null);
                return Uri.parse("content://"+AUTHORITY+"/"+uriId);
        }
        return null;
    }

    @Override
    //s:whereClause strings:whereAguments
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
