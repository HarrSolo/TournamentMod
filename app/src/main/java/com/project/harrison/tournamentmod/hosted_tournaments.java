package com.project.harrison.tournamentmod;

/**
 * Created by Harrison on 10-04-2018.
 */

public class hosted_tournaments {
    private String admin_key,name,audience_key;

    hosted_tournaments(String s1,String s2,String s3)
    {
        this.name=s1;
        this.admin_key=s2;
        this.audience_key=s3;
    }


    hosted_tournaments(){}

    public String getName() {
        return name;
    }

    public String getAudience_key() {
        return audience_key;
    }

    public void setAudience_key(String audience_key) {
        this.audience_key = audience_key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdmin_key() {
        return admin_key;
    }

    public void setAdmin_key(String admin_key) {
        this.admin_key = admin_key;
    }
}
