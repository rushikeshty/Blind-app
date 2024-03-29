package org.tensorflow.lite.examples.detection.Reminder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.tensorflow.lite.examples.detection.DBHandler;
import org.tensorflow.lite.examples.detection.Home;
import org.tensorflow.lite.examples.detection.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

//this class is to take the reminders from the user and inserts into the database
public class ReminderActivity extends AppCompatActivity implements RecognitionListener {
    EditText mDatebtn, mTimebtn;
    @SuppressLint("StaticFieldLeak")
    static EditText mTitledit;
    private DBHandler dbHandler;
    TextView textView;
    private static TextToSpeech textToSpeech;
    static int layoutclick;
    static SpeechRecognizer speechRecognizer;
    //When user tap on the layout we will increment it , switch to case statement
    static String title,finaltime;
    static String finalalarm;
    LinearLayout layout;
    static int count =0;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder2);
        dbHandler = new DBHandler(this);
        layoutclick=0;
        mTitledit = (EditText) findViewById(R.id.editTitle);
        textView = findViewById(R.id.addreminder);
        mDatebtn = findViewById(R.id.btnDate);
        mTimebtn = findViewById(R.id.btnTime);
        layout = findViewById(R.id.layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //we created this method as we have added Onclick in xml code for reminderActivity
                //so when user tap on the screen we increase layout click count because for 1st case
                // we take date from user again for 2nd click we'll take time then task and so on
                layoutclick++;
                textToSpeech.stop();
                startVoiceInput();
                Toast.makeText(getApplicationContext(), String.valueOf(layoutclick), Toast.LENGTH_SHORT).show();

            }
        });
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        /** initialize the speech recognizer as it is custom speech recognizer
         * see this @link:- https://developer.android.com/reference/android/speech/SpeechRecognizer#:~:text=API%20level%208-,SpeechRecognizer,-Kotlin%20%7CJava */
        speechRecognizer.setRecognitionListener(this);
        if(checkIfAlreadyhavePermission()){
            //check permission for recording audio
            Toast.makeText(getApplicationContext(), "Permission is granted", Toast.LENGTH_SHORT).show();
        }else {
            ActivityCompat.requestPermissions(ReminderActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    1);
        }

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setSpeechRate(1f);
                    textToSpeech.speak("tap on the screen and, Tell me the exact date", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

    }

     @RequiresApi(api = Build.VERSION_CODES.O)
     private void processinsert(String title, String date, String time) {
       //for inserting records in sqlite
        String result = dbHandler.addreminder(title, date, time);//data inserting in sqlite                  //inserts the title,date,time into sql lite database
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
             setAlarm(title, date, time);                                                                //calls the set alarm method to set alarm
         }
        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
    }

    public void startVoiceInput() {
        Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "say");
        speechRecognizer.setRecognitionListener(this);
        speechRecognizer.startListening(speechIntent);


    }
    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED;
    }


    @SuppressLint("ObsoleteSdkInt")
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setAlarm(String text, String date, String time) {
        //setting alarm using AlarmManager
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //assigning alarm manager object to set alarm



        Intent intent = new Intent(getApplicationContext(), AlarmBroadcast.class);
        intent.setAction(Long.toString(System.currentTimeMillis()));
        intent.putExtra("event", text);
        //sending data to alarm class to create channel and notification
        intent.putExtra("time", date);
        intent.putExtra("date", time);

         // context variable contains your `Context`

            // Loop counter `i` is used as a `requestCode`
             // Single alarms in 1, 2, ..., 10 minutes (in `i` minutes)

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        finalalarm  = date + " " + mTimebtn.getText().toString();
        Toast.makeText(getApplicationContext(), finalalarm, Toast.LENGTH_LONG).show();
        @SuppressLint("SimpleDateFormat")
        DateFormat formatter = new SimpleDateFormat("d-M-yyyy hh:mm");
        try {

            Date date1 = formatter.parse(finalalarm);
             //Toast.makeText(getApplicationContext(), dateandtime, Toast.LENGTH_LONG).show();
            am.set(AlarmManager.RTC_WAKEUP, date1.getTime(), pendingIntent);

            // Toast.makeText(getApplicationContext(), "Alarm set", Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }





    public void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
        super.onPause();
     }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Toast.makeText(getApplicationContext(), "Listening...", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int i) {
        //if user did not respond to the speech recognizer then it restarts the speech input
        //it cathes the error in this method
        if (count<3){
            textToSpeech.speak("say something",TextToSpeech.QUEUE_FLUSH,null);
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startVoiceInput();
                    count++;

                }
            },2000);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResults(Bundle bundle) {
        ArrayList<String> result = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        if(layoutclick==1) {
            //when first click we will take date from user in number format
            // so we should remove characters as follows
            String date = result.get(0);
            date = date.replace("january", "January");
            date = date.replace("february", "February");
            date = date.replace("march", "March");
            date = date.replace("april", "April");
            date = date.replace("may", "May");
            date = date.replace("june", "June");
            date = date.replace("july", "July");
            date = date.replace("august", "August");
            date = date.replace("september", "September");
            date = date.replace("october", "October");
            date = date.replace("november", "November");
            date = date.replace("december", "December");
            try {
                DateTimeFormatter dtfParse = DateTimeFormatter.ofPattern(
                        "d MMMM yyyy",
                        Locale.ENGLISH);
                // a second one to be used in order to format the desired result
                DateTimeFormatter dtfFormat = DateTimeFormatter.ofPattern(
                        "d-M-yyyy",
                        Locale.ENGLISH);
                // parse the input with the first formatter

                LocalDate localDate = LocalDate.parse(date, dtfParse);
                //mDatebtn.setText(day + "-" + month + "-" + year);
                mDatebtn.setText(localDate.format(dtfFormat));
                textToSpeech.speak("Tell me the time", TextToSpeech.QUEUE_FLUSH, null);
            } catch (Exception e) {
                //if there any exception then say date is wrong
                //Note here we have declare layout click as 0 because we have if condition as if layout click=1 then we will take date but if there is any error
                //then we will set layout click as again 0 so user again tap on screen and say correct date
                // same is applicable for time, task. simple logic
                textToSpeech.speak("Date is wrong", TextToSpeech.QUEUE_FLUSH, null);
                textToSpeech.speak("Tell me the exact date", TextToSpeech.QUEUE_ADD, null);
                layoutclick--;
            }
            if(result.get(0).equals("")){
                textToSpeech.speak("please say the date", TextToSpeech.QUEUE_FLUSH,null);
                layoutclick--;
            }


        }

        if(layoutclick==2) {

            String results;

            results = result.get(0);
            results = results.replace("a.m.", "AM");
            results = results.replace("p.m.", "PM");
            results = results.replace("a.m", "AM");
            results = results.replace("p.m", "PM");
            results = results.replace("am", "AM");
            results = results.replace("pm", "PM");

             // mTimebtn.setText(time);
            if (results.contains("12") && results.contains("PM")) {
                try {
                    String parsePatter = results.contains(":") ? "hh:mm a" : "hh m a";
                    SimpleDateFormat parser = new SimpleDateFormat(parsePatter);
                    Date date1 = parser.parse(results);

                    // Result
                    SimpleDateFormat resultParse = new SimpleDateFormat("hh:mm a");
                    String result1 = resultParse.format(date1);
                    result1 = result1.replace("pm", "PM");
                    mTimebtn.setText(result1);
                    finaltime = mTimebtn.getText().toString();
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                    layoutclick = 1;
                }
            }

             try {
                String parsePatter = results.contains(":") ? "hh:mm a" : "hh m a";
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat parser = new SimpleDateFormat(parsePatter);
                Date date1 = parser.parse(results);
                SimpleDateFormat resultParse = new SimpleDateFormat("HH:mm a");
                String result1 = resultParse.format(date1);
                mTimebtn.setText(result1.toUpperCase());
                finaltime = mTimebtn.getText().toString();

            } catch (ParseException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                try {
                    String parsePatter = results.contains(":") ? "k:mm a" : "k m a";
                    SimpleDateFormat parser = new SimpleDateFormat(parsePatter);
                    Date date1 = parser.parse(results);
                    SimpleDateFormat resultParse = new SimpleDateFormat("HH:mm a");
                    String result1 = resultParse.format(date1);
                     mTimebtn.setText(result1.toUpperCase());
                    finaltime = mTimebtn.getText().toString();
                }catch (ParseException e2){
                    try {
                        SimpleDateFormat parser = new SimpleDateFormat("h:m:a");
                        Date date1 = parser.parse(results);
                        SimpleDateFormat resultParse = new SimpleDateFormat("HH:mm a");
                        String result1 = resultParse.format(date1);
                        mTimebtn.setText(result1.toUpperCase());
                        finaltime = mTimebtn.getText().toString();

                    } catch (ParseException parseException) {
                        parseException.printStackTrace();
                        if(results.length()>3&&results.charAt(0)>0){
                            SimpleDateFormat parser = new SimpleDateFormat("h:m:a");
                            try {
                                Date date1 = parser.parse(results);
                                SimpleDateFormat resultParse = new SimpleDateFormat("HH:mm a");
                                String result1 = resultParse.format(date1);
                                mTimebtn.setText(result1.toUpperCase());
                                finaltime = mTimebtn.getText().toString();


                            } catch (ParseException exception) {
                                exception.printStackTrace();
                            }

                        }

                    }

                    Toast.makeText(getApplicationContext(), e2.getMessage(), Toast.LENGTH_SHORT).show();
                }
                 if(results.length()>3&&results.charAt(0)>0){
                     SimpleDateFormat parser = new SimpleDateFormat("h:m:a");

                     try {
                         Date   date1 = parser.parse(results);
                         SimpleDateFormat resultParse = new SimpleDateFormat("HH:mm a");
                         String result1 = resultParse.format(date1);
                         mTimebtn.setText(result1.toUpperCase());
                         finaltime = mTimebtn.getText().toString();

                     } catch (ParseException exception) {
                         exception.printStackTrace();
                     }

                 }

                layoutclick=1;
                 textToSpeech.speak("time is wrong. say again", TextToSpeech.QUEUE_FLUSH,null);
            }
             if(!mTimebtn.getText().toString().equals("")){
                 textToSpeech.speak("please say the task that you want to remember",TextToSpeech.QUEUE_FLUSH,null);
             }
        }



        if(layoutclick==3) {
             String task = result.get(0);
            mTitledit.setText(task);
            title = mTitledit.getText().toString().trim();                               //access the data from the input field
            if (title.isEmpty()) {
                Toast.makeText(getApplicationContext(), "tap on the screen and say the task that you want to remember", Toast.LENGTH_SHORT).show();   //shows the toast if input field is empty
                layoutclick = 2;
            }

            textToSpeech.speak("would you like to save the reminder", TextToSpeech.QUEUE_FLUSH, null);
        }


            if (result.get(0).contains("yes") && result.get(0).contains("s")) {
                 processinsert(title, mDatebtn.getText().toString(), mTimebtn.getText().toString());
                textToSpeech.speak("Reminder successfully set.", TextToSpeech.QUEUE_FLUSH, null);
                textToSpeech.speak("returning to main menu", TextToSpeech.QUEUE_ADD, null);
                final Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(ReminderActivity.this, Home.class);
                        startActivity(i);
                    }
                }, 5000);
            } else if (result.get(0).contains("no")) {
                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textToSpeech.speak("Ok. Reminder cancel successfully.", TextToSpeech.QUEUE_FLUSH, null);

                    }
                }, 1000);
                finish();
                Intent i = new Intent(ReminderActivity.this, ReminderActivity.class);
                startActivity(i);
            }




        if (result.toString().contains("delete all reminders") || result.toString().contains("delete reminder")) {
            Boolean delete = dbHandler.DeleteAllReminder();
            if (delete) {
                Toast.makeText(getApplicationContext(), "All reminders deleted", Toast.LENGTH_SHORT).show();
                textToSpeech.speak("All reminders deleted", TextToSpeech.QUEUE_FLUSH, null);
            } else {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();

            }
        }
        if(result.get(0).contains("main menu")){
            Intent i = new Intent(ReminderActivity.this,Home.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }

        if (result.get(0).contains("delete all my reminders")||result.get(0).contains("delete reminder")) {
            Boolean delete = dbHandler.DeleteAllReminder();
            if (delete){
                Toast.makeText(getApplicationContext(), "All reminders deleted", Toast.LENGTH_SHORT).show();
                textToSpeech.speak("All reminders deleted",TextToSpeech.QUEUE_FLUSH,null);
            }
            else {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();

            }
        }

        if (result.get(0).contains("exit")) {
            finishAffinity();
        }


    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onEvent(int i, Bundle bundle) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == 1) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(getApplicationContext(), "permission granted", Toast.LENGTH_SHORT).show();
            } else {

                 Toast.makeText(ReminderActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
            }

        }
    }

}
