package com.example.turist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHellp  extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "treakDb";

    public static final String TABLE_TREAKS = "treaks";
    public static final String TABLE_paint = "paint";
    public static final String TABLE_message = "message";
    public  static final String TABLE_CATEG = "categorii";
    public static final String TABLE_ERROR = "error";


    public static final String KEY_ERROR = "log";

    public static final String KEY_TRACK_ID = "track_id";
    public static final String KEY_X = "x";
    public static final String KEY_Y = "y";
    public static final String KEY_X_PAINT = "X_PAINT";
    public static final String KEY_Y_PAINT = "Y_PAINT";
    public static final String KEY_DATE = "date";
    public static final String KEY_MARKER = "marker";

    public static final String KEY_X_MESSAGE = "x_message";
    public static final String KEY_Y_MESSAGE = "y_message";
    public static final String TEXT_MESSAGE = "text";
    public static final String CATEG_MESSAGE = "categ";
    public static final String DATE_MESSAGE = "date";
    public static final String ID_USER_MESSAGE = "id";
    public static final String CATEGRS = "categrs";
    public static final String ID_CATEG = "id_categ";
    public static final String IMAGE_MESSAG = "image";




    public DBHellp(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_ERROR + "("
                + KEY_ERROR + " text" + ");");

        db.execSQL("create table " + TABLE_TREAKS + "("
                + KEY_TRACK_ID + " text,"
                + KEY_X + " text,"
                + KEY_Y + " text,"
                + KEY_DATE + " text,"
                + KEY_MARKER + " text" +");");


        db.execSQL("create table " + TABLE_paint + " ("
                + KEY_X_PAINT + " text,"
                + KEY_Y_PAINT + " text" +");");


        db.execSQL("create table " + TABLE_message + " ("
                + KEY_X_MESSAGE + " text,"
                + KEY_Y_MESSAGE + " text,"
                + TEXT_MESSAGE + " text,"
                + CATEG_MESSAGE + " text,"
                + DATE_MESSAGE + " text,"
                + ID_USER_MESSAGE +" text,"
                + IMAGE_MESSAG + " text" + ");");

        db.execSQL("create table " + TABLE_CATEG + " ("
                + ID_CATEG + " text,"
                + CATEGRS + " text" + ");");


//        db.execSQL("creat table "+ TABLE_paint + "("
//                + KEY_X_PAINT + " text,"
//                + KEY_Y_PAINT + " text" + ");");
    }
    String[] dat;
    String s = "select sysdate from dual";

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_TREAKS);
//        db.execSQL("drop table if exists " + TABLE_paint);
        onCreate(db);
        db.rawQuery(s, dat);
    }

}
