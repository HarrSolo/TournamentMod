package com.project.harrison.tournamentmod;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Harrison on 07-04-2018.
 */

public class stats_adapter extends ArrayAdapter<String> {
    private List<TeamStats> teams;
    Context context;
    private float grade;

    public stats_adapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects,List<TeamStats> teamsfull) {
        super(context, resource, objects);
        this.teams=teamsfull;
        this.context=context;
    }

    class Holder{
        TextView topic,team,stat,info;
        Button see_all;
        int pos;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Holder holder;
        if(convertView==null) {
            holder=new Holder();
            convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.stats_listview, null);
            holder.topic=(TextView)convertView.findViewById(R.id.stats_topic);
            holder.team=(TextView)convertView.findViewById(R.id.stats_team);
            holder.stat=(TextView)convertView.findViewById(R.id.stats_grade);
            holder.info=(TextView)convertView.findViewById(R.id.stats_extra);
            holder.see_all=(Button) convertView.findViewById(R.id.stats_see_all);
            convertView.setTag(holder);
        }
        else
            holder=(Holder) convertView.getTag();


        holder.topic.setText(""+getItem(position));
        holder.pos=position;

        if(teams.size()>0)
        {
            sort(position);
            int c=1;


            if(position==0 || position==1)
            {   //for color
                if(position==1)
                {
                    holder.stat.setTextColor(Color.parseColor("#D50000"));
                    Collections.reverse(teams);
                }
                else
                {
                    holder.stat.setTextColor(Color.parseColor("#1B5E20"));
                }
                String r=""+teams.get(0).getName().toUpperCase()+",";

                while (c<teams.size() && teams.get(c-1).getPlayed()==teams.get(c).getPlayed() && teams.get(c-1).off_rate()==teams.get(c).off_rate() )
                { r=r+""+teams.get(c).getName().toUpperCase()+",";c++;}
                r=r.substring(0,r.length()-1);

                if(position==0 && teams.get(0).off_rate()==0)
                {
                    holder.team.setText("-");
                    holder.stat.setText("-");
                    holder.info.setText("");
                }
                else
                {
                    holder.team.setText(""+r);
                    holder.stat.setText(""+teams.get(0).off_rate());
                    holder.info.setText("SCORED : "+teams.get(0).getGF());
                }
            }
            else if(position==2 || position==3) {
                //for color
                if(position==3)
                {
                    holder.stat.setTextColor(Color.parseColor("#D50000"));
                    Collections.reverse(teams);
                }
                else
                {
                    holder.stat.setTextColor(Color.parseColor("#1B5E20"));
                }

                String r=""+teams.get(0).getName().toUpperCase()+",";

                while (c<teams.size() && teams.get(c-1).getPlayed()==teams.get(c).getPlayed() && teams.get(c-1).def_rate()==teams.get(c).def_rate() )
                { r+=""+teams.get(c).getName().toUpperCase()+",";c++;}
                r=r.substring(0,r.length()-1);

                if(position==3 && teams.get(0).def_rate()==0)
                {
                    holder.team.setText("-");
                    holder.stat.setText("-");
                    holder.info.setText("");
                }
                else
                {
                    holder.team.setText(""+r);
                    holder.stat.setText(""+teams.get(0).def_rate());
                    holder.info.setText("CONCEDED : "+teams.get(0).getGA());
                }

            }
            else if(position==4)
            {
                String r=""+teams.get(0).getName().toUpperCase()+",";
                while (c<teams.size() && teams.get(c-1).getPlayed()==teams.get(c).getPlayed() && teams.get(c-1).getCs()==teams.get(c).getCs() )
                { r+=""+teams.get(c).getName().toUpperCase()+",";c++;}
                r=r.substring(0,r.length()-1);

                if(teams.get(0).getCs()!=0)
                {
                    holder.stat.setTextColor(Color.parseColor("#1B5E20"));
                    holder.team.setText(""+r);
                    holder.stat.setText(""+teams.get(0).getCs());
                    holder.info.setText("PLAYED : "+teams.get(0).getPlayed());
                }
                else
                {
                    holder.team.setText("-");
                    holder.stat.setText("-");
                    holder.info.setText("");
                }

            }
            else if(position==5)
            {
                String r=""+teams.get(0).getName().toUpperCase()+",";
                while (c<teams.size() && teams.get(c-1).getPlayed()==teams.get(c).getPlayed() && teams.get(c-1).getWins()==teams.get(c).getWins() )
                { r+=""+teams.get(c).getName().toUpperCase()+",";c++;}
                r=r.substring(0,r.length()-1);

                if(teams.get(0).getWins()!=0)
                {
                    holder.stat.setTextColor(Color.parseColor("#1B5E20"));
                    holder.team.setText(""+r);
                    holder.stat.setText(""+teams.get(0).getWins());
                    holder.info.setText("PLAYED : "+teams.get(0).getPlayed());
                }
                else
                {
                    holder.team.setText("-");
                    holder.stat.setText("-");
                    holder.info.setText("");
                }

            }
            else if(position==6)
            {
                String r=""+teams.get(0).getName().toUpperCase()+",";
                while (c<teams.size() && teams.get(c-1).getPlayed()==teams.get(c).getPlayed() && teams.get(c-1).getDraw()==teams.get(c).getDraw() )
                { r+=""+teams.get(c).getName().toUpperCase()+",";c++;}
                r=r.substring(0,r.length()-1);

                if(teams.get(0).getDraw()!=0)
                {
                    holder.stat.setTextColor(Color.parseColor("#F9A825"));
                    holder.team.setText(""+r);
                    holder.stat.setText(""+teams.get(0).getDraw());
                    holder.info.setText("PLAYED : "+teams.get(0).getPlayed());
                }
                else
                {
                    holder.team.setText("-");
                    holder.stat.setText("-");
                    holder.info.setText("");
                }

            }
            else if(position==7)
            {
                String r=""+teams.get(0).getName().toUpperCase()+",";
                while (c<teams.size() && teams.get(c-1).getPlayed()==teams.get(c).getPlayed() && teams.get(c-1).getLoss()==teams.get(c).getLoss() )
                { r+=""+teams.get(c).getName().toUpperCase()+",";c++;}
                r=r.substring(0,r.length()-1);

                if(teams.get(0).getLoss()!=0)
                {
                    holder.stat.setTextColor(Color.parseColor("#D50000"));
                    holder.team.setText(""+r);
                    holder.stat.setText(""+teams.get(0).getLoss());
                    holder.info.setText("PLAYED : "+teams.get(0).getPlayed());
                }
                else
                {
                    holder.team.setText("-");
                    holder.stat.setText("-");
                    holder.info.setText("");
                }

            }
            else
            {
                String r=""+teams.get(0).getName().toUpperCase()+",";
                while (c<teams.size() && teams.get(c-1).getPlayed()==teams.get(c).getPlayed() && teams.get(c-1).getGD()==teams.get(c).getGD() )
                { r+=""+teams.get(c).getName().toUpperCase()+",";c++;}
                r=r.substring(0,r.length()-1);

                holder.stat.setTextColor(Color.parseColor("#1B5E20"));
                holder.team.setText(""+r);
                holder.stat.setText(""+teams.get(0).getGD());
                holder.info.setText("PLAYED : "+teams.get(0).getPlayed());
            }
        }
        else {
            holder.team.setText("-");
            holder.stat.setText("-");
            holder.info.setText("");

        }


        holder.see_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sort(holder.pos);
                if(holder.pos==1 || holder.pos==3)
                    Collections.reverse(teams);
                Intent i=new Intent(context,See_all_rank.class);
                i.putExtra("list",(Serializable)teams);
                i.putExtra("statPos",holder.pos);
                context.startActivity(i);
            }
        });


        return convertView;
    }



    private void sort(int pos)
    {
           if(pos==0 || pos==1)
           {
               Collections.sort(teams, new Comparator<TeamStats>() {
                   @Override
                   public int compare(TeamStats t1, TeamStats t2) {
//                      if(t1.off_rate()>t2.off_rate())
//                          return -1;
//                      else if(t1.off_rate()<t2.off_rate())
//                          return 1;
//                      else
//                          return 0;
                       if(t1.off_rate()==t2.off_rate())
                       {
                          if(t1.getPlayed()==t2.getPlayed())
                          {
                              return 0;
                          }
                          else return (t2.getPlayed()-t1.getPlayed());
                       }
                       else return (int)(t2.off_rate()-t1.off_rate());

                   }
               });
           }
           else if(pos==2||pos==3)
           {
               Collections.sort(teams, new Comparator<TeamStats>() {
                   @Override
                   public int compare(TeamStats t1, TeamStats t2) {
//                       if(t1.def_rate()<t2.def_rate())
//                           return -1;
//                       else if(t1.def_rate()>t2.def_rate())
//                           return 1;
//                       else
//                           return 0;
                       if(t1.def_rate()==t2.def_rate())
                       {
                           if(t1.getPlayed()==t2.getPlayed())
                           {
                               return 0;
                           }
                           else return (t2.getPlayed()-t1.getPlayed());
                       }
                       else return (int)(t1.def_rate()-t2.def_rate());


                   }
               });
           }
           else if(pos==4)
           {
               Collections.sort(teams, new Comparator<TeamStats>() {
                   @Override
                   public int compare(TeamStats t1, TeamStats t2) {
//                       if(t1.getCs()>t2.getCs())
//                           return -1;
//                       else if(t1.getCs()<t2.getCs())
//                           return 1;
//                       else
//                           return 0;
                       if(t1.getCs()==t2.getCs())
                       {
                           if(t1.getPlayed()==t2.getPlayed())
                           {
                               return 0;
                           }
                           else return (t2.getPlayed()-t1.getPlayed());
                       }
                       else return (t2.getCs()-t1.getCs());

                   }
               });
           }
           else if(pos==5)
           {
               Collections.sort(teams, new Comparator<TeamStats>() {
                   @Override
                   public int compare(TeamStats t1, TeamStats t2) {
//                       if(t1.getWins()>t2.getWins())
//                           return -1;
//                       else if(t1.getWins()<t2.getWins())
//                           return 1;
//                       else
//                           return 0;
                       if(t1.getWins()==t2.getWins())
                       {
                           if(t1.getPlayed()==t2.getPlayed())
                           {
                               return 0;
                           }
                           else return (t1.getPlayed()-t2.getPlayed());
                       }
                       else return (t2.getWins()-t1.getWins());

                   }
               });
           }
           else if(pos==6)
           {
               Collections.sort(teams, new Comparator<TeamStats>() {
                   @Override
                   public int compare(TeamStats t1, TeamStats t2) {
//                       if(t1.getDraw()>t2.getDraw())
//                           return -1;
//                       else if(t1.getDraw()<t2.getDraw())
//                           return 1;
//                       else
//                           return 0;
                       if(t1.getDraw()==t2.getDraw())
                       {
                           if(t1.getPlayed()==t2.getPlayed())
                           {
                               return 0;
                           }
                           else return (t1.getPlayed()-t2.getPlayed());
                       }
                       else return (t2.getDraw()-t1.getDraw());

                   }
               });
           }
           else if(pos==7)
           {
               Collections.sort(teams, new Comparator<TeamStats>() {
                   @Override
                   public int compare(TeamStats t1, TeamStats t2) {
//                       if(t1.getLoss()>t2.getLoss())
//                           return -1;
//                       else if(t1.getLoss()<t2.getLoss())
//                           return 1;
//                       else
//                           return 0;
                       if(t1.getLoss()==t2.getLoss())
                       {
                           if(t1.getPlayed()==t2.getPlayed())
                           {
                               return 0;
                           }
                           else return (t1.getPlayed()-t2.getPlayed());
                       }
                       else return (t2.getLoss()-t1.getLoss());

                   }
               });
           }
           else if(pos==8)
           {
               Collections.sort(teams, new Comparator<TeamStats>() {
                   @Override
                   public int compare(TeamStats t1, TeamStats t2) {
//                       if(t1.getGD()>t2.getGD())
//                           return -1;
//                       else if(t1.getGD()<t2.getGD())
//                           return 1;
//                       else
//                           return 0;
                       if(t1.getGD()==t2.getGD())
                       {
                           if(t1.getPlayed()==t2.getPlayed())
                           {
                               return 0;
                           }
                           else return (t1.getPlayed()-t2.getPlayed());
                       }
                       else return (t2.getGD()-t1.getGD());

                   }
               });
           }

    }
}
