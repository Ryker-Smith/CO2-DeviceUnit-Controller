package net.fachtnaroe.generatepost_http;

import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.TinyDB;

public class ProgramSettings {

    // the buildNumber can be generated automatically. Look in build.gradle to see how
    public final String buildNumber=Integer.toString(BuildConfig.VERSION_CODE);
//    public final String versionName=BuildConfig.VERSION_NAME;
    public String lastMAC;
    public String lastIPv4;
    TinyDB localDB;

    public ProgramSettings(ComponentContainer screenName){
        localDB= new TinyDB(screenName);
    }

    public boolean get () {
        // the boolean return is for future extension
        lastMAC=(String) localDB.GetValue("lastMAC",lastMAC);
        lastIPv4=(String) localDB.GetValue("lastIPv4",lastIPv4);
        return true;
    }

    public boolean set () {
        localDB.StoreValue("lastMAC", lastMAC);
        localDB.StoreValue("lastIPv4", lastIPv4);
        return true;
    }
}

