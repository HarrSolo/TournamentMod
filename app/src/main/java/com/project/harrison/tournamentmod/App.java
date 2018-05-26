package com.project.harrison.tournamentmod;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import android.app.Application;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Black.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

    }
}
