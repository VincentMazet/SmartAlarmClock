package fr.vincentmazet.smartalarmclock;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

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
import java.util.Locale;

import fr.vincentmazet.smartalarmclock.Service.WeatherApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by iem on 04/05/2017.
 */

public class SettingsActivity extends Activity {

    private Button buttonBackHome;
    private CheckBox checkBoxWeather;
    private SettingsApp settingsApp;
    private String userUid;
    private String userId;
    private DatabaseReference refUser;
    private double longitude,latitude;
    private FirebaseDatabase database;
    private LinearLayout orangeTheme, greenTheme, greyTheme;
    private LinearLayout baseLayout;
    private CardView card1, card2, card3, card4, card5, card6;
    private TextToSpeech textToSpeech;
    private String temp,cityName,meteoStatus;

    private SeekBar luminositySeekBar;
    private int brightness;
    private CheckBox checkboxDisabledAlarm;
    private Spinner spinner;
    private List<String> listDefaultText = new ArrayList<>();
    private ArrayAdapter<String> dataAdapter;
    private EditText editTextCustomMsg;
    private Button buttonDisconnect;
    private boolean synchronisation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        luminositySeekBar = (SeekBar) findViewById(R.id.luminositySeekBar);
        checkboxDisabledAlarm = (CheckBox) findViewById(R.id.checkboxDisabledAlarm);
        findView();
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        LocationListener locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }

        };
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);


        WeatherApi api = WeatherApi.retrofit.create(WeatherApi.class);
        Call weather = api.getWeatherObject(Double.toString(latitude),Double.toString(longitude),getResources().getString(R.string.weather_key));
        weather.enqueue(new Callback<WeatherObject>() {
            @Override
            public void onResponse(Call<WeatherObject> call, Response<WeatherObject> response) {
                cityName = response.body().getName();
                temp = Float.toString(response.body().getMain().getTemp());
                meteoStatus = frenchDescription(response.body().getWeather().get(0).getDescription());
            }

            @Override
            public void onFailure(Call<WeatherObject> call, Throwable t) {

            }
        });


        textToSpeech=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.getDefault());
                }
            }
        });

        editTextCustomMsg.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                textToSpeech=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status != TextToSpeech.ERROR) {
                            textToSpeech.setLanguage(Locale.getDefault());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                textToSpeech.speak(editTextCustomMsg.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, null);
                            } else {
                                textToSpeech.speak(editTextCustomMsg.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                            }
                        }
                    }
                });

                InputMethodManager inputManager = (InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.toggleSoftInput(0, 0);
                return true;
            }
        });




        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_left);



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
                synchronisation = true;
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
                } catch (Exception e) {
                    index = -1;
                }
                if (index != -1) {
                    listDefaultText.add(index, dataSnapshot.getValue(String.class));
                    dataAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int index;
                try {
                    index = Integer.parseInt(dataSnapshot.getKey());
                } catch (Exception e) {
                    index = -1;
                }
                if (index != -1) {
                    listDefaultText.set(index, dataSnapshot.getValue(String.class));
                    dataAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                int index;
                try {
                    index = Integer.parseInt(dataSnapshot.getKey());
                } catch (Exception e) {
                    index = -1;
                }
                if (index != -1) {
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


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (settingsApp != null)
                    settingsApp.prerecordTextId = (int) id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (settingsApp != null)
                    settingsApp.prerecordTextId = 0;
            }
        });


        buttonBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        checkBoxWeather.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    textToSpeech=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if(status != TextToSpeech.ERROR) {
                                textToSpeech.setLanguage(Locale.getDefault());
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    textToSpeech.speak("Aujourd'hui "+ meteoStatus +" à "+cityName+", il fera "+temp+"°", TextToSpeech.QUEUE_FLUSH, null, null);
                                } else {
                                    textToSpeech.speak(editTextCustomMsg.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }
                        }
                    });

                }
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
                    if (!synchronisation) {
                        TimePickerDialog timePickerDialog = new TimePickerDialog(SettingsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, final int hourOfDay, final int minute) {
                                checkboxDisabledAlarm.setText(hourOfDay + ":" + minute);
                                settingsApp.enableAlarm = true;
                                settingsApp.hour = hourOfDay;
                                settingsApp.minutes = minute;
                            }
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                        timePickerDialog.show();
                    }else{
                        checkboxDisabledAlarm.setText(settingsApp.hour + ":" + settingsApp.minutes);
                        synchronisation = false;
                    }


                } else {
                    checkboxDisabledAlarm.setText("Disabled");
                    settingsApp.enableAlarm = false;
                }
            }
        });

        buttonDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(999);
                finish();
            }
        });

    }

    private void setBrightness(int progress) {
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


    private void findView() {
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
        editTextCustomMsg = (EditText) findViewById(R.id.editTextCustomMsg);
        buttonDisconnect = (Button) findViewById(R.id.btDisconnect);
    }

    private void switchTheme() {
        int color1 = Color.parseColor(Theme.ORANGE.getColor1());
        ;
        int color2 = Color.parseColor(Theme.ORANGE.getColor2());
        ;
        switch (settingsApp.theme) {
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

    private void updateUI() {
        switchTheme();
        checkBoxWeather.setChecked(settingsApp.enableWeather);
        if (settingsApp.customMessage.length() > 1) {
            editTextCustomMsg.setText(settingsApp.customMessage);
        }
        spinner.setSelection(settingsApp.prerecordTextId);
        setBrightness(settingsApp.luminosity);
        checkboxDisabledAlarm.setChecked(settingsApp.enableAlarm);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(textToSpeech !=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (settingsApp != null) {
            settingsApp.customMessage = editTextCustomMsg.getText().toString();
            refUser.setValue(settingsApp);
        }

    }

    @Override
    public void onStop() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }


    private String frenchDescription(String description){
        switch (description){
            case "thunderstorm with light rain":
                return "Orage avec pluie légère";
            case "thunderstorm with rain":
                return "Orage avec pluie";
            case "thunderstorm with heavy rain":
                return "Orage avec de fortes pluies";
            case "light thunderstorm":
                return"Orage léger";
            case "thunderstorm":
                return "Orage";
            case "heavy thunderstorm":
                return "Gros orage";
            case "ragged thunderstorm":
                return "Orage violent";
            case "thunderstorm with light drizzle":
                return "Orage avec légère bruine";
            case "thunderstorm with drizzle":
                return "Orage avec bruine";
            case "thunderstorm with heavy drizzle":
                return "Orage avec forte bruine";
            case "light intensity drizzle":
                return "Légère bruine";
            case "drizzle":
                return "Bruine";
            case "heavy intensity drizzle":
                return "Lourde bruine";
            case "light intensity drizzle rain":
                return "Pluies légeremment bruineuse";
            case "drizzle rain":
                return "Pluies bruineuse";
            case "heavy intensity drizzle rain":
                return "Lourde pluie bruineuse";
            case "shower rain and drizzle":
                return "Averses et bruine";
            case "heavy shower rain and drizzle":
                return "Grosses averses et bruine";
            case "shower drizzle":
                return "Averses bruineuse";
            case "light rain":
                return "Pluies légère";
            case "moderate rain":
                return "Pluies modérée";
            case "heavy intensity rain":
                return "Pluies intense";
            case "very heavy rain":
                return "Pluies très intense";
            case "extreme rain":
                return "Pluies extreme";
            case "freezing rain":
                return "Pluies verglaçante";
            case "light intensity shower rain":
                return "Pluies avec légères averses";
            case "shower rain":
                return "Averses";
            case "heavy intensity shower rain":
                return "Grosses Averses";
            case "ragged shower rain":
                return "Averses violentes";
            case "light snow":
                return "Neige légère";
            case "snow":
                return "Neige";
            case "heavy snow":
                return "Neige abondante";
            case "sleet":
                return "Neige fondue";
            case "shower sleet":
                return "Averses de neige fondue";
            case "light rain and snow":
                return "Pluies légères et neige";
            case "rain and snow":
                return "Pluies et neige";
            case "light shower snow":
                return "Petites averses de neige";
            case "shower snow":
                return "Averses de neige";
            case "heavy shower snow":
                return "Lourdes averses de neige";
            case "mist":
                return "Brouillard";
            case "smoke":
                return "Brouillard lourd";
            case "haze":
                return "Brume";
            case "sand, dust whirls":
                return "Sable, tourbillons de poussière";
            case "fog":
                return "Brouillard";
            case "sand":
                return "Sable";
            case "dust":
                return "Poussière";
            case "volcanic ash":
                return "Poussières volcaniques";
            case "squalls":
                return "Rafales";
            case "tornado":
                return "Tornade";
            case "clear sky":
                return "Ciel éclairé";
            case "few clouds":
                return "Quelques nuages";
            case "scattered clouds":
                return "Nuages dispersés";
            case "broken clouds":
                return "Nuages brisées";
            case "overcast clouds":
                return "Nuages couverts";
            case "tropical storm":
                return "Tempête tropicale";
            case "hurricane":
                return "Ouragan";
            case "cold":
                return "Froid";
            case "hot":
                return "Chaud";
            case "windy":
                return "Venteux";
            case "hail":
                return "Je connais pas la traduction désolé";


        }
        return "Problème lors de la récupération de la météo";
    }

}
