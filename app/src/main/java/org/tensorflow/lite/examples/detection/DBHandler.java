package org.tensorflow.lite.examples.detection;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.tensorflow.lite.examples.detection.QRProduct.Prodlist;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

    // creating a constant variables for our database.
    // below variable is for our database name.
    private static final String DB_NAME = "testdata";
  public static   ArrayList<String> arrayList=new ArrayList<>();
    public static   ArrayList<Double> arraysum=new ArrayList<>();
     // below int is our database version
    private static final int DB_VERSION = 1;
    static String date1;
    public static String getDate(){return date1;}
    static String error;
    public static String getError(){
        return error;
    }

    // below variable is for our table name.
    private static final String TABLE_NAME = "hello";

    // below variable is for our id column.

    // below variable is for our course name column
    private static final String NAME_COL = "name";
    private static final String NAME_NUM = "count";
    private static final String ID = "ID";


    private String[] hello;


    // creating a constructor for our database handler.
    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        // on below line we are creating
        // an sqlite query and we are
        // setting our column names
        // along with their data types.
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                +ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME_COL + " TEXT ,"+NAME_NUM+" int )";
        //For storing QR details.

        String query2 = "create table reminder (" +
                "id integer primary key autoincrement,title text,date text,time text,finaldate default current_timestamp)";
        db.execSQL(query2);

        // at last we are calling a exec sql
        // method to execute above sql query
        db.execSQL(query);



    }

    // this method is use to add new course to our sqlite database.
    public void addNewCourse(String courseName,int num) {

       /** on below line we are creating a variable for
        *our sqlite database and calling writable method
        * as we are writing data in our database.*/

        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are creating a
        // variable for content values.
        ContentValues values = new ContentValues();

        // on below line we are passing all values
        // along with its key and value pair.
        values.put(NAME_COL, courseName);

        values.put(NAME_NUM,num);

        // after adding all values we are passing
        // content values to our table.
        db.insert(TABLE_NAME, null, values);

        // at last we are closing our
        // database after adding database.


    }


    // we have created a new method for reading all the courses.
    public ArrayList<Prodlist> readCourses() {
        // on below line we are creating a
        // database for reading our database.
        SQLiteDatabase db = this.getReadableDatabase();

        // on below line we are creating a cursor with query to read data from database.
        String sql = "SELECT * FROM "+TABLE_NAME+" WHERE id=(SELECT max(id) FROM "+TABLE_NAME+");";
        @SuppressLint("Recycle")
        Cursor cursorCourses = db.rawQuery(sql,null);

        // on below line we are creating a new array list.
        ArrayList<Prodlist> courseModalArrayList = new ArrayList<>();



        // moving our cursor to first position.
        if (cursorCourses.moveToFirst()) {
            do {
                // on below line we are adding the data from cursor to our array list.
                courseModalArrayList.add(new Prodlist(cursorCourses.getString(1),cursorCourses.getString(2)));
                     arrayList.add(cursorCourses.getString(1));
                arraysum.add(cursorCourses.getDouble(2));

            } while (cursorCourses.moveToNext());
            // moving our cursor to next.
        }
         cursorCourses.close();
         return courseModalArrayList;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Boolean deleteData(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL(" DELETE FROM " +TABLE_NAME);
        db.close();
        return true;
    }

    public Boolean DeleteAllReminder(){
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL(" DELETE FROM reminder ");
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String addreminder(String title, String date, String time) {
        //LocalDate dateObj = LocalDate.now();
        try {
             SQLiteDatabase database = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("title", title);                                                          //Inserts  data into sqllite database
            contentValues.put("date", date);
            contentValues.put("time", time);
            float result = database.insert("reminder", null, contentValues);    //returns -1 if data successfully inserts into database

            if (result == -1) {
                return "Failed";
            } else {
                return "Successfully inserted";
            }

        } catch(Exception e) {
            return e.getMessage();
        }

    }




    public Cursor readallreminders() {
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "select * from reminder order by id desc";
        //Sql query to  retrieve  data from the database
        return database.rawQuery(query, null);
    }

     public Boolean deleteAll() {
         SQLiteDatabase db = this.getWritableDatabase();
        String sql = " DELETE FROM reminder WHERE (finaldate) < DATE('now') ";
        db.execSQL(sql);
        return true;
    }
}
