package com.profete162.WebcamWallonnes.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static String DB_NAME_WEBCAM = "webcam.db";
    // The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.profete162.WebcamWallonnes/databases/";
    private final Context myContext;
    private SQLiteDatabase myDataBase;

    /**
     * Constructor Takes and keeps a reference of the passed context in order to
     * access to the application assets and resources.
     *
     * @param context
     */
    public DataBaseHelper(Context context) {

        super(context, DB_NAME_WEBCAM, null, 1);
        this.myContext = context;
    }

    /**
     * Creates a empty database on the system and rewrites it with your own
     * database.
     */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if (dbExist) {
            // do nothing - database already exist
        } else {

            // By calling this method and empty database will be created into
            // the default system path
            // of your application so we are gonna be able to overwrite that
            // database with our database.
            this.getWritableDatabase();

            copyDataBase(DB_NAME_WEBCAM);

        }

    }

    public void forceCreateDataBase(Context context) throws IOException {

        // try {
        this.getReadableDatabase();
        //this.getWritableDatabase();

        copyDataBase(DB_NAME_WEBCAM);


/*
        } catch (Exception e) {

            SQLiteDatabase mDatabase = SQLiteDatabase.openDatabase("/data/data/com.profete162.WebcamWallonnes/databases/" + DB_NAME_WEBCAM, null, SQLiteDatabase.CREATE_IF_NECESSARY);
            e.printStackTrace();
        }*/


    }

    /**
     * Check if the database already exist to avoid re-copying the file each
     * time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {

        SQLiteDatabase checkDBWebcam = null;


        try {
            String myPathWebcam = DB_PATH + DB_NAME_WEBCAM;
            checkDBWebcam = SQLiteDatabase.openDatabase(myPathWebcam, null,
                    SQLiteDatabase.NO_LOCALIZED_COLLATORS);


        } catch (SQLiteException e) {
            return false;
            // database does't exist yet.

        }

        if (checkDBWebcam != null) {

            checkDBWebcam.close();


        }

        return true;
    }

    /**
     * Copies your database from your local assets-folder to the just created
     * empty database in the system folder, from where it can be accessed and
     * handled. This is done by transfering bytestream.
     */
    private void copyDataBase(String name) throws IOException {

        // Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(name);

        // Path to the just created empty db
        String outFileName = DB_PATH + name;

        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase(String name) throws SQLException {

        // Open the database
        String myPath = DB_PATH + name;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE
                        | SQLiteDatabase.NO_LOCALIZED_COLLATORS);

    }

    @Override
    public synchronized void close() {

        if (myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

    }

    public boolean updateWebcam(long rowId, String title) {
        ContentValues args = new ContentValues();
        args.put("City", title);
        return myDataBase.update("cameras", args, "_id" + "=" + rowId, null) > 0;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

    public Cursor fetchAllWebcam() {
        return myDataBase.query("cameras", new String[]{"_id", "city", "lat",
                "lng", "img", "starred"}, null, null, null, null, "city");
    }

    public Cursor fetchAllNonFavWebcam() {
        return myDataBase.rawQuery("SELECT * FROM cameras WHERE starred<>1 ORDER BY city ASC", null);
    }

    public Cursor fetchAllFavWebcam() {
        return myDataBase.rawQuery("SELECT * FROM cameras WHERE starred=1 ORDER BY city ASC", null);
    }

    public Cursor fetchAllWebcam(String zone,String lan) {
        return myDataBase.rawQuery("SELECT * FROM cameras WHERE zone_"+lan+"='" + zone
                + "' AND starred<>1 ORDER BY city ASC", null);
    }

    public Cursor fetchAllZones(String lan) {
        return myDataBase.rawQuery("SELECT DISTINCT zone_"+lan+" FROM cameras order by zone_"+lan+" asc", null);
    }

    public Cursor fetchWebcam(String name) {
        return myDataBase.rawQuery("SELECT * FROM cameras WHERE city='" + name
                + "'", null);
    }
    public int setFavorite(Webcam webcam) {
        String strFilter = "_id=" + webcam.id;
        ContentValues args = new ContentValues();
        args.put("starred", 1);
        return myDataBase.update("cameras", args, strFilter, null);
    }

    public int resetFavorite(Webcam webcam) {
       // myDataBase.rawQuery("UPDATE cameras SET starred=0 cameras WHERE _id='" + webcam.id
        //        + "'", null);

        String strFilter = "_id=" + webcam.id;
        ContentValues args = new ContentValues();
        args.put("starred", 0);
       return  myDataBase.update("cameras", args, strFilter, null);
    }

}

