package com.project.harrison.tournamentmod;

import java.io.Serializable;

/**
 * Created by Harrison on 14-03-2018.
 */

public class KnockoutStats implements Serializable {
    private String team1,team2,status,result;
    int round;

    public KnockoutStats(int round,String team1,String team2,String status){
        this.team1=team1;
        this.team2=team2;
        this.status=status;
        this.round=round;
        this.result="VS";
    }

    public String getStatus() {
        return status;
    }

    public int getRound() {
        return round;
    }

    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    public void setTeam2(String team2) {
        this.team2 = team2;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTeam1() {
        return team1;
    }

    public String getTeam2() {
        return team2;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
