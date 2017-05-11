package fr.vincentmazet.smartalarmclock;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.provider.Settings;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.security.Permission;
import java.util.Locale;

import fr.vincentmazet.smartalarmclock.Service.WeatherApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public  static final int RC_SETTING = 666;
    private SettingsApp settingsApp;
    private String userUid;
    private DatabaseReference refUser;

    private LinearLayout baseLayout;
    private CardView card1, card2, card3, card4, card5, card6;
    private FirebaseDatabase database;
    public static final int RC_SIGN_IN = 455;
    private GoogleApiClient googleApiClient;
    FirebaseUser user;

    private TextView resultWeather,cityTextView;
    private ImageView imageViewWeather;

    private Button buttonSettings;
    private double longitude,latitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        Log.d("TAG", "Latitude longitude" + latitude + " " + longitude);
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

        this.overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_right);
        cityTextView = (TextView) findViewById(R.id.cityTextView);
        resultWeather = (TextView) findViewById(R.id.weatherTextView);
        imageViewWeather = (ImageView) findViewById(R.id.imageViewWeather);

        WeatherApi api = WeatherApi.retrofit.create(WeatherApi.class);
        Call weather = api.getWeatherObject(Double.toString(latitude),Double.toString(longitude),getResources().getString(R.string.weather_key));
        weather.enqueue(new Callback<WeatherObject>() {
            @Override
            public void onResponse(Call<WeatherObject> call, Response<WeatherObject> response) {
                cityTextView.setText(response.body().getName());
                resultWeather.setText(response.body().getMain().getTemp() + "°C\n"+frenchDescription(response.body().getWeather().get(0).getDescription()));
                imageViewWeather.setVisibility(View.VISIBLE);
                Picasso.with(MainActivity.this)
                        .load("http://openweathermap.org/img/w/" + response.body().getWeather().get(0).getIcon() + ".png")
                        .into(imageViewWeather);
            }

            @Override
            public void onFailure(Call<WeatherObject> call, Throwable t) {
                resultWeather.setText("Erreur lors de la récupération de la météo");
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(getApplication())) {
                }
            else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }

        buttonSettings = (Button) findViewById(R.id.buttonSettings);

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), SettingsActivity.class);
                startActivityForResult(myIntent,RC_SETTING);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , this )
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    signIn();
                }else{
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
                }
            }
        };

        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SETTING){
            if(resultCode == 999){
                signIn();
                signOut();
            }
        }

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        this);

                // set title
                alertDialogBuilder.setTitle("Connexion");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Vous devez vous connecter pour pouvoir utiliser cette application.")
                        .setCancelable(false)
                        .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                signIn();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
            googleApiClient.isConnected();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //updateUI(null);
                        }

                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signOut() {
        /*googleApiClient.isConnected();
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        String test = status.getStatusMessage();
                    }
                });*/
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
    }

    private void findView(){
        buttonSettings = (Button) findViewById(R.id.buttonSettings);
        card1 = (CardView) findViewById(R.id.mainCard1);
        card2 = (CardView) findViewById(R.id.mainCard2);
        card3 = (CardView) findViewById(R.id.mainCard3);
        card4 = (CardView) findViewById(R.id.mainCard4);
        card5 = (CardView) findViewById(R.id.mainCard5);
        card6 = (CardView) findViewById(R.id.mainCard6);
        baseLayout = (LinearLayout) findViewById(R.id.mainBaseLayout);
    }

}
