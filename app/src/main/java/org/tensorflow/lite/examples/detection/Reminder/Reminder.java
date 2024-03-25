package org.tensorflow.lite.examples.detection.Reminder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.tensorflow.lite.examples.detection.DBHandler;
import org.tensorflow.lite.examples.detection.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Reminder extends AppCompatActivity {


      RecyclerView mRecyclerview;
    ArrayList<Model> dataholder = new ArrayList<Model>();                                               //Array list to add reminders and display in recyclerview
    float x1,x2;
    public static ArrayList<String> text=new ArrayList<>();
    public static ArrayList<String> time=new ArrayList<>();
    TextView txt;
    myAdapter adapter;
    private DBHandler dbHandler;
    static String query,query1;
  private static TextToSpeech textToSpeech;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        dbHandler = new DBHandler(this);
        mRecyclerview = (RecyclerView) findViewById(R.id.recyclerView);
        txt = findViewById(R.id.textView);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.getDefault());
                    textToSpeech.setSpeechRate(0.9f);
                    textToSpeech.speak(query1,TextToSpeech.QUEUE_FLUSH,null);
                }
            }
        });


       Boolean ISDELETED = dbHandler.deleteAll();
        if(ISDELETED){
            Toast.makeText(getApplicationContext(), "Deleted all records ", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
        }

        Cursor cursor = dbHandler.readallreminders();                  //Cursor To Load data From the database
        while (cursor.moveToNext()) {
            Model model = new Model(cursor.getString(1), cursor.getString(2), cursor.getString(3),cursor.getString(4));
            dataholder.add(model);
          }

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("d-M-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        if (cursor.moveToFirst()) {
            do {
                if(cursor.getString(2).equals(formattedDate)) {
                time.add("You have a reminder at,"+cursor.getString(3)+",and reminder is "+cursor.getString(1)+".");

                }
            }
            while (cursor.moveToNext());
            // moving our cursor to next.
        }

         adapter = new myAdapter(dataholder);
        if(time.size()>0){
            txt.setText("You have a new reminder for today. swipe left to read the reminder or swipe right to create reminder.");
            query1 = "You have a new reminder for today. swipe left to read the reminder or swipe right to create reminder.";
            Toast.makeText(getApplicationContext(), time.toString(), Toast.LENGTH_LONG).show();
        }
        else {
            query1 = "you have no reminder for today. swipe right to create a reminder.";
            txt.setText(query1);
        }
          for (int i = 0; i < time.size(); i++) {
               query = time.get(i);

         Toast.makeText(getApplicationContext(), time.toString(), Toast.LENGTH_LONG).show();
          }
             mRecyclerview.setAdapter(adapter);
            //Binds the adapter with recyclerview


    }


    @SuppressLint("SetTextI18n")
    public boolean onTouchEvent(MotionEvent touchEvent) {
        switch (touchEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                 if (x1 < x2) {
                     if(time.toString().contains("[]")){
                         txt.setText("There are no reminders for today. swipe right, to add the reminder");
                         textToSpeech.speak("There are no reminders for today. swipe right, to add the reminder", TextToSpeech.QUEUE_FLUSH,null);
                     }
                     else {
                         textToSpeech.speak(time.toString(),TextToSpeech.QUEUE_FLUSH,null);
                         textToSpeech.speak("swipe left to read once again", TextToSpeech.QUEUE_ADD,null);

                     }

                }
                 if (x1>x2){
                     Intent i = new Intent(Reminder.this,ReminderActivity.class);
                     startActivity(i);
                 }


                break;
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        finish();
        time.clear();
        //Makes the user to exit from the app
        super.onBackPressed();
    }
    public void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
        super.onPause();

    }
}
