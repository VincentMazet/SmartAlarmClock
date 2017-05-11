package fr.vincentmazet.smartalarmclock;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.provider.Settings;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by iem on 04/05/2017.
 */

public class SettingsActivity extends Activity {

    private Button buttonBackHome;
    private CheckBox checkBoxWeather;
    private FirebaseDatabase database;
    private SettingsApp settingsApp;
    private String userUid;
    private DatabaseReference refUser;

    private LinearLayout orangeTheme, greenTheme, greyTheme;
    private LinearLayout baseLayout;
    private CardView card1, card2, card3, card4, card5, card6;

    private SeekBar luminositySeekBar;
    private int brightness;
    private CheckBox checkboxDisabledAlarm;
    private Spinner spinner;
    private List<String> listDefaultText = new ArrayList<>();
    private ArrayAdapter<String> dataAdapter;
    private EditText editTextCustumMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        this.overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_left);

        findView();

        database = FirebaseDatabase.getInstance();
        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        refUser = database.getReference().child("Datas").child(userUid).getRef();
        refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    settingsApp = new SettingsApp();
                    dataSnapshot.getRef().setValue(settingsApp);
                } else {
                    settingsApp = dataSnapshot.getValue(SettingsApp.class);
                }
                updateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference prerecordTextRef = database.getReference().child("Prerecord_text").getRef();
        prerecordTextRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                int index;
                try {
                    index = Integer.parseInt(dataSnapshot.getKey());
                }catch (Exception e){
                    index = -1;
                }
                if(index != -1) {
                    listDefaultText.add(index,dataSnapshot.getValue(String.class));
                    dataAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int index;
                try {
                    index = Integer.parseInt(dataSnapshot.getKey());
                }catch (Exception e){
                    index = -1;
                }
                if(index != -1) {
                    listDefaultText.set(index,dataSnapshot.getValue(String.class));
                    dataAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                int index;
                try {
                    index = Integer.parseInt(dataSnapshot.getKey());
                }catch (Exception e){
                    index = -1;
                }
                if(index != -1) {
                    listDefaultText.remove(index);
                    dataAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listDefaultText);
        spinner.setAdapter(dataAdapter);


        /*spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                settingsApp.prerecordTextId = (int) id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                settingsApp.prerecordTextId = 0;
            }
        });*/


        buttonBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        checkBoxWeather.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsApp.enableWeather = isChecked;
            }
        });

        orangeTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsApp.theme = 1;
                switchTheme();
            }
        });

        greenTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsApp.theme = 2;
                switchTheme();
            }
        });

        greyTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsApp.theme = 3;
                switchTheme();
            }
        });

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

        luminositySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    settingsApp.luminosity = progress;
                                setBrightness(progress);
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
                                                        checkboxDisabledAlarm.setText(hourOfDay + ":" +  minute);
                                                        settingsApp.enableAlarm = true;
                                                        settingsApp.hour = hourOfDay;
                                                        settingsApp.minutes = minute;
                                                    }
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                                        timePickerDialog.show();
                    
                                            } else {
                                        checkboxDisabledAlarm.setText("Disabled");
                                        settingsApp.enableAlarm = false;
                                    }
            }
        });
    }

    private void setBrightness(int progress){
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
    

    private void findView(){
        card1 = (CardView) findViewById(R.id.card1);
        card2 = (CardView) findViewById(R.id.card2);
        card3 = (CardView) findViewById(R.id.card3);
        card4 = (CardView) findViewById(R.id.card4);
        card5 = (CardView) findViewById(R.id.card5);
        card6 = (CardView) findViewById(R.id.card6);
        baseLayout = (LinearLayout) findViewById(R.id.baseLayout);
        buttonBackHome = (Button) findViewById(R.id.buttonBackHome);
        checkBoxWeather = (CheckBox) findViewById(R.id.checkboxWeather);
        orangeTheme = (LinearLayout) findViewById(R.id.orangeTheme);
        greenTheme = (LinearLayout) findViewById(R.id.greenTheme);
        greyTheme = (LinearLayout) findViewById(R.id.greyTheme);
        luminositySeekBar = (SeekBar) findViewById(R.id.luminositySeekBar);
        checkboxDisabledAlarm = (CheckBox) findViewById(R.id.checkboxDisabledAlarm);
        spinner = (Spinner) findViewById(R.id.spinner);
        editTextCustumMsg = (EditText) findViewById(R.id.editTextCustumMsg);
    }

    private void switchTheme(){
        int color1 = Color.parseColor(Theme.ORANGE.getColor1());;
        int color2 = Color.parseColor(Theme.ORANGE.getColor2());;
        switch (settingsApp.theme){
            case 2:
                color1 = Color.parseColor(Theme.GREEN.getColor1());
                color2 = Color.parseColor(Theme.GREEN.getColor2());
                break;
            case 3:
                color1 = Color.parseColor(Theme.GREY.getColor1());
                color2 = Color.parseColor(Theme.GREY.getColor2());
                break;
        }

        baseLayout.setBackgroundColor(color1);

        card1.setCardBackgroundColor(color2);
        card2.setCardBackgroundColor(color2);
        card3.setCardBackgroundColor(color2);
        card4.setCardBackgroundColor(color2);
        card5.setCardBackgroundColor(color2);
        card6.setCardBackgroundColor(color2);
    }

    private void updateUI(){
        switchTheme();
        checkBoxWeather.setChecked(settingsApp.enableWeather);
        if(settingsApp.customMessage.length() > 1){
            editTextCustumMsg.setText(settingsApp.customMessage);
        }
        spinner.setSelection(settingsApp.prerecordTextId);
        setBrightness(settingsApp.luminosity);
    }

    @Override
    protected void onPause() {
        super.onPause();
        settingsApp.customMessage = editTextCustumMsg.getText().toString();
        refUser.setValue(settingsApp);
    }

}
