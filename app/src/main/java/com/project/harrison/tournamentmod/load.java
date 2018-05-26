package com.project.harrison.tournamentmod;

import android.app.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.R.attr.key;
import static android.content.ContentValues.TAG;
import static java.security.AccessController.getContext;

/**
 * Created by Harrison on 05-04-2018.
 */

public class load extends Activity {
    ListView load_tour;TextView message;Button add;
    firebase_data fb;
    TextView text;
    DBHandler db;
    Handler handler;
    HostAdapter host_adap;
    List<Tournament> t;
    private FirebaseDatabase mFirebasedatabase;
    private DatabaseReference mDatabaseReference;
    String userId;
    private FirebaseAuth mFireBaseAuth;
    List<hosted_tournaments> tour;

    int key_val;
    Tournament new_details;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load);
        handler=new Handler();

        mFirebasedatabase=FirebaseDatabase.getInstance();
        mDatabaseReference=mFirebasedatabase.getReference().child("Tournament");
        Intent i=getIntent();
        key_val=i.getIntExtra("key",1);
        new_details=(Tournament)i.getSerializableExtra("class");
        setContentView(R.layout.load);
        load_tour=(ListView)findViewById(R.id.load_tournaments);
        text=(TextView)findViewById(R.id.load_text);
        db=new DBHandler(this);
        mFireBaseAuth= FirebaseAuth.getInstance();
        FirebaseUser user=mFireBaseAuth.getCurrentUser();
        userId=user.getUid();
        message=(TextView)findViewById(R.id.online_info);
        add=(Button)findViewById(R.id.add);
        if(key_val<3)
        {

            t=db.getAll();
            loadAdapter load_ad=new loadAdapter();
            load_tour.setAdapter(load_ad);
            if(key_val==2)
                text.setBackgroundResource(R.drawable.save_text);
            else
                text.setBackgroundResource(R.drawable.load_logo);
        }
        else if(key_val==3){
            add.setBackgroundResource(R.drawable.refresh);
            add.setText("");
            db=new DBHandler(this);
            tour=getAllOnline();
            host_adap=new HostAdapter();
            load_tour.setAdapter(host_adap);
            text.setBackgroundResource(R.drawable.host_text);

        }
        else {
            HistoryAdapter his_adap=new HistoryAdapter();
            load_tour.setAdapter(his_adap);
            text.setBackgroundResource(R.drawable.history_text);
        }



    }


    class loadLVHolder{
        TextView name,progress,date;
        Button load,delete,update,host;
        int id_index;
        int pos;
    }

    class HistoryHolder{
        TextView name,type,date;
        Button open,delete;
        int pos;
    }

    class HostLVHolder{
        TextView name,admin,audience;
        Button share_admin,share_audience,update,delete;
        int pos;
    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
           // Toast.makeText(getApplicationContext(), "No Internet connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    public List<hosted_tournaments> getAllOnline()
    {
        final List<hosted_tournaments> tour=new ArrayList<>();
        if(isOnline())
        {
            //Toast.makeText(getApplicationContext(),"Checking...",Toast.LENGTH_SHORT).show();
            message.setBackgroundColor(Color.parseColor("#fff98c"));
            message.setText("CHECKING....");
            Query q;
            q=mDatabaseReference.orderByChild("user_id").equalTo(userId);

            q.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                       //Toast.makeText(getApplicationContext(),"Fetching Tournaments from Server",Toast.LENGTH_SHORT).show();
                        message.setBackgroundColor(Color.parseColor("#6ee354"));
                        message.setText("FETCHING....");
                        for(final DataSnapshot data: dataSnapshot.getChildren())
                        {
                            String key=data.getKey();
                            mDatabaseReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.getValue()!=null){
                                        fb=dataSnapshot.getValue(firebase_data.class);
                                        Gson gson=new Gson();
                                        Tournament d=gson.fromJson(fb.getData(),new TypeToken<Tournament>(){}.getType());
                                        if(!fb.getKey_code().equalsIgnoreCase(d.getKey()))
                                            tour.add(new hosted_tournaments(d.getName(),fb.getKey_code(),d.getKey()));
                                        host_adap.notifyDataSetChanged();


                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                        message.setAlpha(0f);
                    }
                    else {
                       // Toast.makeText(getApplicationContext(),"No Tournaments hosted !",Toast.LENGTH_SHORT).show();
                        message.setBackgroundColor(Color.parseColor("#ff2f28"));
                        message.setText("NO TOURNAMENTS HOSTED !");

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        else {
            //Toast.makeText(getApplicationContext(),"No Internet Connection!",Toast.LENGTH_SHORT).show();
            message.setBackgroundColor(Color.parseColor("#ff2f28"));
            message.setText("NO INTERNET CONNECTION FOUND !");
        }
        return tour;
    }

    class HistoryAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return db.getAllHistory().size();
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
            final HistoryHolder holder;
            if(view==null) {
                holder=new HistoryHolder();
                view = getLayoutInflater().inflate(R.layout.history, null);
                holder.name=(TextView)view.findViewById(R.id.history_name);
                holder.type=(TextView)view.findViewById(R.id.history_type);
                holder.date=(TextView)view.findViewById(R.id.history_date);
                holder.open=(Button) view.findViewById(R.id.open_history);
                holder.delete=(Button) view.findViewById(R.id.delete_history);
                view.setTag(holder);
            }
            else
                holder=(HistoryHolder) view.getTag();
            holder.pos=i;
            holder.name.setText(""+db.getAllHistory().get(i).getName());
            holder.type.setText(""+db.getAllHistory().get(i).getType().toUpperCase());
            holder.date.setText(""+db.getAllHistory().get(i).getDate());
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(load.this);
                    builder.setCancelable(false);
                    builder.setMessage("Are you sure you want to Delete this record?");
                    builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.setNegativeButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.deleteHistory(Integer.parseInt(db.getAllHistory().get(holder.pos).getId()));
                            notifyDataSetChanged();
                        }
                    });
                    builder.show();

                }
            });
            holder.open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isOnline())
                    {
                        Query q;
                        q=mDatabaseReference.orderByChild("key_code").equalTo(db.getAllHistory().get(holder.pos).getKey());
                        q.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists())
                                {
                                    for(final DataSnapshot data: dataSnapshot.getChildren())
                                    {
                                        if(data.getValue()!=null)
                                        {
                                            String key=data.getKey();
                                            mDatabaseReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if(dataSnapshot.getValue()!=null){
                                                        fb=dataSnapshot.getValue(firebase_data.class);
                                                        Gson gson=new Gson();
                                                        Tournament d=gson.fromJson(fb.getData(),new TypeToken<Tournament>(){}.getType());
                                                        //Toast.makeText(getApplicationContext(),"FOUND"+d.getAdmin_firebaseLocation()+d.getAdmin_key(),Toast.LENGTH_SHORT).show();
                                                        Intent in=new Intent(getApplicationContext(),main_screen.class);
                                                        if(fb.isOwner()){
                                                            d.setOwner(true);
                                                        }
                                                        else
                                                        {
                                                            d.setOwner(false);
                                                        }
                                                        in.putExtra("class",d);
                                                        startActivity(in);
                                                        finish();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }
                                }
                                else {
                                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(load.this);
                                    builder.setCancelable(false);
                                    builder.setTitle("Tournament not found !");
                                    builder.setMessage("Remove this record ?");
                                    builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    builder.setNegativeButton("YES", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            db.deleteHistory(Integer.parseInt(db.getAllHistory().get(holder.pos).getId()));
                                            notifyDataSetChanged();
                                        }
                                    });
                                    builder.show();

                                }

                            }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        String namee=db.getAllHistory().get(holder.pos).getName(),typee=db.getAllHistory().get(holder.pos).getType(),keyy=db.getAllHistory().get(holder.pos).getKey();
                        db.deleteHistory(db.searchHistory(namee,keyy));
                        db.insertHistory(keyy,typee,namee);
                        notifyDataSetChanged();


                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "No Internet connection!", Toast.LENGTH_LONG).show();
                    }
                }
            });
            return view;
        }
    }



    class HostAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return tour.size();
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
            final HostLVHolder holder;
            if(view==null) {
                holder=new HostLVHolder();
                view = getLayoutInflater().inflate(R.layout.hosted_listview, null);
                holder.name=(TextView)view.findViewById(R.id.host_name);
                holder.admin=(TextView)view.findViewById(R.id.admin_key_text);
                holder.audience=(TextView)view.findViewById(R.id.audience_key_text);
                holder.share_admin=(Button) view.findViewById(R.id.share_admin);
                holder.share_audience=(Button) view.findViewById(R.id.share_audience);
                holder.delete=(Button) view.findViewById(R.id.delete_host);
                holder.update=(Button) view.findViewById(R.id.continue_host);
                view.setTag(holder);
            }
            else
                holder=(HostLVHolder) view.getTag();
            holder.name.setText(""+tour.get(i).getName());
            holder.admin.setText("ADMIN : "+tour.get(i).getAdmin_key());
            holder.audience.setText("AUDIENCE : "+tour.get(i).getAudience_key());
            holder.pos=i;

            holder.share_audience.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareSub = "'"+tour.get(holder.pos).getName()+"' TOURNAMENT\nView it on Tournament Mod & Host app\nPASS KEY : "+tour.get(holder.pos).getAudience_key();
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareSub);
                    startActivity(Intent.createChooser(sharingIntent, "Share using"));
                }
            });

            holder.share_admin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareSub = "'"+tour.get(holder.pos).getName()+"' TOURNAMENT\nYou've been granted Admin privileges\nManage it on Tournament Mod & Host app\nPASS KEY : "+tour.get(holder.pos).getAdmin_key();
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareSub);
                    startActivity(Intent.createChooser(sharingIntent, "Share using"));
                }
            });

            holder.update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(isOnline())
                    {
                        Query q=mDatabaseReference.orderByChild("key_code").equalTo(tour.get(holder.pos).getAdmin_key());
                        q.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(final DataSnapshot data: dataSnapshot.getChildren())
                                {
                                    String key=data.getKey();
                                    mDatabaseReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.getValue()!=null){
                                                fb=dataSnapshot.getValue(firebase_data.class);
                                                Gson gson=new Gson();
                                                Tournament d=gson.fromJson(fb.getData(),new TypeToken<Tournament>(){}.getType());
                                                //Toast.makeText(getApplicationContext(),"FOUND"+d.getAdmin_firebaseLocation()+d.getAdmin_key(),Toast.LENGTH_SHORT).show();
                                                Intent in=new Intent(getApplicationContext(),main_screen.class);
                                                if(fb.isOwner())
                                                    d.setOwner(true);
                                                else
                                                    d.setOwner(false);
                                                in.putExtra("class",d);
                                                startActivity(in);
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }

                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "No Internet connection!", Toast.LENGTH_LONG).show();
                    }
                }
            });

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(load.this);
                    builder.setCancelable(false);
                    builder.setMessage("Are you sure you want to Delete this record?");
                    builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.setNegativeButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(isOnline())
                            {
                                Query removeQuery=mDatabaseReference.orderByChild("key_code").equalTo(tour.get(holder.pos).getAdmin_key());
                                removeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot data: dataSnapshot.getChildren()) {
                                            data.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e(TAG, "onCancelled", databaseError.toException());
                                    }
                                });

                                removeQuery=mDatabaseReference.orderByChild("key_code").equalTo(tour.get(holder.pos).getAudience_key());
                                removeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot data: dataSnapshot.getChildren()) {
                                            data.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e(TAG, "onCancelled", databaseError.toException());
                                    }
                                });
                                tour.remove(holder.pos);
                                notifyDataSetChanged();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "No Internet connection!", Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                    builder.show();

                }
            });
            return view;
        }
    }

    class loadAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return t.size();
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
            final loadLVHolder holder;
            if(view==null) {
                holder=new loadLVHolder();
                view = getLayoutInflater().inflate(R.layout.load_listview, null);
                holder.name=(TextView)view.findViewById(R.id.load_name);
                holder.date=(TextView)view.findViewById(R.id.updated_date);
                holder.progress=(TextView)view.findViewById(R.id.status_percent_tournament);
                holder.load=(Button) view.findViewById(R.id.load_button);
                holder.update=(Button) view.findViewById(R.id.update_button);
                holder.delete=(Button) view.findViewById(R.id.delete_button);
                holder.host=(Button) view.findViewById(R.id.host_load);
                view.setTag(holder);
            }
            else
                holder=(loadLVHolder) view.getTag();

            holder.id_index=t.get(i).getId();
            holder.pos=i;
            holder.date.setText(""+t.get(i).getLast_updated());
            holder.name.setText(""+ t.get(i).getName());
            if(t.get(i).getCurrent_round()>t.get(i).getRounds())
                holder.progress.setText("F");
            else
                holder.progress.setText(""+(int)calcProgress(i)+"%");

            if(key_val==1)
            {
                holder.update.setEnabled(false);
                holder.load.setEnabled(true);
                holder.delete.setEnabled(true);
            }
            else
            {
                holder.update.setEnabled(true);
                holder.load.setEnabled(false);
                holder.delete.setEnabled(false);

            }

            if(t.get(i).getFirebase_location()==null)
            {
                holder.host.setAlpha(0f);
            }
            else {
                holder.host.setAlpha(1f);
            }


            holder.load.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent in=new Intent(getApplicationContext(),main_screen.class);
                    in.putExtra("class",t.get(holder.pos));
                    Toast.makeText(getApplicationContext(),"Loaded Successfully",Toast.LENGTH_SHORT).show();
                    startActivity(in);
                    finish();

                }
            });

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(load.this);
                    builder.setCancelable(false);
                    builder.setMessage("Are you sure you want to Delete this record?");
                    builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.setNegativeButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.delete(holder.id_index);
                            t=db.getAll();
                            notifyDataSetChanged();
                        }
                    });
                    builder.show();

                }
            });

            holder.update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.update(new_details,holder.id_index);
                    Toast.makeText(getApplicationContext(),"Updated Successfully "+new_details.getLast_updated(),Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

            return view;
        }
    }

    public void new_save(View v){
        if(key_val==2)
        {
            db.insert(new_details);
            Toast.makeText(getApplicationContext(),"Saved Successfully "+new_details.getLast_updated(),Toast.LENGTH_SHORT).show();
            finish();
        }
        else if(key_val==3) {
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        }
        else {
            Intent i=new Intent(this.getApplicationContext(),info.class);
            startActivity(i);
            finish();
        }

    }

    public float calcProgress(int i){
        float progress_percent;
        if(t.get(i).getCurrent_matches()!=0){
            progress_percent=((float) t.get(i).getCurrent_matches()/t.get(i).getTotal_matches())*100;
        }
        else progress_percent=0;
        return  progress_percent;
    }



}
