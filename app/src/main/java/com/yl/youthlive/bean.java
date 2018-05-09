package com.yl.youthlive;

import android.app.Application;


/**
 * Created by TBX on 11/8/2017.
 */

public class bean extends Application{

    public String BASE_URL = "http://nationproducts.in/";

    public String userId = "";
    String userName = "";
    String userImage = "";

    String liveId = "";


    protected String userAgent;

    @Override
    public void onCreate() {
        super.onCreate();

    }





    public boolean useExtensionRenderers() {
        return BuildConfig.FLAVOR.equals("withExtensions");
    }

}
