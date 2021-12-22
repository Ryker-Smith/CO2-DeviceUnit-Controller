package net.fachtnaroe.generatepost_http;

import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.TinyDB;

import static net.fachtnaroe.generatepost_http.GeneralApplicationSettings.EXTERNALLY_STORED_1;

public class ProgramSettings {

    public static final String URL_MAIN = EXTERNALLY_STORED_1;
    // the buildNumber can be generated automatically. Look in build.gradle to see how
    public final String buildNumber=Integer.toString(BuildConfig.VERSION_CODE);
    //    public final String versionName=BuildConfig.VERSION_NAME;
    public String DEVICE_NAME="";
    public String localIPv4="";
    public Boolean showStartingMessage=true;
    TinyDB localDB;
    // not saved into local DB:
    public String WIFI_PSK = "";
    public String WIFI_SSID = "";
    // these strings are defined as constants, to avoid them being provided twice as literals in get/set
    private static final String str_DEVICE_NAME="DEVICE_NAME";
    private static final String str_localIPv4="localIPv4";
    // providing a NAME_DEFAULT_DEVICE saves on testing/debugging time
//    public static final String default_DEVICE_NAME ="TCFE-CO2-98-88";

    public ProgramSettings(ComponentContainer screenName){
        localDB= new TinyDB(screenName);
    }

    public boolean get () {
        try {
            DEVICE_NAME = (String) localDB.GetValue(str_DEVICE_NAME, DEVICE_NAME);
            localIPv4 = (String) localDB.GetValue(str_localIPv4, localIPv4);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public boolean set () {
        try {
            localDB.StoreValue(str_DEVICE_NAME, DEVICE_NAME);
            localDB.StoreValue(str_localIPv4, localIPv4);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
    String makeGetString(String sensor){
        String test1 = this.URL_MAIN+"?device=";
        test1+= DEVICE_NAME;
        test1+="&";
        test1+="sensor="+sensor;
        return test1;
    }
}

