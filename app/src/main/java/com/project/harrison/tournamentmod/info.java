package com.project.harrison.tournamentmod;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.job.JobInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Harrison on 01-03-2018.
 */

public class info extends Activity {

    //for selection
    Spinner type;ImageView type_image;
    String tour_types[]={"League","Knockout","Group+Knockout"};
    String tournament_type,name;int error=0;

    //all linear layouts and check box buttons
    CheckBox g_double,k_double,f_double,third_place;
    Boolean gdouble,fdouble,kdouble,thirdplace;//for recieving check box status
    LinearLayout no_of_groups,round,qualified_teams_count;
    TextView round_text;

    //editbox for getting user input
    EditText teams_et,groups_et,win_et,draw_et,loss_et,qualifier_count_et,name_et;
    int teams=2,groups=1,win=3,draw=1,loss=0,qualifier_count=1;

    //for switching purposes and team names
    ViewSwitcher tab;Button b_names,b_details;int tab_flag=1;
    ListView team_names_list_view;CustomAdapter names_adpater;
    List<String>  team_names_list=new ArrayList<>();

    int matches=0,k_matches=0;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);

        //initializing lin layouts and check box
        no_of_groups=(LinearLayout)findViewById(R.id.no_of_groups);
        round=(LinearLayout)findViewById(R.id.round);
        qualified_teams_count=(LinearLayout)findViewById(R.id.qualified_team_count);
        g_double=(CheckBox)findViewById(R.id.group_double);
        k_double=(CheckBox)findViewById(R.id.knockout_double);
        f_double=(CheckBox)findViewById(R.id.finals_double);
        third_place=(CheckBox)findViewById(R.id.third_place);

        //initializing edit texts
        teams_et=(EditText)findViewById(R.id.teams);
        groups_et=(EditText)findViewById(R.id.groups);
        win_et=(EditText)findViewById(R.id.win_points);
        draw_et=(EditText)findViewById(R.id.draw_points);
        loss_et=(EditText)findViewById(R.id.loss_points);
        qualifier_count_et=(EditText)findViewById(R.id.group_qualifiers_count);
        name_et=(EditText)findViewById(R.id.name);

        //for switching
        b_names=(Button)findViewById(R.id.button_names);
        b_details=(Button)findViewById(R.id.button_details);
        tab=(ViewSwitcher) findViewById(R.id.tab);
        team_names_list_view=(ListView)findViewById(R.id.names);
        tab_flag=1;

        //for type selection and corresponding actions
        round_text=(TextView)findViewById(R.id.round_text);
        type=(Spinner)findViewById(R.id.type);
        type_image=(ImageView)findViewById(R.id.type_image);
        ArrayAdapter<String> types_adapter=new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner_item,tour_types);
        type.setAdapter(types_adapter);
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                initial_visibility();
                tournament_type=tour_types[i];
                check();
                create_teams_list();
                switch (i)
                {
                    case 0: type_image.setImageResource(R.drawable.cup3);
                            disableChildrens(qualified_teams_count,round,false,0.3f);
                            for(int j = 0; j < ((LinearLayout) no_of_groups).getChildCount(); j++){
                            ((View)((LinearLayout) no_of_groups).getChildAt(j)).setAlpha(0.3f);
                            ((View)((LinearLayout) no_of_groups).getChildAt(j)).setEnabled(false );
                            }
                            groups_et.setText("1");
                            qualifier_count_et.setText("1");
                            f_double.setAlpha(0.3f);
                            f_double.setEnabled(false);
                            k_double.setAlpha(0.3f);
                            k_double.setEnabled(false);
                            third_place.setAlpha(0.3f);
                            third_place.setEnabled(false);
                            break;
                    case 1:type_image.setImageResource(R.drawable.cup2);
                           disableChildrens(qualified_teams_count,no_of_groups,false,0.3f);
                           g_double.setAlpha(0.3f);
                           g_double.setEnabled(false);
                           break;
                    case 2:type_image.setImageResource(R.drawable.cup1);
                           break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //ONCHANGE listener for all edit texts
        teams_et.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(teams_et.getText().toString().trim().length() != 0)
                {
                    teams=Integer.parseInt(teams_et.getText().toString());
                }
                else
                {
                    teams=0;
                }
            }
            @Override public void afterTextChanged(Editable editable) {check();create_teams_list();}
        });
        groups_et.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(groups_et.getText().toString().trim().length() != 0)
                {
                    groups=Integer.parseInt(groups_et.getText().toString());
                }
                else
                {
                    groups=0;
                }
            }
            @Override public void afterTextChanged(Editable editable) {
                if(tournament_type.equalsIgnoreCase("knockout") || tournament_type.equalsIgnoreCase("group+knockout"))
                    check();
            }
        });
        qualifier_count_et.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(qualifier_count_et.getText().toString().trim().length() != 0)
                {
                    qualifier_count=Integer.parseInt(qualifier_count_et.getText().toString());
                }
                else
                {
                    qualifier_count=0;
                }
            }
            @Override public void afterTextChanged(Editable editable) {
                if(tournament_type.equalsIgnoreCase("knockout") || tournament_type.equalsIgnoreCase("group+knockout"))
                    check();
            }
        });

    }

    public void initial_visibility()
    {
        disableChildrens(qualified_teams_count,round,true,1f);
        for(int i = 0; i < ((LinearLayout) no_of_groups).getChildCount(); i++){
            ((View)((LinearLayout) no_of_groups).getChildAt(i)).setAlpha(1f);
            ((View)((LinearLayout) no_of_groups).getChildAt(i)).setEnabled(true);
        }
        g_double.setAlpha(1f);
        g_double.setEnabled(true);
        f_double.setAlpha(1f);
        f_double.setEnabled(true);
        k_double.setAlpha(1f);
        k_double.setEnabled(true);
        third_place.setAlpha(1f);
        third_place.setEnabled(true);

    }

    //for setting the childs of the linera layout at given visibility and enabled?
    public void disableChildrens(LinearLayout layout1,LinearLayout layout2,boolean bool,float alpha)
    {
        for(int i = 0; i < ((LinearLayout) layout1).getChildCount(); i++){
            ((View)((LinearLayout) layout1).getChildAt(i)).setAlpha(alpha);
            ((View)((LinearLayout) layout1).getChildAt(i)).setEnabled(bool);

        }
        for(int i = 0; i < ((LinearLayout) layout2).getChildCount(); i++){
            ((View)((LinearLayout) layout2).getChildAt(i)).setAlpha(alpha);
            ((View)((LinearLayout) layout2).getChildAt(i)).setEnabled(bool);
        }
    }

    public void check()
    {
        if(tournament_type.equalsIgnoreCase("league"))
        {
            group_stage_checker();
        }
        else if(tournament_type.equalsIgnoreCase("knockout"))
        {
            knockout_stage_set(teams);
        }
        else if(tournament_type.equalsIgnoreCase("group+knockout"))
        {
            group_stage_checker();
            if(error!=1)
            {
                int qualifying_teams=qualifier_count*groups;
                knockout_stage_set(qualifying_teams);
            }
            else
            {
                round_text.setText("");
            }

        }

    }

    public void group_stage_checker()
    {
        if(groups!=0 && teams!=0)
        {
            if(teams%groups!=0 || teams/groups<2)
            {
                error=1;
            }
            else
            {
                error=0;
            }
        }
        else
        {
            error=1;
        }
    }

    public void ready_list()
    {
        int size=team_names_list.size();
        for(int i=0;i<teams;i++)
        {
            if(i>=size)
                team_names_list.add("");
        }
    }

    public void knockout_stage_set(int teams)
    {
        String r;
        if(teams>1 && ((teams & (teams - 1)) == 0))
        {
            error=0;
            if(teams==2)
            {
                r="Finals";
                k_double.setEnabled(false);
                third_place.setEnabled(false);
                k_double.setChecked(false);
                third_place.setChecked(false);
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
            round_text.setText(""+r);
        }
        else
        {
            round_text.setText("");
            error=1;
        }
    }

    public void calculateTotalMatches(){
        int teams_per_group=teams/groups;
        if(tournament_type.equalsIgnoreCase("league"))
        {
            matches=(teams_per_group*(teams_per_group-1))/2;
            if(g_double.isChecked())
                matches=matches*2;
        }
        else if(tournament_type.equalsIgnoreCase("knockout"))
        {
            if(teams>2){
                k_matches+=KnockoutMatches(teams);
                if(third_place.isChecked())
                    k_matches++;
                if(k_double.isChecked())
                    k_matches*=2;
            }
            if(f_double.isChecked())
                k_matches+=2;
            else
                k_matches++;
            matches+=k_matches;
        }
        else {
            matches=(teams_per_group*(teams_per_group-1))/2;
            matches*=groups;
            if(g_double.isChecked())
                matches=matches*2;
            if((groups*qualifier_count)>2)
            {
                k_matches+=KnockoutMatches(groups*qualifier_count);
                if(third_place.isChecked())
                    k_matches++;
                if(k_double.isChecked())
                    k_matches*=2;
            }

            if(f_double.isChecked())
                k_matches+=2;
            else
                k_matches++;
            matches+=k_matches;
        }
    }

    public int KnockoutMatches(int teams){
        if(teams>4)
        {
            return teams/2+KnockoutMatches(teams/2);
        }
        else if(teams==4){
            return 2;
        }
        return 0;
    }

    private class ViewHolder { //To hold the listview items permanently
        EditText editText;
        int ref;
    }

    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return teams;
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
        public View getView(final int i, View view, ViewGroup viewGroup) {
            final ViewHolder holder;
            if(view==null) {
                holder=new ViewHolder();
                view = getLayoutInflater().inflate(R.layout.team_names, null);
                holder.editText = (EditText) view.findViewById(R.id.n);
                view.setTag(holder);
            }
            else
                holder=(ViewHolder)view.getTag();

            holder.ref=i;

            holder.editText.setText(""+team_names_list.get(holder.ref));

            holder.editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                      team_names_list.set(holder.ref,editable.toString());
                }
            });



            return view;
        }
    }

    public void switcher(View v)
    {
        String action=getResources().getResourceEntryName(v.getId());
        if(action.equals("button_details") && tab_flag!=1)
        {
            b_details.setBackgroundColor(Color.parseColor("#ffffff"));
            b_details.setTextColor(Color.parseColor("#000000"));
            b_names.setBackgroundColor(Color.parseColor("#000000"));
            b_names.setTextColor(Color.parseColor("#ffffff"));
            tab_flag=1;
            tab.showNext();


        }
        else if(action.equals("button_names") && tab_flag!=2)
        {
            b_names.setBackgroundColor(Color.parseColor("#ffffff"));
            b_names.setTextColor(Color.parseColor("#000000"));
            b_details.setBackgroundColor(Color.parseColor("#000000"));
            b_details.setTextColor(Color.parseColor("#ffffff"));
            tab_flag=2;
            create_teams_list();
            tab.showNext();

        }
    }

    public void create_teams_list()
    {
        ready_list();
        names_adpater=new CustomAdapter();
        team_names_list_view.setAdapter(names_adpater);
    }

    public void trimToSize(){
        int k = team_names_list.size();
        if ( k > teams )
            team_names_list.subList(teams, k).clear();
    }



    public void start(View v)
    {
        check();
        if(error==1)
            Toast.makeText(getApplicationContext(),"Enter valid info for "+tournament_type+" Tournament",Toast.LENGTH_SHORT).show();
        else
        {
            name=name_et.getText().toString();
            gdouble=g_double.isChecked();
            fdouble=f_double.isChecked();
            kdouble=k_double.isChecked();
            thirdplace=third_place.isChecked();
            trimToSize();
            calculateTotalMatches();
           // Toast.makeText(getApplicationContext(),""+matches,Toast.LENGTH_SHORT).show();
            if(allDistinct() && noNull() )
            {
                if(name_et.getText().toString().trim().length() > 0) {
                    Tournament details = new Tournament(team_names_list, teams, win, draw, loss, groups, qualifier_count, name, tournament_type, gdouble, kdouble, fdouble, thirdplace, matches);
                    Intent i = new Intent(getApplicationContext(), seeding.class);
                    i.putExtra("class", details);
                    startActivity(i);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Tournament name should not be empty!",Toast.LENGTH_SHORT).show();
                }
            }
            else if(!allDistinct() && !noNull())
            {
                Toast.makeText(getApplicationContext(),"Team names must be distinct with no empty names !",Toast.LENGTH_SHORT).show();
            }
            else if(!allDistinct())
            {
                Toast.makeText(getApplicationContext(),"Team names must be distinct!",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(),"Team names must not be empty !",Toast.LENGTH_SHORT).show();
            }

        }


    }

    public boolean allDistinct()
    {
        Set<String> foundStrings=new HashSet<>();
        for(String s:team_names_list)
        {
            if(foundStrings.contains(s))
                return false;
            foundStrings.add(s);
        }
        return true;
    }

    public boolean noNull()
    {
        for(String s:team_names_list)
        {
            if(s.equalsIgnoreCase(""))
                return false;
        }
        return true;
    }




}
