package com.project.harrison.tournamentmod;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Harrison on 10-04-2018.
 */

public class ViewTournament extends Activity {
   EditText key;
   TextView message;
    String user_key;
    Button unlock;
    firebase_data fb;
    DBHandler db;
    boolean found;
    private FirebaseDatabase mFirebasedatabase;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_tournament);
        db=new DBHandler(this);
        message=(TextView)findViewById(R.id.key_info);
        unlock=(Button)findViewById(R.id.unlock_button);
        key=(EditText)findViewById(R.id.keycode);
        mFirebasedatabase=FirebaseDatabase.getInstance();
        mDatabaseReference=mFirebasedatabase.getReference().child("Tournament");
        found=false;
    }

    public void validate(View v)
    {
        if(isOnline())
        {
            Query q;
            user_key=key.getText().toString();
            message.setTextColor(Color.parseColor("#F9A825"));
            message.setText("CHECKING.....!");
            unlock.setEnabled(false);
            key.setEnabled(false);
            q=mDatabaseReference.orderByChild("key_code").equalTo(user_key);
            q.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        message.setTextColor(Color.parseColor("#3eff2d"));
                        message.setText("FETCHING.....!");
                        key.setEnabled(false);
                        unlock.setBackgroundResource(R.drawable.lock_selected);
                        unlock.setClickable(false);
                        //Toast.makeText(getApplicationContext(),"Fetching!",Toast.LENGTH_SHORT).show();
                        for(final DataSnapshot data: dataSnapshot.getChildren())
                        {
                            if(data.getValue()!=null)
                            {
                                String key=data.getKey();
                                mDatabaseReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.getValue()!=null){
                                            String type;
                                            fb=dataSnapshot.getValue(firebase_data.class);
                                            Gson gson=new Gson();
                                            Tournament d=gson.fromJson(fb.getData(),new TypeToken<Tournament>(){}.getType());
                                            //Toast.makeText(getApplicationContext(),"FOUND"+d.getAdmin_firebaseLocation()+d.getAdmin_key(),Toast.LENGTH_SHORT).show();
                                            Intent in=new Intent(getApplicationContext(),main_screen.class);
                                            if(fb.isOwner()){
                                                d.setOwner(true);
                                                type="ADMIN";}
                                            else
                                            {
                                                d.setOwner(false);
                                                type="AUDIENCE";
                                            }
                                            int his_flag=db.searchHistory(d.getName(),user_key);
                                            if(his_flag==-1)
                                                db.insertHistory(user_key,type,d.getName());
                                            else {
                                                db.deleteHistory(his_flag);
                                                db.insertHistory(user_key,type,d.getName());
                                            }
                                            in.putExtra("class",d);
                                            startActivity(in);
                                            finish();
                                            found=true;
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
                        //Toast.makeText(getApplicationContext(),"Invalid Pass key!",Toast.LENGTH_SHORT).show();
                        message.setTextColor(Color.parseColor("#ff2d2d"));
                        message.setText("INVALID PASS KEY !");
                        key.setText("");
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        // Vibrate for 500 milliseconds
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            v.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
                        }else{
                            //deprecated in API 26
                            v.vibrate(500);
                        }
                        Animation animation_shake= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.shake);
                        unlock.startAnimation(animation_shake);
                    }
                    unlock.setEnabled(true);
                    key.setEnabled(true);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Internet connection available!", Toast.LENGTH_LONG).show();
        }

    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            //Toast.makeText(getApplicationContext(), "No Internet connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}
