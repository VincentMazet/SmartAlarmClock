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
                settings = dataSnapshot.getValue(Settings.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        card1 = (CardView) findViewById(R.id.card1);
        card2 = (CardView) findViewById(R.id.card2);
        card3 = (CardView) findViewById(R.id.card3);
        card4 = (CardView) findViewById(R.id.card4);
        card5 = (CardView) findViewById(R.id.card5);
        card6 = (CardView) findViewById(R.id.card6);

        baseLayout = (LinearLayout) findViewById(R.id.baseLayout);



        buttonBackHome = (Button) findViewById(R.id.buttonBackHome);
        buttonBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        checkBoxWeather = (CheckBox) findViewById(R.id.checkboxWeather);
        checkBoxWeather.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.enableWeather = isChecked;
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

    }

    @Override
    protected void onPause() {
        super.onPause();
        refUser.setValue(settings);
    }

}
