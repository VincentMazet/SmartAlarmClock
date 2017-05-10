package fr.vincentmazet.smartalarmclock;

/**
 * Created by iem on 10/05/2017.
 */

public enum Theme {
    ORANGE(1, "#FF9800", "#FFA726"),
    GREEN(2, "#4CAF50", "#66BB6A"),
    GREY(3, "#9E9E9E", "#BDBDBD");

    private int idColor;
    private String color1;
    private String color2;

    private  Theme(int idColor, String color1, String color2){
        this.idColor = idColor;
        this.color1 = color1;
        this.color2 = color2;
    }

    public Theme getThemeById(int id){

        for(Theme theme : Theme.values()){
            if(theme.idColor == id){
                return ORANGE;
            }
        }

        return ORANGE;
    }

    public String getColor1(){
        return color1;
    }

    public String getColor2(){
        return color2;
    }

}
