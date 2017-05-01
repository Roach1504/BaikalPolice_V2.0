//package com.example.turist;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.util.Log;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.io.Writer;
//import java.lang.Thread.UncaughtExceptionHandler;
//import java.util.Date;
//
//public class CustomExceptionHandler implements UncaughtExceptionHandler {
//
//    DBHellp dbHelper;
//    SQLiteDatabase database;
//
//    private File logsFolder = null;
//    public CustomExceptionHandler(File logsFolder, Context context) {
//        this.logsFolder = logsFolder;
//
//        dbHelper = new DBHellp(context);
//        database = dbHelper.getWritableDatabase();
//    }
//    @Override
//    public void uncaughtException(Thread thread, Throwable ex) {
//        final Writer result = new StringWriter();
//        final PrintWriter printWriter = new PrintWriter(result);
//        ex.printStackTrace(printWriter);
//        String stacktrace = result.toString();
//        printWriter.close();
//        try {
//            if (!logsFolder.exists()) {
//                logsFolder.createNewFile();
//            }
//            BufferedWriter writer = new BufferedWriter(new FileWriter(logsFolder, true));
//            writer.write(""+new Date()+"\n"+stacktrace);
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        Log.e("Error","My LOG: " + stacktrace);
//
//
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(DBHellp.KEY_ERROR, stacktrace);
//        database.insert(DBHellp.TABLE_ERROR, null, contentValues);
//
//
//        android.os.Process.killProcess(android.os.Process.myPid());
//    }
//}
