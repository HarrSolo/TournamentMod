package com.project.harrison.tournamentmod;

import java.io.Serializable;

/**
 * Created by Harrison on 06-03-2018.
 */

public class fixtures implements Serializable {
    String[] teams=new String[2];
    int[] score=new int[2];
    String status;
    int round;

    public fixtures(){}
    public fixtures(int round,String team1,String team2,int score1,int score2,String status)
    {
        this.round=round;
        this.teams[0]=team1;
        this.teams[1]=team2;
        this.score[0]=score1;
        this.score[1]=score2;
        this.status=status;
    }

    public int getRound() {
        return round;
    }

    public String getTeam1(){
        return teams[0];
    }
    public String getTeam2(){
        return teams[1];
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String[] getTeams() {
        return teams;
    }

    public String getStatus() {
        return status;
    }

    public int[] getScore() {
        return score;
    }

    public int getScore1(){return score[0];}
    public int getScore2(){return score[1];}

    public void setScore(int[] score) {
        this.score = score;
    }

    public int getOutcome(){
        if(score[0]>score[1])
            return 0;
        else if(score[0]<score[1])
            return 1;
        else
            return -1;
    }
}
