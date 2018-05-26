package com.project.harrison.tournamentmod;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Blob;
import java.util.Arrays;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 1;
    private FirebaseAuth mFireBaseAuth;
    private FirebaseAuth.AuthStateListener mFireBaseAuthListener;
    TextView user_name;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user_name=(TextView)findViewById(R.id.user_name_signin);
        mFireBaseAuth=FirebaseAuth.getInstance();
        mFireBaseAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user!=null)
                {

                    user_name.setText("USER : "+user.getDisplayName());
                    user_name.setBackgroundColor(Color.parseColor("#83d94e"));
                }
                else
                {
                    user_name.setBackgroundColor(Color.parseColor("#ff2f28"));
                    user_name.setText("NOT SIGNED IN ! ");

                    if(isOnline())
                    {
                        Toast.makeText(getApplicationContext(),"Please Sign in to Continue !",Toast.LENGTH_SHORT).show();
                        startActivityForResult(
                                AuthUI.getInstance()
                                        .createSignInIntentBuilder()
                                        .setIsSmartLockEnabled(false)
                                        .setAvailableProviders(Arrays.asList(
                                                new AuthUI.IdpConfig.GoogleBuilder().build()))
                                        .build(),
                                RC_SIGN_IN);
                    }
                    else {
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                        alertDialog.setTitle("Alert");
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.setCancelable(false);
                        alertDialog.setMessage("Enable Internet Connection to Sign in !");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });
                        alertDialog.show();
                    }


                }

            }
        };

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN)
            if(resultCode==RESULT_CANCELED)
            {
                finish();
            }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFireBaseAuth.removeAuthStateListener(mFireBaseAuthListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFireBaseAuth.addAuthStateListener(mFireBaseAuthListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.sign_out:
                AuthUI.getInstance().signOut(this);
                user_name.setBackgroundColor(Color.parseColor("#ff2f28"));
                user_name.setText("NOT SIGNED IN ! ");

                return true;
            default:return super.onOptionsItemSelected(item);
        }
    }

    public void action(View v)
    {
        Button b=(Button)v;
        Intent i;
        String action=b.getText().toString();
        if(action.equalsIgnoreCase("new"))
        {
            i=new Intent(this.getApplicationContext(),info.class);
            startActivity(i);
        }
        else if(action.equalsIgnoreCase("load offline"))
        {
            i=new Intent(this.getApplicationContext(),load.class);
            i.putExtra("key",1);
            startActivity(i);
        }
        else if(action.equalsIgnoreCase("view"))
        {
            i=new Intent(this.getApplicationContext(),ViewTournament.class);
            startActivity(i);
        }
        else if(action.equalsIgnoreCase("load online"))
        {
            i=new Intent(this.getApplicationContext(),load.class);
            i.putExtra("key",3);
            startActivity(i);
        }
        else if(action.equalsIgnoreCase("history"))
        {
            i=new Intent(this.getApplicationContext(),load.class);
            i.putExtra("key",4);
            startActivity(i);
        }
        else
        {
            i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"elninoApps@gmail.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "Tournament Manager - Feedback");
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
