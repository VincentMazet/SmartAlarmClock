package fr.vincentmazet.smartalarmclock;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.LinearLayout;

/**
 * Created by iem on 04/05/2017.
 */

public class SettingsActivity extends Activity {

    private Button buttonBackHome;
    private CheckBox checkBoxWeather;
    private CheckBox checkBoxAlarm;
    private FirebaseDatabase database;
    private Settings settings;
    private String userUid;
    private DatabaseReference refUser;

    private LinearLayout orangeTheme, greenTheme, greyTheme;
    private LinearLayout baseLayout;
    private CardView card1, card2, card3, card4, card5, card6;

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
                    settings = new Settings();
                    dataSnapshot.getRef().setValue(settings);
                }
                else{
                    settings = dataSnapshot.getValue(Settings.class);
                }
                updateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
                settings.enableWeather = isChecked;
            }
        });

        checkBoxAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.enableAlarm = isChecked;
            }
        });

        orangeTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.theme = 1;
                switchTheme();
            }
        });

        greenTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.theme = 2;
                switchTheme();
            }
        });

        greyTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.theme = 3;
                switchTheme();
            }
        });

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
        checkBoxAlarm = (CheckBox) findViewById(R.id.checkboxAlarm);
    }

    private void switchTheme(){
        int color1 = Color.parseColor(Theme.ORANGE.getColor1());;
        int color2 = Color.parseColor(Theme.ORANGE.getColor2());;
        switch (settings.theme){
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
        checkBoxWeather.setChecked(settings.enableWeather);
    }

    @Override
    protected void onPause() {
        super.onPause();
        refUser.setValue(settings);
    }

}
