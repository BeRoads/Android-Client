package com.profete162.WebcamWallonnes.adapter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

	// The Android's default system path of your application database.
	private static String DB_PATH = "/data/data/com.profete162.WebcamWallonnes/databases/";
	public static String DB_NAME_WEBCAM = "webcam.db";
	public static String DB_NAME_RADAR = "radars.db";
	public static String DB_NAME_PARKING = "covoiturage.db";
	private SQLiteDatabase myDataBase;
	private final Context myContext;

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 */
	public DataBaseHelper(Context context) {

		super(context,DB_NAME_RADAR, null, 1);
		this.myContext = context;
	}

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * */
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

			try {

				copyDataBase(DB_NAME_WEBCAM);
				copyDataBase(DB_NAME_RADAR);
				copyDataBase(DB_NAME_PARKING);

			} catch (IOException e) {

				throw new Error("Error copying database");

			}
		}

	}

	public void forceCreateDataBase(Context context) throws IOException {

		try{
			this.getReadableDatabase();
			//this.getWritableDatabase();
			try {

				copyDataBase(DB_NAME_WEBCAM);
				copyDataBase(DB_NAME_RADAR);
				copyDataBase(DB_NAME_PARKING);

			} catch (IOException e) {

				e.printStackTrace();

			}
		}catch(Exception e){

			SQLiteDatabase mDatabase = SQLiteDatabase.openDatabase("/data/data/com.profete162.WebcamWallonnes/databases/"+DB_NAME_WEBCAM, null, SQLiteDatabase.CREATE_IF_NECESSARY);
			e.printStackTrace();
		}
		
		
		
		


	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase() {

		SQLiteDatabase checkDBWebcam = null;
		SQLiteDatabase checkDBRadar = null;
		SQLiteDatabase checkDBParking = null;

		try {
			String myPathWebcam = DB_PATH + DB_NAME_WEBCAM;
			String myPathRadar = DB_PATH + DB_NAME_RADAR;
			String myPathParking = DB_PATH + DB_NAME_PARKING;
			checkDBWebcam = SQLiteDatabase.openDatabase(myPathWebcam, null,
					SQLiteDatabase.NO_LOCALIZED_COLLATORS);
			checkDBRadar = SQLiteDatabase.openDatabase(myPathRadar, null,
					SQLiteDatabase.NO_LOCALIZED_COLLATORS);
			checkDBParking = SQLiteDatabase.openDatabase(myPathParking, null,
					SQLiteDatabase.NO_LOCALIZED_COLLATORS);

		} catch (SQLiteException e) {
			return false;
			// database does't exist yet.

		}

		if (checkDBWebcam != null&&checkDBRadar != null&&checkDBParking != null) {

			checkDBWebcam.close();
			checkDBRadar.close();
			checkDBParking.close();

		}

		return  true;
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
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
		return myDataBase.update("webcam", args, "_id" + "=" + rowId, null) > 0;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public Cursor fetchAllWebcam() {
		return myDataBase.query("webcam", new String[] { "_id", "City", "Lat",
				"Lon", "Cat", "Type" }, null, null, null, null, "City");
	}
	public Cursor fetchAllWebcamByDistance(double GPS[]) {
		return myDataBase.query("webcam", new String[] { "_id", "City", "Lat",
				"Lon", "Cat", "Type" }, null, null, null, null, "City");
	}
	public Cursor fetchAllRadar() {
		return myDataBase.query("radars", new String[] { "id", "name", "lat",
				"lon", "speedLimit" }, null, null, null, null, null);
	}
	public Cursor fetchAllParking() {
		return myDataBase.query("covoiturage", new String[] { "id", "nom", "lat",
				"lon", "localisation", "places" }, null, null, null, null, null);
	}

	public Cursor fetchAllWebcam(int group) {
		return myDataBase.rawQuery("SELECT * FROM webcam WHERE Type='" + group
				+ "'", null);
	}
	
	public Cursor fetchWebcam(String name) {
		return myDataBase.rawQuery("SELECT * FROM webcam WHERE City='" + name
				+ "'", null);
	}

}
