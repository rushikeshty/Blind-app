package org.tensorflow.lite.examples.detection.Reminder;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.examples.detection.R;

import java.util.Locale;

//this class creates the Reminder Notification Message

public class NotificationMessage extends AppCompatActivity {
    TextView textView;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_message);
        textView = findViewById(R.id.tv_message);
        textView.setText(AlarmService.message);
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.getDefault());
                    textToSpeech.setSpeechRate(0.9f);
                    textToSpeech.speak(AlarmService.message,TextToSpeech.QUEUE_FLUSH,null);
                }

            }
        });

    }
}