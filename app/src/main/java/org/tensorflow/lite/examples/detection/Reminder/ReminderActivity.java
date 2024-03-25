package org.tensorflow.lite.examples.detection.Reminder;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.examples.detection.Battery;
import org.tensorflow.lite.examples.detection.Calculator;
import org.tensorflow.lite.examples.detection.DBHandler;
import org.tensorflow.lite.examples.detection.DateAndTime;
import org.tensorflow.lite.examples.detection.Location.LocationActivity;
import org.tensorflow.lite.examples.detection.Moneytransfer.Banktransfer;
import org.tensorflow.lite.examples.detection.Moneytransfer.phonetransfer;
import org.tensorflow.lite.examples.detection.Navigation.Navigation;
import org.tensorflow.lite.examples.detection.OCRReader;
import org.tensorflow.lite.examples.detection.ObjectDetection.MainActivity;
import org.tensorflow.lite.examples.detection.QRProduct.QRactivity;
import org.tensorflow.lite.examples.detection.R;
import org.tensorflow.lite.examples.detection.Weather;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

//this class is to take the reminders from the user and inserts into the database
public class ReminderActivity extends AppCompatActivity {
    EditText mDatebtn, mTimebtn;
    @SuppressLint("StaticFieldLeak")
    static EditText mTitledit;
    private DBHandler dbHandler;
    private static final int REQ_CODE_SPEECH_INPUT = 100;
   private static TextToSpeech textToSpeech;
    int layoutclick;
    //When user tap on the layout we will increment it , switch to case statement
    static String title,finaltime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder2);
        dbHandler = new DBHandler(this);
        mTitledit = (EditText) findViewById(R.id.editTitle);
        mDatebtn = findViewById(R.id.btnDate);
        mTimebtn = findViewById(R.id.btnTime);
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setSpeechRate(1f);
                    textToSpeech.speak("Tell me the exact date", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
    }

     @RequiresApi(api = Build.VERSION_CODES.O)
     private void processinsert(String title, String date, String time) {

        String result = dbHandler.addreminder(title, date, time);//data inserting in sqlite                  //inserts the title,date,time into sql lite database
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

             setAlarm(title, date, time);                                                                //calls the set alarm method to set alarm
         }
        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            a.printStackTrace();
        }
    }


    @SuppressLint("ObsoleteSdkInt")
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setAlarm(String text, String date, String time) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);                   //assigning alarm manager object to set alarm
        Intent intent = new Intent(getApplicationContext(), AlarmBroadcast.class);
        intent.putExtra("event", text);                                                       //sending data to alarm class to create channel and notification
        intent.putExtra("time", date);
        intent.putExtra("date", time);
         PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String dateandtime = date + " " + finaltime;
        Toast.makeText(getApplicationContext(), dateandtime, Toast.LENGTH_LONG).show();
        @SuppressLint("SimpleDateFormat")
        DateFormat formatter = new SimpleDateFormat("d-M-yyyy hh:mm");
        try {

            Date date1 = formatter.parse(dateandtime);

            //Toast.makeText(getApplicationContext(), dateandtime, Toast.LENGTH_LONG).show();
            am.set(AlarmManager.RTC_WAKEUP, date1.getTime(), pendingIntent);
            // Toast.makeText(getApplicationContext(), "Alarm set", Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public void layoutClicked(View view) {
        layoutclick++;
        startVoiceInput();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
               switch (layoutclick) {
                   case 1:
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
                           textToSpeech.speak("Date is wrong", TextToSpeech.QUEUE_FLUSH, null);
                           textToSpeech.speak("Tell me the exact date", TextToSpeech.QUEUE_ADD, null);
                           layoutclick--;
                       }
                       break;
                   case 2:

                       String time = result.get(0);
                       time = time.replace("a.m", "AM");
                       time = time.replace("p.m", "PM");
                       time = time.substring(0, time.length() - 1);
                       // mTimebtn.setText(time);
                       if (time.contains("12") && time.contains("PM")) {
                           try {
                               String parsePatter = time.contains(":") ? "hh:mm a" : "hh m a";
                               SimpleDateFormat parser = new SimpleDateFormat(parsePatter);
                               Date date1 = parser.parse(time);

                               // Result
                               SimpleDateFormat resultParse = new SimpleDateFormat("hh:mm a");
                               String result1 = resultParse.format(date1);
                               result1 = result1.replace("pm", "PM");
                               mTimebtn.setText(result1);
                               finaltime = mTimebtn.getText().toString();
                           } catch (Exception ex) {
                               Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                           }
                       }
                       if (time.contains("AM")) {
                           try {
                               String parsePatter = time.contains(":") ? "hh:mm a" : "hh m a";
                               SimpleDateFormat parser = new SimpleDateFormat(parsePatter);
                               Date date1 = parser.parse(time);

                               // Result
                               SimpleDateFormat resultParse = new SimpleDateFormat("hh:mm a");
                               String result1 = resultParse.format(date1);
                               result1 = result1.replace("am", "AM");
                               mTimebtn.setText(result1);
                               finaltime = mTimebtn.getText().toString();
                           } catch (Exception ex) {
                               Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                           }
                       }

                       if (time.contains("PM")) {
                           try {
                               String parsePatter = time.contains(":") ? "hh:mm a" : "hh m a";
                               SimpleDateFormat parser = new SimpleDateFormat(parsePatter);
                               Date date1 = parser.parse(time);
                               SimpleDateFormat resultParse = new SimpleDateFormat("HH:mm a");
                               String result1 = resultParse.format(date1);
                               result1 = result1.replace("pm", "PM");
                               mTimebtn.setText(result1);
                               finaltime = mTimebtn.getText().toString();
                           } catch (Exception ex) {
                               Toast.makeText(getApplicationContext(), time, Toast.LENGTH_SHORT).show();
                           }
                       }

                       textToSpeech.speak("Tell me the task that you want to remember", TextToSpeech.QUEUE_FLUSH, null);

                       break;

                   case 3:
                       String task = result.get(0);
                       mTitledit.setText(task);
                       title = mTitledit.getText().toString().trim();                               //access the data from the input field
                       if (title.isEmpty()) {
                           Toast.makeText(getApplicationContext(), "Please Enter text", Toast.LENGTH_SHORT).show();   //shows the toast if input field is empty
                       } else {
                           if (mDatebtn.getText().equals("") || mTimebtn.getText().equals("date")) {                                               //shows toast if date and time are not selected
                               Toast.makeText(getApplicationContext(), "Please select date and time", Toast.LENGTH_SHORT).show();
                           }

                       }
                       textToSpeech.speak("would you like to save the reminder", TextToSpeech.QUEUE_FLUSH, null);

                       break;
                   case 4:
                       if (result.get(0).contains("yes")) {
                           processinsert(title, mDatebtn.getText().toString(), finaltime);
                           textToSpeech.speak("Reminder successfully set.", TextToSpeech.QUEUE_FLUSH, null);
                           final Handler h = new Handler();
                           h.postDelayed(new Runnable() {
                               @Override
                               public void run() {
                                   Intent i = new Intent(ReminderActivity.this, ReminderActivity.class);
                                   startActivity(i);
                               }
                           }, 3000);
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
                       } else {
                           textToSpeech.speak("would you like to save the reminder", TextToSpeech.QUEUE_FLUSH, null);
                           layoutclick--;
                       }


                       break;
                   default:
                       textToSpeech.speak("There are some incorrect details. Restarting. ", TextToSpeech.QUEUE_FLUSH, null, "hii");
                       Intent i = getIntent();
                       finish();
                       startActivity(i);
                       if (result.toString().contains("delete all my reminders") || result.toString().contains("delete reminder")) {
                           Boolean delete = dbHandler.DeleteAllReminder();
                           if (delete) {
                               Toast.makeText(getApplicationContext(), "All reminders deleted", Toast.LENGTH_SHORT).show();
                               textToSpeech.speak("All reminders deleted", TextToSpeech.QUEUE_FLUSH, null);
                           } else {
                               Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();

                           }
                       }
                       if (result.get(0).equals("exit")) {
                           finishAffinity();
                           System.exit(0);
                       }
                       if (result.get(0).equals("read")) {
                           Intent intent = new Intent(getApplicationContext(), OCRReader.class);
                           startActivity(intent);
                       } else {
                           textToSpeech.speak("Do not understand just Swipe right Say again", TextToSpeech.QUEUE_FLUSH, null);

                       }
                       if (result.get(0).equals("calculator")) {
                           Intent intent = new Intent(getApplicationContext(), Calculator.class);
                           startActivity(intent);
                       } else {
                           textToSpeech.speak("Do not understand just Swipe right Say again", TextToSpeech.QUEUE_FLUSH, null);
                       }
                       if (result.get(0).equals("time and date")) {
                           Intent intent = new Intent(getApplicationContext(), DateAndTime.class);
                           startActivity(intent);
                       } else {
                           textToSpeech.speak("Do not understand just Swipe right  Say again", TextToSpeech.QUEUE_FLUSH, null);
                       }
                       if (result.get(0).equals("weather")) {
                           Intent intent = new Intent(getApplicationContext(), Weather.class);
                           startActivity(intent);
                       } else {
                           textToSpeech.speak("Do not understand just Swipe right Say again", TextToSpeech.QUEUE_FLUSH, null);
                       }

                       if (result.get(0).equals("battery")) {
                           Intent intent = new Intent(getApplicationContext(), Battery.class);
                           startActivity(intent);
                       } else {
                           textToSpeech.speak("Do not understand Swipe right Say again", TextToSpeech.QUEUE_FLUSH, null);
                       }
                       if (result.get(0).equals("yes")) {
                           textToSpeech.speak("  Say Read for reading,  calculator for calculator,  time and date,  weather for weather,  battery for battery. Do you want to listen again", TextToSpeech.QUEUE_FLUSH, null);
                       } else if ((result.get(0).equals("no"))) {
                           textToSpeech.speak("then Swipe right and say what you want", TextToSpeech.QUEUE_FLUSH, null);

                       } else if (result.get(0).equals("location")) {

                           Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
                           startActivity(intent);
                       }

                       if (result.get(0).contains("bank transfer")) {
                           Intent i1 = new Intent(ReminderActivity.this, Banktransfer.class);
                           startActivity(i1);
                       } else if (result.get(0).contains("phone transfer")) {
                           Intent i1 = new Intent(ReminderActivity.this, phonetransfer.class);
                           startActivity(i1);
                       } else {
                           textToSpeech.speak("Do not understand just Swipe right Say again", TextToSpeech.QUEUE_FLUSH, null);
                       }
                       if (result.get(0).contains("object detection")) {
                           Intent i1 = new Intent(ReminderActivity.this, MainActivity.class);
                           startActivity(i1);
                       }
                       if(result.get(0).contains("navigation")){
                           Intent i1 = new Intent(ReminderActivity.this, Navigation.class);
                           startActivity(i1);
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

                       if (result.get(0).contains("create reminder")||result.get(0).contains("reminder")||result.get(0).contains("reminders")) {
                           Intent i1 = new Intent(ReminderActivity.this, Reminder.class);
                           startActivity(i1);
                       }
                       if (result.get(0).contains("scan the QR") || result.get(0).contains("QR")) {
                           Intent i1 = new Intent(ReminderActivity.this, QRactivity.class);
                           startActivity(i1);
                       }
                       if (result.get(0).contains("exit")) {
                            finishAffinity();
                       }

               }

            }
        }

    }
    public void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
        super.onPause();
     }

}
