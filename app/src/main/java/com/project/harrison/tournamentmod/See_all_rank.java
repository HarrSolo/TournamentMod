package com.project.harrison.tournamentmod;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


/**
 * Created by Harrison on 08-04-2018.
 */

public class See_all_rank extends Activity {
    private List<TeamStats> teams;
    private int stat_pos;
    ListView see_all_lv;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_all_rank);
        Intent i=getIntent();
        stat_pos=i.getIntExtra("statPos",0);
        teams=(List<TeamStats>)i.getSerializableExtra("list");
        see_all_lv=findViewById(R.id.see_all_lv);
        SeeAllAdapter see_all_adap=new SeeAllAdapter();
        see_all_lv.setAdapter(see_all_adap);

    }

    private class Holder{
        TextView rank,team,stats;
        int pos;
    }

    class SeeAllAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return teams.size();
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
            final Holder holder;
            if(view==null) {
                holder=new Holder();
                view = getLayoutInflater().inflate(R.layout.see_all, null);
                holder.rank=(TextView)view.findViewById(R.id.see_all_rank);
                holder.team=(TextView)view.findViewById(R.id.see_all_team);
                holder.stats=(TextView)view.findViewById(R.id.see_all_points);
                view.setTag(holder);
            }
            else
                holder=(Holder) view.getTag();
            holder.rank.setText("#"+(i+1));
            holder.team.setText(""+teams.get(i).getName().toUpperCase());

            if(stat_pos==0 || stat_pos==1)
            {
                holder.stats.setText(""+teams.get(i).off_rate());
            }
            else if(stat_pos==2 || stat_pos==3)
            {
                holder.stats.setText(""+teams.get(i).def_rate());
            }
            else if(stat_pos==4)
            {
                holder.stats.setText(""+teams.get(i).getCs());
            }
            else if(stat_pos==5)
            {
                holder.stats.setText(""+teams.get(i).getWins());
            }
            else if(stat_pos==6)
            {
                holder.stats.setText(""+teams.get(i).getDraw());
            }
            else if(stat_pos==7)
            {
                holder.stats.setText(""+teams.get(i).getLoss());
            }
            else if(stat_pos==8)
            {
                holder.stats.setText(""+teams.get(i).getGD());
            }


            if(stat_pos==0 ||stat_pos==2 ||stat_pos==4 ||stat_pos==5 ||stat_pos==8 )
            {
               holder.stats.setTextColor(Color.parseColor("#1B5E20"));
            }
            else if(stat_pos==1 ||stat_pos==3 ||stat_pos==7)
            {
                holder.stats.setTextColor(Color.parseColor("#D50000"));
            }
            else
            {
                holder.stats.setTextColor(Color.parseColor("#F9A825"));
            }



            return view;
        }
    }
}
