package com.project.harrison.tournamentmod;

import java.io.Serializable;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;
import static android.R.attr.theme;

/**
 * Created by Harrison on 09-04-2018.
 */

public class firebase_data implements Serializable {

    private String key_code;
    private String data,user_id;
    private boolean owner;


    public  firebase_data() {}

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public firebase_data(String key_code, String data,Boolean owner,String uid)
    {
        this.key_code=key_code;
        this.data=data;
        this.owner=owner;
        this.user_id=uid;


    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public String getKey_code() {
        return key_code;
    }

    public void setKey_code(String key_code) {
        this.key_code = key_code;
    }
}
