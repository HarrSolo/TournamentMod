package com.project.harrison.tournamentmod;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Harrison on 10-03-2018.
 */

public class standings_adapter extends ArrayAdapter<TeamStats> {
    private int highlight,teams_per_group;
    public standings_adapter(Context context, int resource ,List<TeamStats> objects,int highlight,int teams_per_group)
    {
        super(context, resource, objects);
        this.highlight=highlight;
        this.teams_per_group=teams_per_group;
    }
    class Holder{
        TextView name,w,d,l,gd,pts,played;
        int index,h;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Holder holder;
        if(view==null) {
            holder=new Holder();
            view = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.standings, null);
            holder.name=(TextView)view.findViewById(R.id.name);
            holder.played=(TextView)view.findViewById(R.id.played);
            holder.w=(TextView)view.findViewById(R.id.won);
            holder.d=(TextView)view.findViewById(R.id.draw);
            holder.l=(TextView)view.findViewById(R.id.lost);
            holder.gd=(TextView)view.findViewById(R.id.gd);
            holder.pts=(TextView)view.findViewById(R.id.points);
            view.setTag(holder);
        }
        else
            holder=(Holder) view.getTag();
        TeamStats teamStats=getItem(i);

        holder.index=i;
        holder.h=i%teams_per_group;
        int[] stats=teamStats.getStats();

        if(teamStats.getStatus().equalsIgnoreCase("heading"))
        {
            holder.name.setText(""+teamStats.getName());
            holder.played.setText("P");
            holder.w.setText("W");
            holder.d.setText("D");
            holder.l.setText("L");
            holder.gd.setText("GD");
            holder.pts.setText("PTS");
            holder.name.setBackgroundColor(Color.BLACK);
            holder.played.setBackgroundColor(Color.BLACK);
            holder.w.setBackgroundColor(Color.BLACK);
            holder.d.setBackgroundColor(Color.BLACK);
            holder.l.setBackgroundColor(Color.BLACK);
            holder.gd.setBackgroundColor(Color.BLACK);
            holder.pts.setBackgroundColor(Color.BLACK);
            holder.name.setTextColor(Color.WHITE);
            holder.played.setTextColor(Color.WHITE);
            holder.w.setTextColor(Color.WHITE);
            holder.d.setTextColor(Color.WHITE);
            holder.l.setTextColor(Color.WHITE);
            holder.gd.setTextColor(Color.WHITE);
            holder.pts.setTextColor(Color.WHITE);
        }
        else
        {
            holder.name.setText(""+teamStats.getName().toUpperCase());
            holder.played.setText(""+(stats[0]+stats[1]+stats[2]));
            holder.w.setText(""+stats[0]);
            holder.d.setText(""+stats[1]);
            holder.l.setText(""+stats[2]);
            holder.gd.setText(""+(stats[3]-stats[4]));
            holder.pts.setText(""+stats[5]);
            holder.name.setTextColor(Color.BLACK);
            holder.played.setTextColor(Color.BLACK);
            holder.w.setTextColor(Color.BLACK);
            holder.d.setTextColor(Color.BLACK);
            holder.l.setTextColor(Color.BLACK);
            holder.gd.setTextColor(Color.BLACK);
            holder.pts.setTextColor(Color.BLACK);

            if(holder.h<=highlight && holder.h>0)
            {
                holder.name.setBackgroundColor(Color.parseColor("#CCFF90"));
                holder.played.setBackgroundColor(Color.parseColor("#CCFF90"));
                holder.w.setBackgroundColor(Color.parseColor("#CCFF90"));
                holder.d.setBackgroundColor(Color.parseColor("#CCFF90"));
                holder.l.setBackgroundColor(Color.parseColor("#CCFF90"));
                holder.gd.setBackgroundColor(Color.parseColor("#CCFF90"));
                holder.pts.setBackgroundColor(Color.parseColor("#CCFF90"));
            }
            else{
                holder.name.setBackgroundColor(Color.WHITE);
                holder.played.setBackgroundColor(Color.WHITE);
                holder.w.setBackgroundColor(Color.WHITE);
                holder.d.setBackgroundColor(Color.WHITE);
                holder.l.setBackgroundColor(Color.WHITE);
                holder.gd.setBackgroundColor(Color.WHITE);
                holder.pts.setBackgroundColor(Color.WHITE);
            }

        }




        return view;
    }
}
