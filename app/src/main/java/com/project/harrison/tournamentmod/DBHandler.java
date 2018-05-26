package com.project.harrison.tournamentmod;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static android.R.attr.data;

/**
 * Created by Harrison on 01-04-2018.
 */

public class DBHandler extends SQLiteOpenHelper {

    private final static String DB_NAME="TournamentsDB";
    private final static int DB_V=12;
    private final static String T_NAME="Tournament";
    private final static String T_NAME_HISTORY="History";
    private final static String id="id";
    private final static String name="class";
    private final static String name_tour="name_tour";
    private final static String type="type";
    private final static String date="date";
    private final static String key="key";

    public DBHandler(Context c){super(c,DB_NAME,null,DB_V);}

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String create="create table "+T_NAME+"("+id+" integer primary key autoincrement not null,"+name+" blob)";
       // String create2="create table "+T_NAME_ONLINE+"("+id+" integer primary key autoincrement not null,"+name_tour+" text,"+admin_key+" text,"+audience_key+" text)";
        String create3="create table "+T_NAME_HISTORY+"("+id+" integer primary key autoincrement not null,"+name_tour+" text,"+type+" text,"+key+" text,"+date+" text)";
        sqLiteDatabase.execSQL(create);
        //sqLiteDatabase.execSQL(create2);
        sqLiteDatabase.execSQL(create3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists "+T_NAME);
      //  sqLiteDatabase.execSQL("drop table if exists "+T_NAME_ONLINE);
        sqLiteDatabase.execSQL("drop table if exists "+T_NAME_HISTORY);
        onCreate(sqLiteDatabase);

    }

    public void insert(Tournament d)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Gson gson=new Gson();
        ContentValues values=new ContentValues();
        values.put(name,gson.toJson(d).getBytes());
        db.insert(T_NAME,null,values);
        db.close();
    }

    public void insertHistory(String key1,String t_type,String name)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(name_tour,name);
        values.put(type,t_type);
        values.put(key,key1);
        values.put(date,new SimpleDateFormat("dd-MM-yy @ HH:mm").format(Calendar.getInstance().getTime()));
        db.insert(T_NAME_HISTORY,null,values);
        db.close();
    }



//    public void insertOnline(String admin,String audience,String name)
//    {
//        SQLiteDatabase db=this.getWritableDatabase();
//        ContentValues values=new ContentValues();
//        values.put(name_tour,name);
//        values.put(admin_key,admin);
//        values.put(audience_key,audience);
//        db.insert(T_NAME_ONLINE,null,values);
//        db.close();
//    }

//    public List<hosted_tournaments> getAllOnline()
//    {
//        List<hosted_tournaments> data=new ArrayList<>();
//        SQLiteDatabase db=this.getWritableDatabase();
//        String s="Select * from "+T_NAME_ONLINE;
//        Cursor c=db.rawQuery(s,null);
//        if(c.moveToFirst())
//        {
//            do{
//                data.add(new hosted_tournaments(c.getString(0),c.getString(1),c.getString(2),c.getString(3)));
//            }while (c.moveToNext());
//        }
//        c.close();
//        db.close();
//        return data;
//    }

    public List<history> getAllHistory()
    {
        List<history> data=new ArrayList<>();
        SQLiteDatabase db=this.getWritableDatabase();
        String s="Select * from "+T_NAME_HISTORY;
        Cursor c=db.rawQuery(s,null);
        if(c.moveToFirst())
        {
            do{
                data.add(new history(c.getString(0),c.getString(1),c.getString(2),c.getString(3),c.getString(4)));
            }while (c.moveToNext());
        }
        c.close();
        db.close();
        Collections.reverse(data);
        return data;
    }

    public int searchHistory(String name,String key)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        String s="Select * from "+T_NAME_HISTORY;
        Cursor c=db.rawQuery(s,null);
        if(c.moveToFirst())
        {
            do{
                if(c.getString(1).equalsIgnoreCase(name) && c.getString(3).equalsIgnoreCase(key))
                    return Integer.parseInt(c.getString(0));
            }while (c.moveToNext());

        }
        c.close();
        db.close();
        return -1;
    }


    public void delete(int i)
    {
        SQLiteDatabase db=getWritableDatabase();
        db.delete(T_NAME,id+" =? ", new String[]{String.valueOf(i)});
        db.close();
    }

    public void deleteHistory(int i)
    {
        SQLiteDatabase db=getWritableDatabase();
        db.delete(T_NAME_HISTORY,id+" =? ", new String[]{String.valueOf(i)});
        db.close();
    }



//    public void deleteOnline(int i)
//    {
//        SQLiteDatabase db=getWritableDatabase();
//        db.delete(T_NAME_ONLINE,id+" =? ", new String[]{String.valueOf(i)});
//        db.close();
//    }


    public void update(Tournament d,int i)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Gson gson=new Gson();
        ContentValues values=new ContentValues();
        values.put(name,gson.toJson(d).getBytes());
        db.update(T_NAME,values,id+" =? ", new String[]{String.valueOf(i)});
        db.close();
    }




    public List<Tournament> getAll(){
        List<Tournament> bundle=new ArrayList<>();
        SQLiteDatabase db=this.getWritableDatabase();
        String s="Select * from "+T_NAME;
        Cursor c=db.rawQuery(s,null);
        if(c.moveToFirst())
        {
            do{
                // data d=new data();
                // d.setId(Integer.parseInt(c.getString(0)));
                byte[] blob=c.getBlob(c.getColumnIndex(name));
                String json=new String(blob);
                Gson gson=new Gson();
                Tournament d=gson.fromJson(json,new TypeToken<Tournament>(){}.getType());
                d.setId(Integer.parseInt(c.getString(0)));
                bundle.add(d);
            }while (c.moveToNext());
        }
        c.close();
        db.close();
        return bundle;
    }




}
