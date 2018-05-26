package com.project.harrison.tournamentmod;

/**
 * Created by Harrison on 16-04-2018.
 */

public class history {
    private String id,name,key,type,date;
    public history(String s1,String s2,String s3,String s4,String s5)
    {
        this.id=s1;
        this.name=s2;
        this.type=s3;
        this.key=s4;
        this.date=s5;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
