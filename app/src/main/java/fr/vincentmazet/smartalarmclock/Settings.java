package fr.vincentmazet.smartalarmclock;

/**
 * Created by iem on 10/05/2017.
 */

public class Settings {
    public int luminosity = 100;

    public int theme = 1;

    public boolean enableAlarm = false;

    public int hour = 10;

    public int minutes = 0;

    public String customMessage = "";

    public boolean enableWeather = false;

    //need for Firebase
    public Settings() {
    }
}
