package com.project.harrison.tournamentmod;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harrison on 04-03-2018.
 */

public class Tournament implements Serializable {
    private List<String> teams;
    private int team_count,win,draw,loss,groups,qualifying_count,rounds,current_round,total_matches,current_matches,id;
    private String name,type,last_updated,winner;
    private boolean g_double,k_double,f_double,third_place,finished=false,button_text_is_default=true;
    private List<fixtures> fixtures=new ArrayList<>();
    private List<TeamStats> team_stats=new ArrayList<>();
    private List<TeamStats> team_stats_all=new ArrayList<>();
    private List<KnockoutStats> knockoutStatsList=new ArrayList<>();
    //added extra from main_screen used var
    private int selected=0;
    private int[] selected_index=new int[2];
    private List<String> Qualified_teams=new ArrayList<>();
    private List<String> Knocked_out_teams=new ArrayList<>();
    private List<fixtures> fix_to_add=new ArrayList<>();
    private List<String> pot1=new ArrayList<>();
    private List<String> pot2=new ArrayList<>();
    private boolean error=false,owner=true;
    private boolean cloud_transfer=false;
    private String firebase_location,key,admin_firebaseLocation,admin_key;


    public Tournament(){}


    public Tournament(List<String> teams,int team_count,int win,int draw,int loss,int groups,int qualifying_count,
                      String name,String type,boolean g_double,boolean k_double,boolean f_double,boolean third_place,int total_matches){
        this.teams=teams;
        this.team_count=team_count;
        this.win=win;this.draw=draw;this.loss=loss;this.groups=groups;this.qualifying_count=qualifying_count;
        this.name=name;this.type=type;
        this.g_double=g_double;this.k_double=k_double;this.f_double=f_double;this.third_place=third_place;
        this.total_matches=total_matches;
        this.current_matches=0;
    }

    public boolean isCloud_transfer() {
        return cloud_transfer;
    }

    public void setCloud_transfer(boolean cloud_transfer) {
        this.cloud_transfer = cloud_transfer;
    }

    public String getAdmin_firebaseLocation() {
        return admin_firebaseLocation;
    }

    public void setAdmin_firebaseLocation(String admin_firebaseLocation) {
        this.admin_firebaseLocation = admin_firebaseLocation;
    }

    public String getAdmin_key() {
        return admin_key;
    }

    public void setAdmin_key(String admin_key) {
        this.admin_key = admin_key;
    }

    public List<KnockoutStats> getKnockoutStatsList() {
        return knockoutStatsList;
    }

    public void setKnockoutStatsList(List<KnockoutStats> knockoutStatsList) {
        this.knockoutStatsList = knockoutStatsList;
    }

    public void setCurrent_matches(int current_matches) {
        this.current_matches = current_matches;
    }

    public int getTotal_matches() {
        return total_matches;
    }

    public int getCurrent_matches() {
        return current_matches;
    }

    public void setTeams(List<String> teams) {
        this.teams = teams;
    }

    public void setTeam_stats(List<TeamStats> team_stats) {
        this.team_stats = team_stats;
    }

    public List<TeamStats> getTeam_stats() {
        return team_stats;
    }

    public List<com.project.harrison.tournamentmod.fixtures> getFixtures() {
        return fixtures;
    }

    public void setFixtures(List<com.project.harrison.tournamentmod.fixtures> fixtures) {
        this.fixtures = fixtures;
    }

    public boolean isF_double() {
        return f_double;
    }

    public boolean isG_double() {
        return g_double;
    }

    public boolean isK_double() {
        return k_double;
    }

    public boolean isThird_place() {
        return third_place;
    }

    public int getDraw() {
        return draw;
    }

    public void setTeam_count(int team_count) {
        this.team_count = team_count;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public void setLoss(int loss) {
        this.loss = loss;
    }

    public void setGroups(int groups) {
        this.groups = groups;
    }

    public void setQualifying_count(int qualifying_count) {
        this.qualifying_count = qualifying_count;
    }

    public void setTotal_matches(int total_matches) {
        this.total_matches = total_matches;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setG_double(boolean g_double) {
        this.g_double = g_double;
    }

    public void setK_double(boolean k_double) {
        this.k_double = k_double;
    }

    public void setF_double(boolean f_double) {
        this.f_double = f_double;
    }

    public void setThird_place(boolean third_place) {
        this.third_place = third_place;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isButton_text_is_default() {
        return button_text_is_default;
    }

    public void setTeam_stats_all(List<TeamStats> team_stats_all) {
        this.team_stats_all = team_stats_all;
    }

    public int[] getSelected_index() {
        return selected_index;
    }

    public void setSelected_index(int[] selected_index) {
        this.selected_index = selected_index;
    }

    public void setQualified_teams(List<String> qualified_teams) {
        Qualified_teams = qualified_teams;
    }

    public void setKnocked_out_teams(List<String> knocked_out_teams) {
        Knocked_out_teams = knocked_out_teams;
    }

    public void setFix_to_add(List<com.project.harrison.tournamentmod.fixtures> fix_to_add) {
        this.fix_to_add = fix_to_add;
    }


    public void setPot1(List<String> pot1) {
        this.pot1 = pot1;
    }

    public void setPot2(List<String> pot2) {
        this.pot2 = pot2;
    }

    public boolean isError() {
        return error;
    }

    public boolean isOwner() {
        return owner;
    }

    public int getGroups() {
        return groups;
    }

    public int getLoss() {
        return loss;
    }

    public int getQualifying_count() {
        return qualifying_count;
    }

    public List<String> getTeams() {
        return teams;
    }

    public int getTeam_count() {
        return team_count;
    }

    public int getWin() {
        return win;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getCurrent_round() {
        return current_round;
    }

    public int getRounds() {
        return rounds;
    }

    public void setCurrent_round(int current_round) {
        this.current_round = current_round;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean getFinished()
    {
        return finished;
    }

    public void setButton_text_is_default(boolean button_text_is_default) {
        this.button_text_is_default = button_text_is_default;
    }

    public boolean getButton_text_is_default() {
        return this.button_text_is_default;
    }

    public int getSelected() {
        return selected;
    }

    public int getSelected_index0() {
        return selected_index[0];
    }
    public int getSelected_index1() {
        return selected_index[1];
    }

    public List<fixtures> getFix_to_add() {
        return fix_to_add;
    }

    public List<String> getPot1() {
        return pot1;
    }

    public List<String> getPot2() {
        return pot2;
    }

    public List<String> getQualified_teams() {
        return Qualified_teams;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public void setSelected_index0(int selected_index) {
        this.selected_index[0] = selected_index;
    }
    public void setSelected_index1(int selected_index) {
        this.selected_index[1] = selected_index;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public boolean getError() {
        return this.error;
    }

    public List<String> getKnocked_out_teams() {
        return Knocked_out_teams;
    }

    public List<TeamStats> getTeam_stats_all() {
        return team_stats_all;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public boolean getOwner() {
        return owner;
    }

    public String getFirebase_location() {
        return firebase_location;
    }

    public String getKey() {
        return key;
    }

    public void setFirebase_location(String firebase_location) {
        this.firebase_location = firebase_location;
    }

    public void setKey(String key) {
        this.key = key;
    }
}


