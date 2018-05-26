package com.project.harrison.tournamentmod;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.SerializationUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.R.attr.editable;
import static android.R.id.list;
import static com.project.harrison.tournamentmod.R.id.decor_content_parent;
import static com.project.harrison.tournamentmod.R.id.n;
import static com.project.harrison.tournamentmod.R.id.score1;
import static com.project.harrison.tournamentmod.R.id.score2;
import static com.project.harrison.tournamentmod.R.id.status;
import static com.project.harrison.tournamentmod.R.id.tab;
import static com.project.harrison.tournamentmod.R.id.teams;
import static com.project.harrison.tournamentmod.R.id.top;

/**
 * Created by Harrison on 06-03-2018.
 */

public class main_screen extends Activity {

    TabLayout tabLayout;
    ListView main_listView;
    Tournament details;
    firebase_data fb_data;
    Button save,host;
    TextView tour_name,progress;
    Button to_next_round_button;
    fixture_adpater adapter;
    KnockoutPhaseAdapter adapter_knockout;
    standings_adapter s_adapter;
    stats_adapter stats_adap;
    List<String> topics=new ArrayList<>();
    private FirebaseDatabase mFirebasedatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private DatabaseReference mDatabaseReferenceUpdate;
    private FirebaseAuth mFireBaseAuth;
    Animation anim_blink;
    public static final int RC_SIGN_IN = 1;
    String userId;
    /*int selected=0;int[] selected_index=new int[2];
    List<String> Qualified_teams=new ArrayList<>();
    List<fixtures> fix_to_add=new ArrayList<>();
    List<String> pot1=new ArrayList<>();
    List<String> pot2=new ArrayList<>();
    boolean error=false;*/
    DBHandler db;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
      //  super.onBackPressed();
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Are you sure you want to exit?");
        builder.setPositiveButton("STAY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
        mFirebasedatabase=FirebaseDatabase.getInstance();
        mDatabaseReference=mFirebasedatabase.getReference().child("Tournament");
        save=(Button)findViewById(R.id.save);
        host=(Button)findViewById(R.id.host);
        //getting details class
        Intent i=getIntent();
        details=(Tournament)i.getSerializableExtra("class");
        main_listView=(ListView)findViewById(R.id.main_listview);
        tour_name=(TextView)findViewById(R.id.tour_name_text);
        progress=(TextView)findViewById(R.id.progress);
        tour_name.setText(""+details.getName().toUpperCase());
        tabLayout = (TabLayout) findViewById(R.id.simpleTabLayout);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#F9A825"));
        tabLayout.setBackgroundColor(Color.parseColor("#101846"));
        to_next_round_button=(Button)findViewById(R.id.next_round);
        to_next_round_button.setEnabled(false);
        mFireBaseAuth=FirebaseAuth.getInstance();
        FirebaseUser user=mFireBaseAuth.getCurrentUser();
        userId=user.getUid();
        initialize();


        TabLayout.Tab group_tab = tabLayout.newTab();
        group_tab.setText("Group Standings");
        //tabLayout.addTab(group_tab);

        TabLayout.Tab fixture_tab = tabLayout.newTab();
        fixture_tab.setText("Fixtures");
       // tabLayout.addTab(fixture_tab);

        TabLayout.Tab knockout_tab = tabLayout.newTab();
        knockout_tab.setText("Knockout Phase");
       // tabLayout.addTab(knockout_tab);

        final TabLayout.Tab stats_tab = tabLayout.newTab();
        stats_tab.setText("Stats");
        //tabLayout.addTab(stats_tab);
        if(details.getType().equalsIgnoreCase("knockout"))
        {
           tabLayout.addTab(knockout_tab);
            tabLayout.addTab(fixture_tab);
            create_knockoutPhase_LV();
        }
        else if(details.getType().equalsIgnoreCase("league"))
        {

            tabLayout.addTab(group_tab);
            tabLayout.addTab(fixture_tab);
            create_standings_lv();
        }
        else
        {
            tabLayout.addTab(group_tab);
            tabLayout.addTab(fixture_tab);
            tabLayout.addTab(knockout_tab);
            create_standings_lv();
            //tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        }
        tabLayout.addTab(stats_tab);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getText().toString()) {
                    case "Group Standings" :
                          updateGroupStandings();
                          create_standings_lv();
                        break;
                    case "Fixtures":
                        create_fixture_lv();
                        break;
                    case "Knockout Phase":
                        create_knockoutPhase_LV();
                        break;
                    case "Stats":
                        create_stats_lv();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if(!details.isOwner())
        {
            mDatabaseReferenceUpdate=mFirebasedatabase.getReference().child("Tournament").child(details.getFirebase_location());
            mChildEventListener=new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    //firebase_data fb=dataSnapshot.getValue(firebase_data.class);
                    if(!details.isOwner()){
                        //Toast.makeText(getApplicationContext(),"Changes Updated",Toast.LENGTH_SHORT).show();
                        mDatabaseReference.child(details.getAdmin_firebaseLocation()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue()!=null){
                                    fb_data=dataSnapshot.getValue(firebase_data.class);
                                    Gson gson=new Gson();
                                    Tournament d=gson.fromJson(fb_data.getData(),new TypeToken<Tournament>(){}.getType());
                                    d.setOwner(false);
                                    details=d;
//                                    adapter_knockout.notifyDataSetChanged();
//                                    adapter.notifyDataSetChanged();
//                                    updateProgress();
//                                    if(details.getButton_text_is_default())
//                                        to_next_round_button.setText("TO NEXT ROUND");
//                                    else
//                                        to_next_round_button.setText("START ROUND");
//
//                                    if(details.getCurrent_round()>details.getRounds())
//                                    {
//                                        progress.setText("WINNER : "+details.getWinner());
//                                        Animation anim= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.blink);
//                                        progress.startAnimation(anim);
//                                    }
//                                    else if(checkCurrentRoundStatus() )
//                                    {
//                                        to_next_round_button.setEnabled(true);
//                                        to_next_round_button.setAlpha(1f);
//                                    }
//                                    else
//                                    {
//                                        to_next_round_button.setEnabled(false);
//                                        to_next_round_button.setAlpha(0f);
//                                    }
//
//                                    if(!details.getOwner())
//                                    {
//                                        save.setEnabled(false);
//                                        save.setAlpha(0f);
//                                        host.setEnabled(false);
//                                        host.setAlpha(0f);
//                                    }
//                                    else {
//                                        save.setEnabled(true);
//                                        save.setAlpha(1f);
//                                        host.setEnabled(true);
//                                        host.setAlpha(1f);
//                                    }
                                    initialize();
                                    updateOnSync(tabLayout.getSelectedTabPosition());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            mDatabaseReferenceUpdate.addChildEventListener(mChildEventListener);
        }



    }


    public void updateOnSync(int p)
    {
        if(p==1)
        {
            create_fixture_lv();
        }
        if(!details.getType().equalsIgnoreCase("knockout"))
        {
            if(p==0)
            {
                create_standings_lv();
            }
            else if(details.getType().equalsIgnoreCase("league") && p==2)
            {
                create_stats_lv();
            }
            else if(!details.getType().equalsIgnoreCase("league") && p==3)
            {
                create_stats_lv();
            }
        }
        else if(p==2)
        {
            create_stats_lv();
        }
    }

    public void toKnockoutTab()
    {
        TabLayout.Tab tab;
      if(details.getType().equalsIgnoreCase("knockout"))
      {
          tab=tabLayout.getTabAt(0);
      }
      else
      {
          tab=tabLayout.getTabAt(2);
      }
        tab.select();
    }
    public void toFixturesTab()
    {
        TabLayout.Tab tab;
        tab=tabLayout.getTabAt(1);
        tab.select();
    }
    public void toStandingsTab()
    {
        TabLayout.Tab tab;
        tab=tabLayout.getTabAt(0);
        tab.select();
    }

    public void initialize(){
        anim_blink=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.blink);
        db=new DBHandler(this);
        adapter_knockout=new KnockoutPhaseAdapter();
        adapter=new fixture_adpater();
        adapter_knockout.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
        updateProgress();
        if(details.getButton_text_is_default())
            to_next_round_button.setText("TO NEXT ROUND");
        else
            to_next_round_button.setText("START ROUND");

        if(details.getCurrent_round()>details.getRounds())
        {
            progress.setText("WINNER : "+details.getWinner());
            Animation anim= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.blink);
            progress.startAnimation(anim);
        }
        else if(checkCurrentRoundStatus() )
        {
            to_next_round_button.setEnabled(true);
            to_next_round_button.setAlpha(1f);
        }
        else
        {
            to_next_round_button.setEnabled(false);
            to_next_round_button.setAlpha(0f);
        }
        details.setCloud_transfer(false);

        if(!details.getOwner())
        {
            save.setEnabled(false);
            save.setAlpha(0f);
            host.setEnabled(false);
            host.setAlpha(0f);
        }
        else {
            save.setEnabled(true);
            save.setAlpha(1f);
            host.setEnabled(true);
            host.setAlpha(1f);
        }

        topics.add("BEST OFFENCE RATIO");
        topics.add("WORST OFFENCE RATIO");
        topics.add("BEST DEFENSE RATIO");
        topics.add("WORST DEFENCE RATIO");
        topics.add("MOST CLEAN SHEETS");
        topics.add("MOST WINS");
        topics.add("MOST DRAW");
        topics.add("MOST LOSS");
        topics.add("BEST GOAL DIFFERENCE");



    }

    public void save(View v)
    {

        Date curr_time= Calendar.getInstance().getTime();
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yy @ HH:mm");
        String time=sdf.format(curr_time);
        details.setLast_updated(time);
        //db.insert(details);
        //Toast.makeText(getApplicationContext(),"Saved Successfully "+time,Toast.LENGTH_SHORT).show();
        Intent i=new Intent(this.getApplicationContext(),load.class);
        i.putExtra("key",2);
        i.putExtra("class",details);
        startActivity(i);
    }

    public void cloud(View v)
    {
        if(details.isCloud_transfer())
        {
            details.setCloud_transfer(false);
            host.setBackgroundResource(R.drawable.cloud);
            host.clearAnimation();
        }
        else {
            if(isOnline())
            {
                details.setCloud_transfer(true);
                host.setBackgroundResource(R.drawable.cloud_on);
                host.startAnimation(anim_blink);
                updateOnline();
            }

        }

    }

    public void checkAndHost()
    {
        if(details.isCloud_transfer())
        {
            updateOnline();
        }

    }


    public void updateOnline()
    {
        if(isOnline())
        {
            Gson gson=new Gson();
            Date curr_time= Calendar.getInstance().getTime();
            SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yy @ HH:mm");
            String time=sdf.format(curr_time);
            details.setLast_updated(time);
            if(details.getFirebase_location()==null)
            {
                //getting key for read and write
                String key_code=generateRandom();
                details.setKey(key_code);
                String key_code_admin=generateRandom();
                details.setAdmin_key(key_code_admin);
                //getting firebase location for read and write
                String key =mDatabaseReference.push().getKey();
                details.setFirebase_location(key);
                String key_admin=mDatabaseReference.push().getKey();
                details.setAdmin_firebaseLocation(key_admin);

                String b=gson.toJson(details);
                mDatabaseReference.child(key).setValue(new firebase_data(key_code,b,false,userId));
                mDatabaseReference.child(key_admin).setValue(new firebase_data(key_code_admin,b,true,userId));
                Toast.makeText(getApplicationContext(),"Hosted Successfully",Toast.LENGTH_SHORT).show();

                //db.insertOnline(details.getAdmin_key(),details.getKey(),details.getName());

            }
            else {
                String b=gson.toJson(details);
                mDatabaseReference.child(details.getFirebase_location()).setValue(new firebase_data(details.getKey(),b,false,userId));
                mDatabaseReference.child(details.getAdmin_firebaseLocation()).setValue(new firebase_data(details.getAdmin_key(),b,true,userId));
                //Toast.makeText(getApplicationContext(),"Updated Successfully",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(getApplicationContext(), "No Internet connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private static String generateRandom() {
        boolean find_another=true;
        StringBuilder res;
        Random rand=new Random();
        int i;
        String aToZ="ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        do{
            res=new StringBuilder();
            for (i = 0; i < 6; i++) {
                int randIndex=rand.nextInt(aToZ.length());
                res.append(aToZ.charAt(randIndex));
            }
            res.insert(3,"-");
            find_another=false;

        }while (find_another);


        return res.toString();
    }

//    private void code_exists(String key)
//    {
//        Query q=mDatabaseReference.orderByChild("key_code").equalTo(key);
//        q.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists())
//                {
//                }
//                else {
//
//                }
//                }
//                @Override
//                public void onCancelled(DatabaseError databaseError) { }
//            });
//    }

    public boolean checkCurrentRoundStatus(){
        List<fixtures> fix=details.getFixtures();
        int count=0;
        for(int i=0;i<details.getFixtures().size();i++)
        {
            if( (fix.get(i).getStatus().equalsIgnoreCase("not-played") || fix.get(i).getStatus().equalsIgnoreCase("playing")) && fix.get(i).getRound()==details.getCurrent_round())
                count++;
        }
        if(count==0)
            return true;
        else
            return false;
    }

    public void create_standings_lv(){
        updateGroupStandings();
        s_adapter=new standings_adapter(main_screen.this,R.layout.standings,details.getTeam_stats(),details.getQualifying_count(),(details.getTeam_count()/details.getGroups())+1);
        main_listView.setAdapter(s_adapter);
    }

    public void create_stats_lv(){
        updateAllStats();
        List<TeamStats> played_teams=new ArrayList<>();
        for(int i=0;i<details.getTeam_stats_all().size();i++)
        {
            if(details.getTeam_stats_all().get(i).getPlayed()>0)
                played_teams.add(details.getTeam_stats_all().get(i));
        }
        stats_adap=new stats_adapter(main_screen.this,R.layout.stats_listview,topics,played_teams);
        main_listView.setAdapter(stats_adap);
    }

    public void create_fixture_lv(){
        //fixtures=details.getFixtures();
        main_listView.setAdapter(adapter);
    }

    public void create_knockoutPhase_LV(){
        main_listView.setAdapter(adapter_knockout);
    }

    class fixtures_lv_holder{
        TextView team1;
        EditText score1;
        TextView team2;
        EditText score2;
        Button status;
        int index;
    }

    public void updateGroupStandings(){
        List<fixtures> fix=details.getFixtures();
        int teams_per_group=details.getTeam_count()/details.getGroups();
        final List<TeamStats> team_stats=details.getTeam_stats();
        fixtures single_fix;TeamStats single_team;
        int wp=details.getWin();int lp=details.getLoss();int dp=details.getDraw();
        int[] stats;String[] teams;
        if(details.getCurrent_round()==1)//no need to update if we r past group stage
        {
            for(int k=0;k<team_stats.size();k++)
            {
                int[] stats_new={0,0,0,0,0,0};
                team_stats.get(k).setStats(stats_new);
            }
            details.setTeam_stats(team_stats);
            for(int i=0;i<fix.size();i++)
            {
                 single_fix=fix.get(i);
                 teams=single_fix.getTeams();
                 int winner;
                 if(single_fix.getRound()==1 && (single_fix.getStatus().equalsIgnoreCase("played") || single_fix.getStatus().equalsIgnoreCase("playing")))
                 {
                     int count=0;
                     winner=single_fix.getOutcome();
                     for(int j=0;j<team_stats.size() && count<=2;j++)
                     {
                         single_team=team_stats.get(j);
                         stats=single_team.getStats();
                         if(single_team.getName().equalsIgnoreCase(teams[0]))
                         {
                             if(winner==0)
                             {
                                 stats[0]++;
                             }
                             else if(winner==1)
                             {
                                 stats[2]++;
                             }
                             else
                             {
                                 stats[1]++;
                             }
                             stats[3]+=single_fix.getScore1();
                             stats[4]+=single_fix.getScore2();
                             stats[5]=(stats[0]*wp)+(stats[1]*dp)+(stats[2]*lp);
                             team_stats.set(j,single_team);
                             count++;
                         }
                         else if(single_team.getName().equalsIgnoreCase(teams[1]))
                         {
                             if(winner==1)
                             {
                                 stats[0]++;
                             }
                             else if(winner==0)
                             {
                                 stats[2]++;
                             }
                             else
                             {
                                 stats[1]++;
                             }
                             stats[3]+=single_fix.getScore2();
                             stats[4]+=single_fix.getScore1();
                             stats[5]=(stats[0]*wp)+(stats[1]*dp)+(stats[2]*lp);
                             team_stats.set(j,single_team);
                             count++;
                         }

                     }
                 }
            }
        }

        if(details.getType().equalsIgnoreCase("league"))
        {
            team_stats.remove(0);
            sortListAlphabetically(team_stats);
            sortList(team_stats);
            team_stats.add(0,new TeamStats("LEAGUE","heading"));
            details.setTeam_stats(team_stats);
        }
        else
        {
            List<TeamStats> total=new ArrayList<>();
            for(int i=0;i<details.getGroups();i++)
            {
                List<TeamStats> each_group=new ArrayList<>();
                for(int j=0;j<(teams_per_group+1);j++)
                {
                    each_group.add(team_stats.get((i*(teams_per_group+1))+j));
                    TeamStats heading=each_group.get(0);
                    each_group.remove(0);
                    sortListAlphabetically(each_group);
                    sortList(each_group);
                    each_group.add(0,heading);
                }
                for(int k=0;k<(teams_per_group+1);k++)
                {
                    total.add((i*(teams_per_group+1))+k,each_group.get(k));
                }
            }
            details.setTeam_stats(total);
        }

    }

    public List<TeamStats> sortListAlphabetically(List<TeamStats> team_stats)
    {
        Collections.sort(team_stats, new Comparator<TeamStats>() {
            @Override
            public int compare(TeamStats t1, TeamStats t2) {
                return t1.getName().compareToIgnoreCase(t2.getName());
            }
        });
        return team_stats;
    }

    public List<TeamStats> sortList(List<TeamStats> team_stats)
    {
        Collections.sort(team_stats, new Comparator<TeamStats>() {
            @Override
            public int compare(TeamStats t1, TeamStats t2) {
                if(t1.getPoints()==t2.getPoints())
                {
                    if(t1.getGD()==t2.getGD())
                    {
                        if(t1.getGF()==t2.getGF())
                        {
                            if(t1.getGA()==t2.getGA())
                            {
                                if(t1.getPlayed()==t2.getPlayed())
                                    return headToHead(t1,t2);
                                else
                                    return t1.getPlayed()-t2.getPlayed();
                            }
                            else
                                return t1.getGA()-t2.getGA();
                        }
                        else
                            return t2.getGF()-t1.getGF();
                    }
                    else
                        return t2.getGD()-t1.getGD();
                }
                else
                    return t2.getPoints()-t1.getPoints();


            }
        });
        return team_stats;
    }

    public int headToHead(TeamStats t1,TeamStats t2)
    {
        int point1=0,point2=0,goal1=0,goal2=0,home1=0,home2=0,away1=0,away2=0,count=0;
        for(int i=0;i<details.getFixtures().size();i++)
        {
            if(details.getFixtures().get(i).getStatus().equalsIgnoreCase("played"))
            {
                if(details.getFixtures().get(i).getTeam1().equalsIgnoreCase(t1.getName()) && details.getFixtures().get(i).getTeam2().equalsIgnoreCase(t2.getName()))
                {
                    if(details.getFixtures().get(i).getOutcome()==0)
                        point1+=details.getWin();
                    else if(details.getFixtures().get(i).getOutcome()==1)
                        point2+=details.getWin();
                    else {
                        point1+=details.getDraw();
                        point2+=details.getDraw();
                    }
                    home1+=details.getFixtures().get(i).getScore1();
                    away2+=details.getFixtures().get(i).getScore2();
                    count++;
                }
                else if(details.getFixtures().get(i).getTeam1().equalsIgnoreCase(t2.getName()) && details.getFixtures().get(i).getTeam2().equalsIgnoreCase(t1.getName())) {
                    if(details.getFixtures().get(i).getOutcome()==0)
                        point2+=details.getWin();
                    else if(details.getFixtures().get(i).getOutcome()==1)
                        point1+=details.getWin();
                    else {
                        point1+=details.getDraw();
                        point2+=details.getDraw();
                    }
                    home2+=details.getFixtures().get(i).getScore1();
                    away1+=details.getFixtures().get(i).getScore2();
                    count++;
                }
            }
        }

        if(count>0)
        {
            goal1=home1+away1;
            goal2=home2+away2;
            if(point1==point2)
            {
                if(goal1==goal2)
                {
                    if(away1==away2)
                    {
                        if(home1==home2)
                            return 0;
                        else return home2-home1;
                    }
                    return away2-away1;
                }
                return goal2-goal1;
            }
            else return point2-point1;
        }
        else return 0;

    }

    public void updateProgress(){
        float progress_percent;
        if(details.getCurrent_matches()!=0){
            progress_percent=((float) details.getCurrent_matches()/details.getTotal_matches())*100;
        }
        else progress_percent=0;
            progress.setText("PROGRESS : "+(int)progress_percent+"%");
    }

    class fixture_adpater extends BaseAdapter{

        @Override
        public int getCount() {
            return details.getFixtures().size() ;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final fixtures_lv_holder holder;
            if(view==null) {
                holder=new fixtures_lv_holder();
                view = getLayoutInflater().inflate(R.layout.fixtures, null);
                holder.status = (Button) view.findViewById(status);
                holder.team1=(TextView)view.findViewById(R.id.team1);
                holder.team2=(TextView)view.findViewById(R.id.team2);
                holder.score1=(EditText)view.findViewById(score1);
                holder.score2=(EditText)view.findViewById(score2);
                view.setTag(holder);
            }
            else
                holder=(fixtures_lv_holder) view.getTag();

            final fixtures single_fixture=details.getFixtures().get(i);
            String[] teams=single_fixture.getTeams();
            final int[] score=single_fixture.getScore();
            final int round=single_fixture.getRound();
            final String status=single_fixture.getStatus();

            holder.index=i;
            holder.team1.setText(""+teams[0].toUpperCase());
            holder.team2.setText(""+teams[1].toUpperCase());
            holder.score1.setText(""+score[0]);
            holder.score2.setText(""+score[1]);

            if(status.equalsIgnoreCase("heading"))
            {
                holder.team1.setBackgroundColor(Color.parseColor("#000000"));
                holder.team1.setTextColor(Color.WHITE);

                holder.score1.setAlpha(0f);
                holder.score1.setEnabled(false);
                holder.score2.setAlpha(0f);
                holder.score2.setEnabled(false);
                holder.team2.setAlpha(0f);
                holder.status.setAlpha(0f);
                holder.status.setEnabled(false);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                    holder.team1.getLayoutParams();
                params.weight = 100f;
                holder.team1.setLayoutParams(params);
            }
            else if(status.equalsIgnoreCase("not-played"))
            {
                holder.team1.setBackgroundColor(Color.WHITE);
                holder.team1.setTextColor(Color.BLACK);

                holder.status.setBackgroundResource(R.drawable.wrong_white);
                holder.score1.setAlpha(1f);
                holder.score1.setEnabled(true);
                holder.score2.setAlpha(1f);
                holder.score2.setEnabled(true);
                holder.team2.setAlpha(1f);
                holder.status.setEnabled(true);
                holder.status.setAlpha(1f);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                        holder.team1.getLayoutParams();
                params.weight = 5f;
                holder.team1.setLayoutParams(params);

            }
            else if(status.equalsIgnoreCase("played"))
            {
                holder.team1.setBackgroundColor(Color.WHITE);
                holder.team1.setTextColor(Color.BLACK);
                holder.status.setBackgroundResource(R.drawable.tick_white);
                holder.score1.setAlpha(1f);
                holder.score1.setEnabled(false);
                holder.score2.setAlpha(1f);
                holder.score2.setEnabled(false);
                holder.team2.setAlpha(1f);
                holder.status.setEnabled(true);
                holder.status.setAlpha(1f);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                        holder.team1.getLayoutParams();
                params.weight = 5f;
                holder.team1.setLayoutParams(params);

            }
            else if(status.equalsIgnoreCase("playing"))
            {
                holder.team1.setBackgroundColor(Color.WHITE);
                holder.team1.setTextColor(Color.BLACK);

                holder.status.setBackgroundResource(R.drawable.whistle);
                holder.score1.setAlpha(1f);
                holder.score1.setEnabled(true);
                holder.score2.setAlpha(1f);
                holder.score2.setEnabled(true);
                holder.team2.setAlpha(1f);
                holder.status.setEnabled(true);
                holder.status.setAlpha(1f);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                        holder.team1.getLayoutParams();
                params.weight = 5f;
                holder.team1.setLayoutParams(params);
            }

            if(details.getOwner() && !details.getFixtures().get(i).getStatus().equalsIgnoreCase("played"))
            {
                holder.score1.setEnabled(true);
                holder.score2.setEnabled(true);
            }
            else {
                holder.score1.setEnabled(false);
                holder.score2.setEnabled(false);
            }

            holder.score1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(details.getFixtures().get(holder.index).getStatus().equalsIgnoreCase("playing"))
                    {
                        if(holder.score1.getText().toString().trim().length()!=0 && holder.score2.getText().toString().trim().length()!=0)
                        {
                            score[0]=Integer.parseInt(holder.score1.getText().toString());
                            score[1]=Integer.parseInt(holder.score2.getText().toString());
                            single_fixture.setScore(score);
                            details.getFixtures().set(holder.index,single_fixture);
                            checkAndHost();
                        }
                    }
                }
                @Override
                public void afterTextChanged(Editable editable) {}
            });

            holder.score2.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(details.getFixtures().get(holder.index).getStatus().equalsIgnoreCase("playing"))
                    {
                        if(holder.score1.getText().toString().trim().length()!=0 && holder.score2.getText().toString().trim().length()!=0)
                        {
                            score[0]=Integer.parseInt(holder.score1.getText().toString());
                            score[1]=Integer.parseInt(holder.score2.getText().toString());
                            single_fixture.setScore(score);
                            details.getFixtures().set(holder.index,single_fixture);
                            checkAndHost();
                        }
                    }
                }
                @Override
                public void afterTextChanged(Editable editable) {}
            });


            holder.status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(details.getOwner())
                    {
                        if(round==details.getCurrent_round() && !to_next_round_button.getText().toString().equalsIgnoreCase("start round"))
                        {
                            if(holder.score1.getText().toString().trim().length()!=0)
                            {
                                score[0]=Integer.parseInt(holder.score1.getText().toString());
                            }
                            else
                            {
                                score[0]=0;
                            }
                            if(holder.score2.getText().toString().trim().length()!=0)
                            {
                                score[1]=Integer.parseInt(holder.score2.getText().toString());
                            }
                            else
                            {
                                score[1]=0;
                            }
                            if(status.equalsIgnoreCase("not-played"))
                            {
                                single_fixture.setStatus("playing");
                            }
                            else if(status.equalsIgnoreCase("playing"))
                            {
                                single_fixture.setStatus("played");
                                details.setCurrent_matches(details.getCurrent_matches()+1);
                            }
                            else
                            {
                                single_fixture.setStatus("not-played");
                                details.setCurrent_matches(details.getCurrent_matches()-1);
                            }
                            updateProgress();
                            single_fixture.setScore(score);
                            details.getFixtures().set(holder.index,single_fixture);
                            adapter.notifyDataSetChanged();
                            if(checkCurrentRoundStatus())
                            {
                                to_next_round_button.setEnabled(true);
                                to_next_round_button.setAlpha(1f);
                            }
                            else
                            {
                                to_next_round_button.setEnabled(false);
                                to_next_round_button.setAlpha(0f);
                            }
                            checkAndHost();

                        }
                    }

                }
            });



            return view;
        }
    }

    private class KnockoutPhaseHolder{
        TextView result;
        int index;
        Button team1,team2;
    }

    private class KnockoutPhaseAdapter extends BaseAdapter{


        @Override
        public int getCount() {
            return details.getKnockoutStatsList().size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final KnockoutPhaseHolder holder;
            if(view==null) {
                holder=new KnockoutPhaseHolder();
                view = getLayoutInflater().inflate(R.layout.knockout_seeding, null);
                holder.team1=(Button) view.findViewById(R.id.team1);
                holder.team2=(Button)view.findViewById(R.id.team2);
                holder.result=(TextView) view.findViewById(R.id.result);
                view.setTag(holder);
            }
            else
                holder=(KnockoutPhaseHolder) view.getTag();
            final KnockoutStats single_knockout=details.getKnockoutStatsList().get(i);
            final String status=single_knockout.getStatus();
            final int round=single_knockout.getRound();
            holder.index=i;
            holder.team1.setText(""+single_knockout.getTeam1());
            holder.team2.setText(""+single_knockout.getTeam2());
            holder.result.setText(""+single_knockout.getResult());
            if(status.equalsIgnoreCase("heading"))
            {
                holder.result.setAlpha(0f);
                holder.team2.setAlpha(0f);
                holder.team2.setEnabled(false);
                holder.team1.setBackgroundColor(Color.parseColor("#000000"));
                holder.team1.setTextColor(Color.parseColor("#ffffff"));
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                        holder.team1.getLayoutParams();
                params.weight = 100;
                holder.team1.setLayoutParams(params);
            }
            else  {
                holder.result.setAlpha(1f);
                holder.team2.setAlpha(1f);
                holder.team2.setEnabled(true);
                holder.team1.setBackgroundColor(Color.parseColor("#EEEEEE"));
                holder.team1.setTextColor(Color.parseColor("#000000"));
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                        holder.team1.getLayoutParams();
                params.weight = 5f;
                holder.team1.setLayoutParams(params);
            }

            if(!status.equalsIgnoreCase("heading"))
            {
                if(details.getSelected()==1 && details.getSelected_index0()==holder.index && details.getSelected_index1()==1)
                {
                    holder.team1.setBackgroundColor(Color.GREEN);
                    holder.team2.setBackgroundColor(Color.parseColor("#CCFF90"));
                }
                else if(details.getSelected()==1 && details.getSelected_index0()==holder.index && details.getSelected_index1()==2)
                {
                    holder.team2.setBackgroundColor(Color.GREEN);
                    holder.team1.setBackgroundColor(Color.parseColor("#CCFF90"));
                }
                else if(round<=details.getCurrent_round()) {
                    holder.team1.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    holder.team2.setBackgroundColor(Color.parseColor("#EEEEEE"));
                }
                else {
                    holder.team1.setBackgroundColor(Color.parseColor("#CCFF90"));
                    holder.team2.setBackgroundColor(Color.parseColor("#CCFF90"));
                }
            }

            if(details.getKnockoutStatsList().get(i).getStatus().equalsIgnoreCase(details.getKnockoutStatsList().get(i).getTeam1()))
            {
                holder.team1.setText(""+single_knockout.getTeam1()+"*");
                holder.team2.setText(""+single_knockout.getTeam2());
            }
            else if(details.getKnockoutStatsList().get(i).getStatus().equalsIgnoreCase(details.getKnockoutStatsList().get(i).getTeam2()))
            {
                holder.team1.setText(""+single_knockout.getTeam1());
                holder.team2.setText(""+single_knockout.getTeam2()+"*");
            }
            else
            {
                holder.team1.setText(""+single_knockout.getTeam1());
                holder.team2.setText(""+single_knockout.getTeam2());
            }



            holder.team1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(details.getOwner())
                    {
                        if(!status.equalsIgnoreCase("heading") && round>details.getCurrent_round())
                        {
                            if(details.getSelected()==0)
                            {
                                details.setSelected(1);
                                details.setSelected_index0(holder.index);
                                details.setSelected_index1(1);//denotes team1 in ith pos
                                notifyDataSetChanged();
                            }
                            else if(details.getSelected()==1 && details.getSelected_index0()==holder.index && details.getSelected_index1()==1 )
                            {
                                details.setSelected(0);
                                notifyDataSetChanged();
                            }
                            else if(details.getSelected()==1 && !(details.getSelected_index0()==holder.index && details.getSelected_index1()==1) ) {
                                String team;
                                if(details.getSelected_index1()==1){
                                    team=details.getKnockoutStatsList().get(details.getSelected_index0()).getTeam1();
                                    details.getKnockoutStatsList().get(details.getSelected_index0()).setTeam1(single_knockout.getTeam1());}
                                else {
                                    team=details.getKnockoutStatsList().get(details.getSelected_index0()).getTeam2();
                                    details.getKnockoutStatsList().get(details.getSelected_index0()).setTeam2(single_knockout.getTeam1());}
                                details.getKnockoutStatsList().get(holder.index).setTeam1(team);
                                details.setSelected(0);
                                updateKnockout(false);
                                notifyDataSetChanged();
                            }
                            checkAndHost();
                        }

                    }



                }
            });

            holder.team2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(details.getOwner())
                    {
                        if(!status.equalsIgnoreCase("heading") && round>details.getCurrent_round())
                        {
                            if(details.getSelected()==0)
                            {
                                details.setSelected(1);
                                details.setSelected_index0(holder.index);
                                details.setSelected_index1(2);
                                //denotes team2 in ith pos
                                notifyDataSetChanged();
                            }
                            else if(details.getSelected()==1 && details.getSelected_index0()==holder.index && details.getSelected_index1()==2 )
                            {
                                details.setSelected(0);
                                notifyDataSetChanged();
                            }
                            else if(details.getSelected()==1 && !(details.getSelected_index0()==holder.index && details.getSelected_index1()==2) ){
                                String team;
                                if(details.getSelected_index1()==1){
                                    team=details.getKnockoutStatsList().get(details.getSelected_index0()).getTeam1();
                                    details.getKnockoutStatsList().get(details.getSelected_index0()).setTeam1(single_knockout.getTeam2());}
                                else {
                                    team=details.getKnockoutStatsList().get(details.getSelected_index0()).getTeam2();
                                    details.getKnockoutStatsList().get(details.getSelected_index0()).setTeam2(single_knockout.getTeam2());}
                                details.getKnockoutStatsList().get(holder.index).setTeam2(team);
                                details.setSelected(0);
                                updateKnockout(false);
                                notifyDataSetChanged();
                            }
                            checkAndHost();
                        }
                    }

                }
            });
            return view;
        }
    }



    public void updateRound(View v){

        Button b=(Button)v;
        String action=b.getText().toString();
        if(details.getOwner())
        {
            if(action.toLowerCase().equalsIgnoreCase("to next round"))
            {
                if((details.getCurrent_round()+1)>details.getRounds())//completed condition
                {
                    if(details.getType().equalsIgnoreCase("league"))
                    {
                        updateGroupStandings();
                        details.setCurrent_round(details.getCurrent_round()+1);
                        progress.setText("WINNER : "+details.getTeam_stats().get(1).getName().toUpperCase());
                        Animation anim= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.blink);
                        progress.startAnimation(anim);
                        details.setFinished(true);
                        details.setWinner(details.getTeam_stats().get(1).getName().toUpperCase());
                        to_next_round_button.setEnabled(false);
                        to_next_round_button.setAlpha(0f);
                        toStandingsTab();
                        checkAndHost();
                    }
                    else
                    {
                        calculateKnockoutresult();
                        if(!details.getError())
                        {
                            if(details.isThird_place())
                                progress.setText("WINNER : "+details.getQualified_teams().get(1).toUpperCase());
                            else
                                progress.setText("WINNER : "+details.getQualified_teams().get(0).toUpperCase());
                            Animation anim= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.blink);
                            progress.startAnimation(anim);
                            details.setFinished(true);
                            details.setWinner(details.getQualified_teams().get(0).toUpperCase());
                            details.setCurrent_round(details.getCurrent_round()+1);
                            to_next_round_button.setEnabled(false);
                            to_next_round_button.setAlpha(0f);
                            toKnockoutTab();
                            checkAndHost();
                        }
                        else {
                            details.setError(false);
                            Toast.makeText(getApplicationContext(),"Insufficient data to proceed for next round !",Toast.LENGTH_SHORT).show();
                        }
                    }

                }
                else
                {
                    if(details.getType().equalsIgnoreCase("knockout"))
                    {
                        if((details.getCurrent_round()+1)==details.getRounds())//final ROund
                        {
                            calculateKnockoutresult();
                            if(!details.getError())
                            {
                                GenFinalRound();
                                for(int i=0;i<details.getFix_to_add().size();i++)
                                    details.getFixtures().add(details.getFix_to_add().get(i));
                                details.setCurrent_round(details.getCurrent_round()+1);
                                details.setSelected(0);
                                to_next_round_button.setAlpha(0f);
                                to_next_round_button.setEnabled(false);
                                toKnockoutTab();
                                checkAndHost();
                            }
                            else {
                                details.setError(false);
                                Toast.makeText(getApplicationContext(),"Insufficient data to proceed for next round !",Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            calculateKnockoutresult();
                            Collections.shuffle(details.getQualified_teams());
                            if(!details.getError())
                            {
                                genFixtureKnockout(details.getQualified_teams(),false,details.getCurrent_round()+1);
                                to_next_round_button.setText("START ROUND");
                                details.setButton_text_is_default(false);
                                toKnockoutTab();
                                checkAndHost();
                            }
                            else {
                                details.setError(false);
                                Toast.makeText(getApplicationContext(),"Insufficient data to proceed for next round !",Toast.LENGTH_SHORT).show();
                            }
                        }


                    }
                    else {
                        if((details.getCurrent_round()+1)==2) //start of knockout from group stages
                        {
                            updateGroupStandings();
                            GroupToKnockout();
                            to_next_round_button.setText("START ROUND");
                            details.setButton_text_is_default(false);
                            toKnockoutTab();
                            checkAndHost();
                        }
                        else if((details.getCurrent_round()+1)==details.getRounds())//final ROund
                        {
                            calculateKnockoutresult();
                            if(!details.getError())
                            {
                                GenFinalRound();
                                for(int i=0;i<details.getFix_to_add().size();i++)
                                    details.getFixtures().add(details.getFix_to_add().get(i));
                                details.setCurrent_round(details.getCurrent_round()+1);
                                details.setSelected(0);
                                to_next_round_button.setAlpha(0f);
                                to_next_round_button.setEnabled(false);
                                toKnockoutTab();
                                checkAndHost();
                            }
                            else {
                                details.setError(false);
                                Toast.makeText(getApplicationContext(),"Insufficient data to proceed for next round !",Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            calculateKnockoutresult();
                            Collections.shuffle(details.getQualified_teams());
                            if(!details.getError())
                            {
                                genFixtureKnockout(details.getQualified_teams(),false,details.getCurrent_round()+1);
                                to_next_round_button.setText("START ROUND");
                                details.setButton_text_is_default(false);
                                toKnockoutTab();
                                checkAndHost();
                            }
                            else {
                                details.setError(false);
                                Toast.makeText(getApplicationContext(),"Insufficient data to proceed for next round !",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
            else {
                for(int i=0;i<details.getFix_to_add().size();i++)
                    details.getFixtures().add(details.getFix_to_add().get(i));
                details.setCurrent_round(details.getCurrent_round()+1);
                details.setSelected(0);
                details.setError(false);
                to_next_round_button.setText("TO NEXT ROUND");
                toFixturesTab();
                details.setButton_text_is_default(true);
                to_next_round_button.setAlpha(0f);
                to_next_round_button.setEnabled(false);
                checkAndHost();

            }
            adapter_knockout.notifyDataSetChanged();
            adapter.notifyDataSetChanged();
        }



    }



    public void calculateKnockoutresult() //gets the qualified teams from the knockout stage
    {
        details.getQualified_teams().clear();
        details.getKnocked_out_teams().clear();
        int knockout_size=details.getKnockoutStatsList().size();
        int fix_size=details.getFixtures().size();
        for(int i=0;i<knockout_size;i++)
        {
            if(details.getKnockoutStatsList().get(i).getRound()==details.getCurrent_round() && !details.getKnockoutStatsList().get(i).getStatus().equalsIgnoreCase("heading"))
            {
                int score1h=0,score2h=0,score1a=0,score2a=0,score1=0,score2=0;

                for(int j=0;j<fix_size;j++)
                {

                    if(details.getFixtures().get(j).getRound()==details.getCurrent_round() && details.getFixtures().get(j).getTeam1().toLowerCase().equalsIgnoreCase(details.getKnockoutStatsList().get(i).getTeam1().toLowerCase()))
                    {
                        score1h+=details.getFixtures().get(j).getScore1();
                        score2h+=details.getFixtures().get(j).getScore2();
                    }

                    if(details.getFixtures().get(j).getRound()==details.getCurrent_round() && details.getFixtures().get(j).getTeam2().toLowerCase().equalsIgnoreCase(details.getKnockoutStatsList().get(i).getTeam1().toLowerCase()))
                    {
                        score2a+=details.getFixtures().get(j).getScore1();
                        score1a+=details.getFixtures().get(j).getScore2();
                    }
                    score1=score1h+score1a;
                    score2=score2h+score2a;

                }
                details.getKnockoutStatsList().get(i).setResult(""+score1+"-"+score2);
                if(score1>score2)
                {
                    details.getKnockoutStatsList().get(i).setStatus(details.getKnockoutStatsList().get(i).getTeam1());
                    details.getQualified_teams().add(details.getKnockoutStatsList().get(i).getTeam1());
                    details.getKnocked_out_teams().add(details.getKnockoutStatsList().get(i).getTeam2());
                }
                else if(score2>score1)
                {
                    details.getKnockoutStatsList().get(i).setStatus(details.getKnockoutStatsList().get(i).getTeam2());
                    details.getQualified_teams().add(details.getKnockoutStatsList().get(i).getTeam2());
                    details.getKnocked_out_teams().add(details.getKnockoutStatsList().get(i).getTeam1());
                }
                else if(details.isK_double()){
                    if(score1a>score2a){
                        details.getKnockoutStatsList().get(i).setStatus(details.getKnockoutStatsList().get(i).getTeam1());
                        details.getQualified_teams().add(details.getKnockoutStatsList().get(i).getTeam1());
                        details.getKnocked_out_teams().add(details.getKnockoutStatsList().get(i).getTeam2());}
                    else if(score1a<score2a){
                        details.getKnockoutStatsList().get(i).setStatus(details.getKnockoutStatsList().get(i).getTeam2());
                        details.getQualified_teams().add(details.getKnockoutStatsList().get(i).getTeam2());
                        details.getKnocked_out_teams().add(details.getKnockoutStatsList().get(i).getTeam1());}
                    else
                        details.setError(true);
                }
                else
                {
                    details.setError(true);
                }
            }
        }

    }

    public void genFixtureKnockout(List<String> teams,boolean finals,int round_no)
    {
        seeding s=new seeding();
        details.getFix_to_add().clear();
        List<KnockoutStats> kn=details.getKnockoutStatsList();
        String round=s.getKnockoutRound(teams.size());
        String leg;
        if(details.isK_double())
            leg=" - First leg";
        else
            leg="";
        fixtures f=new fixtures(round_no,""+round+""+leg,"",0,0,"heading");
        KnockoutStats k1=new KnockoutStats(round_no,""+round,"","heading");
        details.getFix_to_add().add(f);
        kn.add(k1);
        for(int i=0;i<teams.size()/2;i++)
        {
            fixtures f1=new fixtures(round_no,teams.get(i*2),teams.get((i*2)+1),0,0,"not-played");
            KnockoutStats k=new KnockoutStats(round_no,teams.get(i*2),teams.get((i*2)+1),"not-completed");
            details.getFix_to_add().add(f1);
            kn.add(k);
        }
        if(details.isK_double())
        {
            fixtures fi=new fixtures(round_no,""+round+" - Second Leg","",0,0,"heading");
            details.getFix_to_add().add(fi);
            for(int i=0;i<teams.size()/2;i++)
            {
                fixtures f2=new fixtures(round_no,teams.get((i*2)+1),teams.get(i*2),0,0,"not-played");
                details.getFix_to_add().add(f2);
            }
        }
        details.setKnockoutStatsList(kn);
    }

    public void GenFinalRound(){
        int round_no=details.getCurrent_round()+1;
        String leg;
        details.getFix_to_add().clear();
        if(details.isThird_place())
        {

            if(details.isK_double())
                leg=" - First leg";
            else
                leg="";
            //adding heading
            details.getKnockoutStatsList().add(new KnockoutStats(round_no,"Third Place","","heading"));
            details.getFix_to_add().add(new fixtures(round_no,"Third Place"+""+leg,"",0,0,"heading"));
            //adding match
            details.getKnockoutStatsList().add(new KnockoutStats(round_no,""+details.getKnocked_out_teams().get(0),""+details.getKnocked_out_teams().get(1),"not-completed"));
            details.getFix_to_add().add(new fixtures(round_no,""+details.getKnocked_out_teams().get(0),""+details.getKnocked_out_teams().get(1),0,0,"not-played"));
            if(details.isK_double()) {
                details.getFix_to_add().add(new fixtures(round_no,"Third Place"+" - Second Leg","",0,0,"heading"));
                details.getFix_to_add().add(new fixtures(round_no, "" + details.getKnocked_out_teams().get(1), "" + details.getKnocked_out_teams().get(0), 0, 0, "not-played"));
            }
        }
        //for finals
        if(details.isF_double())
            leg=" - First leg";
        else
            leg="";
        details.getKnockoutStatsList().add(new KnockoutStats(round_no,"Finals","","heading"));
        details.getFix_to_add().add(new fixtures(round_no,"Finals"+""+leg,"",0,0,"heading"));

        details.getKnockoutStatsList().add(new KnockoutStats(round_no,""+details.getQualified_teams().get(0),""+details.getQualified_teams().get(1),"not-completed"));
        details.getFix_to_add().add(new fixtures(round_no,""+details.getQualified_teams().get(0),""+details.getQualified_teams().get(1),0,0,"not-played"));
        if(details.isF_double()) {
            details.getFix_to_add().add(new fixtures(round_no,"Finals"+" - Second Leg","",0,0,"heading"));
            details.getFix_to_add().add(new fixtures(round_no, "" + details.getQualified_teams().get(1), "" + details.getQualified_teams().get(0), 0, 0, "not-played"));
        }
    }

    public void updateKnockout(boolean finals){
        details.getQualified_teams().clear();
        int round_no=details.getCurrent_round()+1;
        seeding s=new seeding();
        details.getFix_to_add().clear();
        for(int i=0;i<details.getKnockoutStatsList().size();i++)
        {
            if(details.getKnockoutStatsList().get(i).getRound()==(details.getCurrent_round()+1) && !details.getKnockoutStatsList().get(i).getStatus().equalsIgnoreCase("heading"))
            {
                details.getQualified_teams().add(details.getKnockoutStatsList().get(i).getTeam1());
                details.getQualified_teams().add(details.getKnockoutStatsList().get(i).getTeam2());
            }
        }
        String round=s.getKnockoutRound(details.getQualified_teams().size());
        String leg;
        if(details.isK_double())
            leg="- First leg";
        else
            leg="";
        fixtures f=new fixtures(round_no,""+round+""+leg,"",0,0,"heading");
        details.getFix_to_add().add(f);
        for(int i=0;i<details.getQualified_teams().size()/2;i++)
        {
            fixtures f1=new fixtures(round_no,details.getQualified_teams().get(i*2),details.getQualified_teams().get((i*2)+1),0,0,"not-played");
            details.getFix_to_add().add(f1);
        }
        if(details.isK_double())
        {
            fixtures fi=new fixtures(round_no,""+round+" - Second Leg","",0,0,"heading");
            details.getFix_to_add().add(fi);
            for(int i=0;i<details.getQualified_teams().size()/2;i++)
            {
                fixtures f2=new fixtures(round_no,details.getQualified_teams().get((i*2)+1),details.getQualified_teams().get(i*2),0,0,"not-played");
                details.getFix_to_add().add(f2);
            }
        }
    }

    public void GroupToKnockout(){
        int i,e=details.getQualifying_count(),teams_per_group=details.getTeam_count()/details.getGroups(),loop_c=details.getQualifying_count()/2;
        if(loop_c==0)
            loop_c=1;
        int teams_in_knockout=details.getQualifying_count()*details.getGroups(),round_no=details.getCurrent_round()+1;
        seeding s=new seeding();
        String round=s.getKnockoutRound(teams_in_knockout);
        List<TeamStats> teams_order=details.getTeam_stats();
        details.getKnockoutStatsList().add(new KnockoutStats(round_no,""+round,"","heading"));
        for(i=1;i<=loop_c;i++)
        {
            details.getPot1().clear();
            details.getPot2().clear();
            for(int j=0;j<teams_order.size();j++)
            {
                if(j%(teams_per_group+1)==i)
                    details.getPot1().add(teams_order.get(j).getName());
                if(j%(teams_per_group+1)==e)
                    details.getPot2().add(teams_order.get(j).getName());
            }
            if(details.getQualifying_count()!=1)
            shuffleList();
            else
            {
                int siz=details.getPot1().size();
                Collections.reverse(details.getPot2());
                for(int k=0;k<siz/2;k++)
                {
                    details.getPot1().remove(0);
                    details.getPot2().remove(0);
                }
                Collections.shuffle(details.getPot1());
                Collections.shuffle(details.getPot2());
            }
            for(int k=0;k<details.getPot1().size();k++)
            {
                details.getKnockoutStatsList().add(new KnockoutStats(round_no,details.getPot1().get(k),details.getPot2().get(k),"not-completed"));
            }
            e--;
        }
        updateKnockout(false);
    }

     public void shuffleList() {
        List<String> temp = new ArrayList<String>(details.getPot2());
        Random rand = new Random();

        for (int i = 0; i < details.getPot2().size(); i++) {
            int newPos = rand.nextInt(details.getPot2().size());
            while (newPos == i||temp.get(newPos)==null) {
                newPos = rand.nextInt(details.getPot2().size());
            }
            details.getPot2().set(i, temp.get(newPos));
            temp.set(newPos,null);
        }
    }

    public void updateAllStats()
    {
        int i,j,fix_size=details.getFixtures().size(),teams_size=details.getTeam_stats_all().size();
        int wp=details.getWin(),lp=details.getLoss(),dp=details.getDraw();
        for(i=0;i<details.getTeam_stats_all().size();i++)
        {
            int[] stats_new={0,0,0,0,0,0};
            details.getTeam_stats_all().get(i).setStats(stats_new);
            details.getTeam_stats_all().get(i).setCs(0);

        }
        for(i=0;i<fix_size;i++)
        {
            if(details.getFixtures().get(i).getStatus().equalsIgnoreCase("played"))
            {
                int index1=getPos(details.getFixtures().get(i).getTeam1()),index2=getPos(details.getFixtures().get(i).getTeam2());
                //Toast.makeText(getApplicationContext(),""+index1+"-"+index2,Toast.LENGTH_SHORT).show();
                int [] stats1=details.getTeam_stats_all().get(index1).getStats();
                int [] stats2=details.getTeam_stats_all().get(index2).getStats();


                if(details.getFixtures().get(i).getOutcome()==0)
                {
                    stats1[0]++;
                    stats2[2]++;
                }
                else if(details.getFixtures().get(i).getOutcome()==1)
                {
                    stats2[0]++;
                    stats1[2]++;
                }
                else {
                    stats1[1]++;stats2[1]++;
                }

                stats1[3]+=details.getFixtures().get(i).getScore1();stats2[3]+=details.getFixtures().get(i).getScore2();
                stats1[4]+=details.getFixtures().get(i).getScore2();stats2[4]+=details.getFixtures().get(i).getScore1();
                stats1[5]=(stats1[0]*wp)+(stats1[1]*dp)+(stats1[2]*lp);stats2[5]=(stats2[0]*wp)+(stats2[1]*dp)+(stats2[2]*lp);

                if(details.getFixtures().get(i).getScore1()==0)
                {
                    details.getTeam_stats_all().get(index2).setCs(details.getTeam_stats_all().get(index2).getCs()+1);
                }
                if(details.getFixtures().get(i).getScore2()==0)
                {
                    details.getTeam_stats_all().get(index1).setCs(details.getTeam_stats_all().get(index1).getCs()+1);
                }

                details.getTeam_stats_all().get(index1).setStats(stats1);
                details.getTeam_stats_all().get(index2).setStats(stats2);
            }

        }
    }

    public int getPos(String name)
    {
        int i,size=details.getTeam_stats_all().size();
        for(i=0;i<size;i++)
        {
            if(details.getTeam_stats_all().get(i).getName().equalsIgnoreCase(name))
                return i;
        }
        return 99;
    }






}
