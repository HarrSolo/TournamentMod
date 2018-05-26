package com.project.harrison.tournamentmod;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Harrison on 10-03-2018.
 */

public class TeamStats implements Serializable {
    private String name,status;
    int cs;
    private int[] stats=new int[6];
    //0-won 1-draw 2-lost 3-goals_for 4-goals_against 5-points

    public TeamStats(String name,String status){
        this.name=name;
        this.status=status;
        for(int i=0;i<6;i++)
            this.stats[i]=0;
        cs=0;
    }

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    public String getStatus() {
        return status;
    }

    public int getWins(){
        return stats[0];
    }
    public int getLoss(){
        return stats[2];
    }
    public int getDraw(){
        return stats[1];
    }

    public int[] getStats() {
        return stats;
    }

    public int getPoints(){return stats[5];}
    public int getGF(){return stats[3];}
    public int getGD(){return stats[3]-stats[4];}
    public int getGA(){return stats[4];}
    public  int getPlayed()
    {
        return stats[0]+stats[1]+stats[2];
    }

    public float off_rate(){
        float rate;
        if(getPlayed()>0 && getGF()>0)
           rate=((float)getGF())/((float)getPlayed());
        else rate=0;

        return round(rate,2);
    }

    public float def_rate(){
        float rate;
        if(getPlayed()>0 && getGA()>0)
            rate=((float)getGA())/((float)getPlayed());
        else rate=0;

        return round(rate,2);
    }

    public int getCs() {
        return cs;
    }

    public void setCs(int cs) {
        this.cs = cs;
    }

    public String getName() {
        return name;
    }

    public void setStats(int[] stats) {
        this.stats = stats;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
