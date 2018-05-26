package com.project.harrison.tournamentmod;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


/**
 * Created by Harrison on 04-03-2018.
 */

public class seeding extends Activity {

    Tournament details;int team_per_group,groups,list_view_size;
    TextView tour_name;
    //for swapping
    int selected=0,selected_index,teams_count;

    ListView seeding_lv;
    List<String> team_names;
    List<String> team_name_modified,team_name_customized;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seeding);

        Intent i=getIntent();
        details=(Tournament)i.getSerializableExtra("class");
        generateRounds();
        seeding_lv=(ListView)findViewById(R.id.seeding_list_view);
        tour_name=(TextView)findViewById(R.id.tournament_name);
        tour_name.setText(""+details.getName());
        groups=details.getGroups();
        team_names=details.getTeams();
        teams_count=details.getTeam_count();
        Collections.shuffle(team_names);
        team_per_group=teams_count/groups;
        if(details.getType().toLowerCase().contains("group") && groups>1)
        {
            team_name_modified=new ArrayList<>(team_names);
            team_name_customized=new ArrayList<>();
            createModifiedList();//add group i to list and arrange it in order to place it in list
            list_view_size=calcRows(team_name_customized.size(),2);
            create_list_group();
        }
        else if(details.getType().equalsIgnoreCase("knockout"))
        {
             createKnockout_list();
        }
        else
        {
            manageGroupFixtures();
            generateTeamStats();
            Intent j;
            j=new Intent(this,main_screen.class);
            j.putExtra("class",details);
            startActivity(j);
            finish();
        }
    }

    public void generateRounds(){
        details.setCurrent_round(1);
        int rounds=(int)(Math.log(details.getTeam_count())/Math.log(2));
        if(details.getType().equalsIgnoreCase("knockout"))
        {
            details.setRounds(rounds);
        }
        else if(details.getType().toLowerCase().contains("group"))
        {
            rounds=(int)(Math.log(details.getGroups()*details.getQualifying_count())/Math.log(2));
            details.setRounds(rounds+1);
        }
        else
            details.setRounds(1);
    }

    public void genFixtureKnockout(List<String> teams,int round_no)
    {
        List<fixtures> fix=details.getFixtures();
        List<KnockoutStats> kn=details.getKnockoutStatsList();
        String round=getKnockoutRound(teams_count);
        String leg;
        if(!round.equalsIgnoreCase("finals"))
        {
            if(details.isK_double())
                leg="- First leg";
            else
                leg="";
        }
        else {
            if(details.isF_double())
                leg="- First leg";
            else
                leg="";
        }

        fixtures f=new fixtures(round_no,""+round+""+leg,"",0,0,"heading");
        KnockoutStats k1=new KnockoutStats(round_no,""+round,"","heading");
        fix.add(f);
        kn.add(k1);
        for(int i=0;i<teams.size()/2;i++)
        {
            fixtures f1=new fixtures(round_no,teams.get(i*2),teams.get((i*2)+1),0,0,"not-played");
            KnockoutStats k=new KnockoutStats(round_no,teams.get(i*2),teams.get((i*2)+1),"not-completed");
            fix.add(f1);
            kn.add(k);
        }
        if((details.isK_double() && !round.equalsIgnoreCase("finals"))||(details.isF_double() && round.equalsIgnoreCase("finals")))
        {
            fixtures fi=new fixtures(round_no,""+round+" - Second Leg","",0,0,"heading");
            fix.add(fi);
            for(int i=0;i<teams.size()/2;i++)
            {
                fixtures f2=new fixtures(round_no,teams.get((i*2)+1),teams.get(i*2),0,0,"not-played");
                fix.add(f2);
            }
        }
        details.setFixtures(fix);
        details.setKnockoutStatsList(kn);
    }

    public String getKnockoutRound(int teams)
    {
        String r;
        if(teams==2)
        {
            r="Finals";
        }
        else if(teams==4)
        {
            r="Semi-Finals";
        }
        else if(teams==8)
        {
            r="Quarter-Finals";
        }
        else
        {
            r="Round-of-"+teams;
        }
        return r;
    }

    public void create_list_group(){
        GroupSeedingAdapter adapter=new GroupSeedingAdapter();
        seeding_lv.setAdapter(adapter);
    }

    public void createKnockout_list(){
        KnockoutAdapter adapter=new KnockoutAdapter();
        seeding_lv.setAdapter(adapter);
    }

    public int calcRows(int top,int bottom)
    {
        int q=top/bottom;
        int r=top%bottom;
        if(r==0)
            return q;
        else
            return q+1;
    }



    public void createModifiedList(){
        int curr_g1=0,curr_g2=1;
        for(int i=0;i<groups;i++)
        {
         team_name_modified.add((i*team_per_group)+i,"GROUP "+(i+1));
        }
        for(int i=0;i<calcRows(groups,2);i++)
        {

            for(int j=0;j<(team_per_group+1);j++)
            {
                team_name_customized.add(""+team_name_modified.get(curr_g1*(team_per_group+1)+j));
                if(curr_g2<groups)
                team_name_customized.add(""+team_name_modified.get(curr_g2*(team_per_group+1)+j));
            }
            curr_g1+=2;
            curr_g2+=2;
        }
    }

    public void recoverList(){
        int curr_g1=0;
        List<String> teams_customized_final=new ArrayList<>(team_name_customized);
        Iterator iterator=teams_customized_final.iterator();
        List<String> teams_finals=new ArrayList<>();
        while (iterator.hasNext())
        {
            if(Pattern.compile("^GROUP [0-9]+$").matcher(iterator.next().toString()).find())
            {
                iterator.remove();
            }
        }
        for(int i=0;i<calcRows(groups,2);i++)//for each two set
        {

            for(int j=0;j<team_per_group*2;j++)//no of rows in each set
            {
                if(j%2==0)
                teams_finals.add(""+teams_customized_final.get((curr_g1*team_per_group*2)+j));//matching 1 and 1 in each set
            }
            for(int j=0;j<team_per_group*2;j++)//no of rows in each set
            {
                if(j%2!=0)
                    teams_finals.add(""+teams_customized_final.get((curr_g1*team_per_group*2)+j));//matching 1 and 1 in each set
            }
            curr_g1++;
        }
        details.setTeams(teams_finals);

    }

    public void manageGroupFixtures(){
        List<fixtures> temp_fix=new ArrayList<>();
        if(details.getType().equalsIgnoreCase("league"))
        {
            temp_fix=generateGroupFixture(team_names,temp_fix);

        }
        else
        {
            List<String> teams=new ArrayList<>();
            teams=details.getTeams();
            for(int i=0;i<groups;i++)
            {
                List<String> each_group=new ArrayList<>();
                for(int j=0;j<team_per_group;j++)
                {
                    each_group.add(teams.get((i*team_per_group)+j));
                }
                temp_fix=generateGroupFixture(each_group,temp_fix);
            }

        }
        temp_fix=generateGroupFixWithTitles(temp_fix);
        details.setFixtures(temp_fix);
    }

    public List<fixtures> generateGroupFixWithTitles(List<fixtures> fix){
        List<fixtures> final_fix=new ArrayList<>();
        int md=1;
        int match_days;
        if(team_per_group%2!=0)
            match_days=team_per_group;
        else
            match_days=team_per_group-1;
        int matches_per_matchday=team_per_group/2;//for a single group
        int total_group_match=((team_per_group-1)*(team_per_group))/2;
        for(int i=0;i<match_days;i++)
        {
            fixtures f=new fixtures(1,"MATCHDAY "+md,"",0,0,"heading");
            final_fix.add(f);md++;
            for(int j=0;j<groups;j++)
            {
                for(int k=0;k<matches_per_matchday;k++)
                {
                    fixtures fi=fix.get((j*total_group_match)+(i*matches_per_matchday)+k);
                    String[] teams_stored=fi.getTeams();
                    final_fix.add(new fixtures(1,""+teams_stored[0],""+teams_stored[1],0,0,"not-played"));
                }
            }
        }
        if(details.isG_double())
        {
            for(int i=0;i<match_days;i++)
            {
                fixtures f=new fixtures(1,"MATCHDAY "+md,"",0,0,"heading");
                final_fix.add(f);md++;
                for(int j=0;j<groups;j++)
                {
                    for(int k=0;k<matches_per_matchday;k++)
                    {
                        fixtures fi=fix.get((j*total_group_match)+(i*matches_per_matchday)+k);
                        String[] teams_stored=fi.getTeams();
                        final_fix.add(new fixtures(1,""+teams_stored[1],""+teams_stored[0],0,0,"not-played"));
                    }
                }
            }
        }
        return final_fix;

    }

    public List<fixtures> generateGroupFixture(List<String> teams,List<fixtures> fix)
    {
        if(teams.size()%2!=0)
            teams.add("extra-bye");

        for(int i=0;i<teams.size()-1;i++)//no of matchdays
        {
            int last_index=teams.size()-1;
            for(int j=0;j<teams.size()/2;j++)//matching by splitting in half and froming pairs
            {
                if(!teams.get(j).equalsIgnoreCase("extra-bye") && !teams.get(last_index).equalsIgnoreCase("extra-bye"))
                {
                    fixtures f=new fixtures(1,teams.get(j),teams.get(last_index),0,0,"not-played");
                    fix.add(f);
                }
                last_index--;
                //Toast.makeText(getApplication(),""+teams.get(j)+" vs "+teams.get(j+mid),Toast.LENGTH_SHORT).show();
            }
            for(int k=0;k<teams.size()-2;k++)//shifting all elements by one pos except the first one
            {
                Collections.swap(teams,1,2+k);
            }
        }
        return fix;

    }

    public void generateTeamStats()
    {
        List<TeamStats> ts=details.getTeam_stats();
        List<String> teams=details.getTeams();
        if(details.getType().equalsIgnoreCase("knockout"))
        {
            ts.add(new TeamStats("Team Stats","heading"));
            for(int i=0;i<teams.size();i++)
            {
                ts.add(new TeamStats(teams.get(i),"active"));
                details.getTeam_stats_all().add(new TeamStats(teams.get(i),"active"));
            }
        }
        else
        {
            for(int i=0;i<groups;i++)
            {
                ts.add(new TeamStats("GROUP "+(i+1),"heading"));
                for(int j=0;j<team_per_group;j++){
                    ts.add(new TeamStats(teams.get((i*team_per_group)+j),"active"));
                    details.getTeam_stats_all().add(new TeamStats(teams.get((i*team_per_group)+j),"active"));
                }

            }
        }
    }

    public void start(View v)
    {
        Intent i;
        if(details.getType().toLowerCase().contains("group"))
        {
            recoverList();
            manageGroupFixtures();
            generateTeamStats();
            i=new Intent(this,main_screen.class);
            i.putExtra("class",details);
        }
        else
        {
            details.setTeams(team_names);
            genFixtureKnockout(team_names,1);
            generateTeamStats();
            i=new Intent(this,main_screen.class);
            i.putExtra("class",details);
        }

        startActivity(i);
        finish();
    }

    class GroupHolder{
        Button g1;
        Button g2;
        int index1;
        int index2;
    }

    class Knockoutholder{
        Button t1;
        Button t2;
        int index1;
        int index2;
    }

    class KnockoutAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return teams_count/2;
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
            final Knockoutholder holder;
            if(view==null) {
                holder=new Knockoutholder();
                view = getLayoutInflater().inflate(R.layout.knockout_seeding, null);
                holder.t1 = (Button) view.findViewById(R.id.team1);
                holder.t2 = (Button) view.findViewById(R.id.team2);
                view.setTag(holder);
            }
            else
                holder=(Knockoutholder) view.getTag();

            holder.index1=i*2;
            holder.index2=(i*2)+1;

            holder.t1.setText(""+team_names.get(holder.index1));
            holder.t2.setText(""+team_names.get(holder.index2));

            if(holder.index1==selected_index && selected==1)
            {
                holder.t1.setBackgroundColor(Color.GREEN);
            }
            else
            {
                holder.t1.setBackgroundColor(Color.WHITE);
            }

            if(holder.index2==selected_index && selected==1)
            {
                holder.t2.setBackgroundColor(Color.GREEN);
            }
            else
            {
                holder.t2.setBackgroundColor(Color.WHITE);
            }

            holder.t1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(selected==0)
                    {
                        selected_index=holder.index1;
                        selected=1;
                        createKnockout_list();
                    }
                    else if(selected==1 && selected_index==holder.index1)
                    {
                        selected=0;
                        createKnockout_list();
                    }
                    else if(selected==1 && selected_index!=holder.index1)
                    {
                        String temp=team_names.get(selected_index);
                        team_names.set(selected_index,team_names.get(holder.index1));
                        team_names.set(holder.index1,temp);
                        selected=0;
                        createKnockout_list();
                    }
                }
            });

            holder.t2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(selected==0)
                    {
                        selected_index=holder.index2;
                        selected=1;
                        createKnockout_list();
                    }
                    else if(selected==1 && selected_index==holder.index2)
                    {
                        selected=0;
                        createKnockout_list();
                    }
                    else if(selected==1 && selected_index!=holder.index2)
                    {
                        String temp=team_names.get(selected_index);
                        team_names.set(selected_index,team_names.get(holder.index2));
                        team_names.set(holder.index2,temp);
                        selected=0;
                        createKnockout_list();
                    }
                }
            });




            return view;
        }
    }

    class GroupSeedingAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list_view_size;
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
            final GroupHolder holder;
            if(view==null) {
                holder=new GroupHolder();
                view = getLayoutInflater().inflate(R.layout.group_seeding, null);
                holder.g1 = (Button) view.findViewById(R.id.group_one);
                holder.g2 = (Button) view.findViewById(R.id.group_two);
                view.setTag(holder);
            }
            else
                holder=(GroupHolder) view.getTag();

                    holder.index1=i*2;
                    holder.index2=(i*2)+1;
                    holder.g1.setText(""+team_name_customized.get(holder.index1));
                    holder.g2.setText(""+team_name_customized.get(holder.index2));


                if(Pattern.compile("^GROUP [0-9]+$").matcher(holder.g1.getText().toString()).find())
                {
                    holder.g1.setBackgroundColor(Color.BLACK);
                    holder.g1.setTextColor(Color.WHITE);
                }
                else if(holder.index1==selected_index && selected==1)
                {
                    holder.g1.setBackgroundColor(Color.GREEN);
                }
                else
                {
                    holder.g1.setBackgroundColor(Color.WHITE);
                }

                if(Pattern.compile("^GROUP [0-9]+$").matcher(holder.g2.getText().toString()).find())
                {
                    holder.g2.setBackgroundColor(Color.BLACK);
                    holder.g2.setTextColor(Color.WHITE);
                }
                else if(holder.index2==selected_index && selected==1)
                {
                    holder.g2.setBackgroundColor(Color.GREEN);
                }
                else
                {
                holder.g2.setBackgroundColor(Color.WHITE);
                }



                holder.g1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!Pattern.compile("^GROUP [0-9]+$").matcher(holder.g1.getText().toString()).find())
                        {
                            if(selected==0)
                            {
                                selected_index=holder.index1;
                                selected=1;
                                create_list_group();
                            }
                            else if(selected==1 && selected_index==holder.index1)
                            {
                                selected=0;
                                create_list_group();
                            }
                            else if(selected==1 && selected_index!=holder.index1)
                            {
                                String temp=team_name_customized.get(selected_index);
                                team_name_customized.set(selected_index,team_name_customized.get(holder.index1));
                                team_name_customized.set(holder.index1,temp);
                                selected=0;
                                create_list_group();
                            }
                        }

                    }
                });

            holder.g2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                          if(!Pattern.compile("^GROUP [0-9]+$").matcher(holder.g2.getText().toString()).find())
                          {
                              if(selected==0)
                              {
                                  selected_index=holder.index2;
                                  selected=1;
                                  create_list_group();
                              }
                              else if(selected==1 && selected_index==holder.index2)
                              {
                                  selected=0;
                                  create_list_group();
                              }
                              else if(selected==1 && selected_index!=holder.index2)
                              {
                                  String temp=team_name_customized.get(selected_index);
                                  team_name_customized.set(selected_index,team_name_customized.get(holder.index2));
                                  team_name_customized.set(holder.index2,temp);
                                  selected=0;
                                  create_list_group();
                              }
                          }

                }
            });


            return view;
        }
    }

}
