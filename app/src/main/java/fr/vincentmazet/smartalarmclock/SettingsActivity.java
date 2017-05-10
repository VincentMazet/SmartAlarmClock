package fr.vincentmazet.smartalarmclock;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by iem on 04/05/2017.
 */

public class SettingsActivity extends Activity {

    private Button buttonBackHome;
    private SeekBar luminositySeekBar;
    private int brightness;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    private CheckBox checkboxDisabledAlarm;
    private String userId;

    private LinearLayout orangeTheme, greenTheme, greyTheme;
    private LinearLayout baseLayout;
    private CardView card1, card2, card3, card4, card5, card6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        luminositySeekBar = (SeekBar) findViewById(R.id.luminositySeekBar);
        checkboxDisabledAlarm = (CheckBox) findViewById(R.id.checkboxDisabledAlarm);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_left);
        card1 = (CardView) findViewById(R.id.card1);
        card2 = (CardView) findViewById(R.id.card2);
        card3 = (CardView) findViewById(R.id.card3);
        card4 = (CardView) findViewById(R.id.card4);
        card5 = (CardView) findViewById(R.id.card5);
        card6 = (CardView) findViewById(R.id.card6);

        baseLayout = (LinearLayout) findViewById(R.id.baseLayout);
        database = FirebaseDatabase.getInstance();

        Settings.System.putInt(
                getApplication().getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        try {
            brightness =
                    Settings.System.getInt(
                            getApplication().getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        luminositySeekBar.setProgress(brightness);

        buttonBackHome = (Button) findViewById(R.id.buttonBackHome);
        buttonBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivity(myIntent);
            }
        });

        orangeTheme = (LinearLayout) findViewById(R.id.orangeTheme);
        orangeTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                baseLayout.setBackgroundColor(Color.parseColor(Theme.ORANGE.getColor1()));

                card1.setCardBackgroundColor(Color.parseColor(Theme.ORANGE.getColor2()));
                card2.setCardBackgroundColor(Color.parseColor(Theme.ORANGE.getColor2()));
                card3.setCardBackgroundColor(Color.parseColor(Theme.ORANGE.getColor2()));
                card4.setCardBackgroundColor(Color.parseColor(Theme.ORANGE.getColor2()));
                card5.setCardBackgroundColor(Color.parseColor(Theme.ORANGE.getColor2()));
                card6.setCardBackgroundColor(Color.parseColor(Theme.ORANGE.getColor2()));
            }
        });


        greenTheme = (LinearLayout) findViewById(R.id.greenTheme);
        greenTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                baseLayout.setBackgroundColor(Color.parseColor(Theme.GREEN.getColor1()));

                card1.setCardBackgroundColor(Color.parseColor(Theme.GREEN.getColor2()));
                card2.setCardBackgroundColor(Color.parseColor(Theme.GREEN.getColor2()));
                card3.setCardBackgroundColor(Color.parseColor(Theme.GREEN.getColor2()));
                card4.setCardBackgroundColor(Color.parseColor(Theme.GREEN.getColor2()));
                card5.setCardBackgroundColor(Color.parseColor(Theme.GREEN.getColor2()));
                card6.setCardBackgroundColor(Color.parseColor(Theme.GREEN.getColor2()));
            }
        });

        greyTheme = (LinearLayout) findViewById(R.id.greyTheme);
        greyTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                baseLayout.setBackgroundColor(Color.parseColor(Theme.GREY.getColor1()));

                card1.setCardBackgroundColor(Color.parseColor(Theme.GREY.getColor2()));
                card2.setCardBackgroundColor(Color.parseColor(Theme.GREY.getColor2()));
                card3.setCardBackgroundColor(Color.parseColor(Theme.GREY.getColor2()));
                card4.setCardBackgroundColor(Color.parseColor(Theme.GREY.getColor2()));
                card5.setCardBackgroundColor(Color.parseColor(Theme.GREY.getColor2()));
                card6.setCardBackgroundColor(Color.parseColor(Theme.GREY.getColor2()));


            }


        });

        luminositySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                database.getReference().child("Datas").child(userId).child("luminosity").getRef().setValue(progress);
                Settings.System.putInt(getApplication().getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, progress);
                Settings.System.putInt(
                        getApplication().getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                try {
                    brightness =
                            Settings.System.getInt(
                                    getApplication().getContentResolver(),
                                    Settings.System.SCREEN_BRIGHTNESS);
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        Date date = new Date();
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        checkboxDisabledAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(SettingsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, final int hourOfDay, final int minute) {
                            checkboxDisabledAlarm.setText(hourOfDay + ":" + minute);
                            database.getReference().child("Datas").child(userId).child("hour").getRef().setValue(hourOfDay);
                            database.getReference().child("Datas").child(userId).child("minute").getRef().setValue(minute);
                            database.getReference().child("Datas").child(userId).child("clock_activated").getRef().setValue(true);
                        }
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                    timePickerDialog.show();

                } else {
                    checkboxDisabledAlarm.setText("Disabled");
                    database.getReference().child("Datas").child(userId).child("hour").getRef().setValue(0);
                    database.getReference().child("Datas").child(userId).child("minute").getRef().setValue(0);
                    database.getReference().child("Datas").child(userId).child("clock_activated").getRef().setValue(false);
                }
            }
        });

    }
}
