package net.fachtnaroe.generatepost_http;

import android.util.Log;
import android.view.View;

import com.google.appinventor.components.runtime.Button;
import com.google.appinventor.components.runtime.CheckBox;
import com.google.appinventor.components.runtime.Component;
import com.google.appinventor.components.runtime.EventDispatcher;
import com.google.appinventor.components.runtime.Form;
import com.google.appinventor.components.runtime.HandlesEventDispatching;
import com.google.appinventor.components.runtime.HorizontalArrangement;
import com.google.appinventor.components.runtime.Label;
import com.google.appinventor.components.runtime.Notifier;
import com.google.appinventor.components.runtime.Spinner;
import com.google.appinventor.components.runtime.TableArrangement;
import com.google.appinventor.components.runtime.TextBox;
import com.google.appinventor.components.runtime.VerticalScrollArrangement;
import com.google.appinventor.components.runtime.Web;
//import com.google.appinventor.components.runtime.util.YailList;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import static net.fachtnaroe.generatepost_http.GeneralApplicationSettings.EXTERNALLY_STORED_1;

public class MainActivity extends Form implements HandlesEventDispatching {

    private
    VerticalScrollArrangement Screen1;
    StatusBarTools statusBar;
    HorizontalArrangement SmallControls;
    TextBox txt_SSID, txt_PSK, txt_DeviceName, txt_IPv4;
    Label lbl_SSID, lbl_PSK, lbl_DeviceName, lbl_IPv4, padDivider3;
    Button configureDeviceButton, findDeviceButton, connectLocalDeviceButton, testLocalDeviceButton;
    Web sensorUnitConnection, relayServerConnection, testConnection;
    Label feedbackBox;
    Notifier notifier_Messages;
//    CheckBox chk_Active;
    Spinner spin_Active;
    TextBox txt_active;
    TextBox txt_Status, txt_Attempts;
    ArrayList myHeaders;
    TableArrangement NetworkSetup;
    String  activity = "",
            d1_IPv4="waiting",
            config_Proto="http://",
            config_Port=":80", // could be eg :8080 etc
            config_Read ="/getconfig",
            config_Write ="/setconfig";
    int programProgress = 0;
    boolean d1_ModeWrite=false;

    private static final int max_SSID = 32;
    private static final int max_PSK = 64;
    private static final int max_DeviceName = 32;
    private static final String URL_MAIN = EXTERNALLY_STORED_1;
    private static final String WIFI_PSK = "password";
    private static final String WIFI_SSID = "someSSID";
    private static final String NAME_DEFAULT_DEVICE="TCFE-CO2-98-88";
    /* Tá ná dáthanna déanta mar an gcéanna le HTML, ach le FF ar
    dtús air. Sin uimhir ó 0-FF ar cé comh tréshoilseacht an rud.
    Cur 0x ós comhair sin chun stad-riamh-fhocail i Hexadecimal a dhénamh agus sabháil */
    /* How to use HTML for the colours */
    private static final int BACKGROUND_COLOR = 0xFF477c9b;
    private static final int BUTTON_COLOR = 0xFF103449;
    private static final int HEADING_COLOR = 0xFFe0e0ff;
    private static final int SECTION_TOP_COLOR = 0xFF000000;
    private static final int SECTION_BG_COLOR = 0xFF477c9b;
    private static final int TEXTBOX_COLOR = 0xFF000000;
    private static final int TEXTBOX_BACKGROUND_COLOR = 0xFFdbdde6;
    private static final int COLOR_SUCCESS_GREEN = 0xFF569f4b;
    private static final int SIZE_LABELS_TXT = 18;
    private static final int SIZE_SMALL_LABELS_TXT = 14;
    private static final int SIZE_TOP_BAR = 50;
    private static final int FONT_NUMBER = 1;
    private static final int FONT_NUMBER_FIXED = 3;
    private static final int PAD_DIVIDER_HEIGHT = 2;

    private static final int d1_OUTOFBOX = 0;
    private static final int d1_ATTEMPTING = 2;
    private static final int d1_CONFIGURED = 4;
    private static final int d1_MINATTEMPTS = 0;
    private static final int d1_MAXATTEMPTS = 10;

    class eepromStruct {
        public char active;
        public byte config_Status;
        public byte config_Attempts;
        public char[] config_SSID = new char[max_SSID];
        public char[] config_PSK = new char[max_PSK];
        public char[] config_DeviceName = new char[max_DeviceName];
    }

    eepromStruct d1_Data;
    JSONObject d1_JSON;

    protected void $define() {
        /* this next allows the app to use the full screen. In fact,
        seemingly anything makes this work at 100% except "Fixed" and the this.Sizing
        absent in the first place.
         */
        /* Cur seo isteach. Is cuma cén focal atá ann, níl gá leis */
        this.Sizing("Responsive");
        this.BackgroundColor(BACKGROUND_COLOR);

        Form a = this;
        Integer w = a.$form().Width();
        Integer h = a.$form().Height();
        Screen1 = new VerticalScrollArrangement(this);
        // each component, listed in order
        statusBar=new StatusBarTools(Screen1);
        Label heading = new Label(Screen1);
        SmallControls = new HorizontalArrangement(Screen1);
        Label lblActive = new Label(SmallControls);
        txt_active = new TextBox(SmallControls);
        Label lblStatus = new Label(SmallControls);
        spin_Active = new Spinner((SmallControls));
        txt_Status = new TextBox(SmallControls);
        Label lblAttempts = new Label(SmallControls);
        txt_Attempts = new TextBox(SmallControls);
        Label padDivider1 = new Label(Screen1);
        HorizontalArrangement tableEnclosure=new HorizontalArrangement(Screen1);
        NetworkSetup = new TableArrangement(tableEnclosure);
        lbl_DeviceName = new Label(NetworkSetup);
        txt_DeviceName = new TextBox(NetworkSetup);
        lbl_IPv4 = new Label(NetworkSetup);
        Label padMiddle = new Label(NetworkSetup);
        padDivider3 = new Label(Screen1);
        Label padDivider4 = new Label(Screen1);
        feedbackBox = new Label(Screen1);
        findDeviceButton = new Button(Screen1);
        connectLocalDeviceButton = new Button(Screen1);
        configureDeviceButton = new Button(Screen1);
        sensorUnitConnection = new Web(Screen1);
        relayServerConnection = new Web(Screen1);
        testConnection = new Web(Screen1);
        notifier_Messages = new Notifier(Screen1);

        // still not sure how this works
        statusBar.BGTransparentColor("#00000000");
        statusBar.BackgroundColor("#00000000");
        // now, how every component looks:
        Screen1.Width(w);
        Screen1.Height(h);
        Screen1.AlignHorizontal(Component.ALIGNMENT_NORMAL);
        Screen1.AlignVertical(Component.ALIGNMENT_CENTER);
        Screen1.BackgroundColor(BACKGROUND_COLOR);
        heading.Width(w);
        heading.Height(SIZE_TOP_BAR);
        heading.Text("\b<h3><b>Update EEPROM Settings</b></h3>");
        heading.TextAlignment(Component.ALIGNMENT_CENTER);
        heading.FontSize(SIZE_LABELS_TXT + 5);
        heading.HTMLFormat(true);
        heading.TextColor(HEADING_COLOR);

        SmallControls.Visible(false);
        SmallControls.Width(w);
        SmallControls.Height(SIZE_TOP_BAR);
        SmallControls.AlignVertical(Component.ALIGNMENT_CENTER);
        SmallControls.BackgroundColor(BUTTON_COLOR);
        lblActive.Text("Is active ");
        lblActive.FontTypeface(FONT_NUMBER);
        lblActive.FontSize(SIZE_SMALL_LABELS_TXT);
        lblActive.TextColor(Component.COLOR_WHITE);
        lblActive.HeightPercent(100);
        txt_active.BackgroundColor(TEXTBOX_BACKGROUND_COLOR);
        txt_active.Height(SIZE_TOP_BAR);
        txt_active.Enabled(false);
        spin_Active.ElementsFromString("N A Y");

        lblStatus.Text("Status (0/2/4) ");
        lblStatus.FontTypeface(FONT_NUMBER);
        lblStatus.FontSize(SIZE_SMALL_LABELS_TXT);
        lblStatus.TextColor(Component.COLOR_WHITE);
        lblStatus.Height(SIZE_TOP_BAR);
        txt_Status.WidthPercent(10);
        txt_Status.BackgroundColor(TEXTBOX_BACKGROUND_COLOR);
        txt_Status.Height(SIZE_TOP_BAR);
        txt_Status.TextColor(TEXTBOX_COLOR);
        txt_Status.TextAlignment(Component.ALIGNMENT_CENTER);
        txt_Status.Text("0");
        txt_Status.NumbersOnly(true);
        txt_Status.FontSize(SIZE_LABELS_TXT);
        lblAttempts.Text("Retry attempts ");
        lblAttempts.FontTypeface(FONT_NUMBER);
        lblAttempts.FontSize(SIZE_SMALL_LABELS_TXT);
        lblAttempts.TextColor(Component.COLOR_WHITE);
        lblAttempts.HeightPercent(100);
        txt_Attempts.WidthPercent(10);
        txt_Attempts.BackgroundColor(TEXTBOX_BACKGROUND_COLOR);
        txt_Attempts.Height(SIZE_TOP_BAR);
        txt_Attempts.TextColor(TEXTBOX_COLOR);
        txt_Attempts.TextAlignment(Component.ALIGNMENT_CENTER);
        txt_Attempts.Text("0");
        txt_Attempts.NumbersOnly(true);
        txt_Attempts.FontSize(SIZE_LABELS_TXT);

        padDivider1.Height(PAD_DIVIDER_HEIGHT);
        tableEnclosure.WidthPercent(100);
        tableEnclosure.BackgroundColor(BACKGROUND_COLOR);
        NetworkSetup.Rows(2);
        NetworkSetup.Columns(3);
        NetworkSetup.Width(w);
        lbl_DeviceName.Row(0);
        lbl_DeviceName.Column(0);
        lbl_DeviceName.FontSize(SIZE_LABELS_TXT);
        lbl_DeviceName.Text("Device Name");
        lbl_DeviceName.TextAlignment(Component.ALIGNMENT_OPPOSITE);
        lbl_DeviceName.FontTypeface(FONT_NUMBER);
        lbl_DeviceName.Visible(true);
        lbl_DeviceName.BackgroundColor(BACKGROUND_COLOR);
        txt_DeviceName.FontSize(SIZE_LABELS_TXT);
        txt_DeviceName.Row(0);
        txt_DeviceName.Column(2);
        txt_DeviceName.TextAlignment(Component.ALIGNMENT_NORMAL);
        txt_DeviceName.BackgroundColor(TEXTBOX_BACKGROUND_COLOR);
        txt_DeviceName.FontTypeface(FONT_NUMBER_FIXED);
        txt_DeviceName.Text(NAME_DEFAULT_DEVICE);
        txt_DeviceName.Visible(true);
        txt_DeviceName.WidthPercent(100);
        lbl_IPv4.Row(1);
        lbl_IPv4.Column(0);
        lbl_IPv4.FontSize(SIZE_LABELS_TXT);
        lbl_IPv4.Text("IP address");
        lbl_IPv4.TextAlignment(Component.ALIGNMENT_OPPOSITE);
        lbl_IPv4.FontTypeface(FONT_NUMBER);
        lbl_IPv4.Visible(false);
        txt_IPv4 = new TextBox(NetworkSetup);
        txt_IPv4.FontSize(SIZE_LABELS_TXT);
        txt_IPv4.Row(1);
        txt_IPv4.Column(2);
        txt_IPv4.TextAlignment(Component.ALIGNMENT_NORMAL);
        txt_IPv4.BackgroundColor(TEXTBOX_BACKGROUND_COLOR);
        txt_IPv4.FontTypeface(FONT_NUMBER_FIXED);
        txt_IPv4.Visible(false);

        padMiddle.Row(0);
        padMiddle.Column(1);
        padMiddle.WidthPercent(1);
        padDivider3.Text("Activity");
        padDivider3.FontBold(false);
        padDivider3.WidthPercent(100);
        padDivider3.TextAlignment(Component.ALIGNMENT_CENTER);
        padDivider3.FontTypeface(FONT_NUMBER);
        padDivider3.FontSize(SIZE_LABELS_TXT);
        feedbackBox.Width(w);
        feedbackBox.HeightPercent(50);
        feedbackBox.HTMLFormat(true);
        feedbackBox.FontSize(SIZE_LABELS_TXT);
        feedbackBox.FontTypeface(FONT_NUMBER_FIXED);
        feedbackBox.BackgroundColor(TEXTBOX_BACKGROUND_COLOR);

        padDivider4.Height(PAD_DIVIDER_HEIGHT);
        findDeviceButton.Text("Find my device");
        findDeviceButton.FontSize(SIZE_LABELS_TXT);
        findDeviceButton.FontTypeface(FONT_NUMBER);
        findDeviceButton.WidthPercent(100);
        findDeviceButton.BackgroundColor(BUTTON_COLOR);
        findDeviceButton.TextColor(Component.COLOR_WHITE);
        findDeviceButton.Visible(true);
        findDeviceButton.HeightPercent(8);
        connectLocalDeviceButton.Text("Connect to device");
        connectLocalDeviceButton.FontSize(SIZE_LABELS_TXT);
        connectLocalDeviceButton.FontTypeface(FONT_NUMBER);
        connectLocalDeviceButton.WidthPercent(100);
        connectLocalDeviceButton.BackgroundColor(BUTTON_COLOR);
        connectLocalDeviceButton.TextColor(Component.COLOR_WHITE);
        connectLocalDeviceButton.Visible(false);
        configureDeviceButton.Text("Press to Get config");
        configureDeviceButton.FontSize(SIZE_LABELS_TXT);
        configureDeviceButton.FontTypeface(FONT_NUMBER);
        configureDeviceButton.WidthPercent(100);
        configureDeviceButton.BackgroundColor(BUTTON_COLOR);
        configureDeviceButton.TextColor(Component.COLOR_WHITE);
        configureDeviceButton.Visible(false);

        // now, the events the components can respond to
        EventDispatcher.registerEventForDelegation(this, formName, "Click");
        EventDispatcher.registerEventForDelegation(this, formName, "GotText");
        EventDispatcher.registerEventForDelegation(this, formName, "LostFocus");
        EventDispatcher.registerEventForDelegation(this, formName, "AfterSelecting");
    }

    public boolean dispatchEvent(Component component, String componentName, String eventName, Object[] params) {
        // finally, here is how the events are responded to
        dbg("dispatchEvent: " + formName + " [" +component.toString() + "] [" + componentName + "] " + eventName);
        if (eventName.equals("BackPressed")) {
            // this would be a great place to do something useful
            return true;
        }
        else if (eventName.equals("LostFocus")) {
            if (component.equals(txt_Status)) {
                int temp = Integer.valueOf(txt_Attempts.Text());
                if ((temp != d1_OUTOFBOX) && (temp != d1_ATTEMPTING) && (temp != d1_CONFIGURED)) {
                    txt_Status.Text("0");
                }
            }
            else if (component.equals(txt_Attempts)) {
                int temp = Integer.valueOf(txt_Attempts.Text());
                if ((temp < d1_MINATTEMPTS) || (temp > d1_MAXATTEMPTS)) {
                    txt_Attempts.Text("1");
                }
            }
            return true;
        }
        else if (eventName.equals("GotText")) {
            if (component.equals(testConnection)) {
                String status = params[1].toString();
                String textOfResponse = (String) params[3];
                handler_Response(component, status, textOfResponse);
                return true;
            }
            else if (component.equals(relayServerConnection)) {
                String status = params[1].toString();
                String textOfResponse = (String) params[3];
                handler_Response(component, status, textOfResponse);
                return true;
            }
            else if (component.equals(sensorUnitConnection)) {
//                    dbg(params[0].toString());
//                    dbg(params[2].toString());
                    String status = params[1].toString();
                    String textOfResponse = (String) params[3];
                    handler_Response(component, status, textOfResponse);
                    return true;
                }
        }
        else if (eventName.equals("Click")) {
            if (component.equals(connectLocalDeviceButton)) {
                activity="";
                // once "Find my device" [by name] has completed there should be an IP address available
                // Is "browse local network" an Android config requirement
                testConnection.Url( config_Proto + txt_IPv4.Text() + config_Port);
                feedbackBox.Text( messages("<b>Connection attempt to:</b> "+testConnection.Url()));
                dbg("Connection attempt to: "+testConnection.Url());
                testConnection.Get();
                dbg("A");
                padDivider3.FontBold(true);
                return true;
            }
            else if (component.equals(findDeviceButton)) {
                activity = "";
                if (!txt_DeviceName.Text().equals("")) {
                    relayServerConnection.Url(makeGetString_IPv4());
                    padDivider3.FontBold(true);
                    lbl_IPv4.Visible(true);
                    txt_IPv4.Visible(true);
                    feedbackBox.Text(messages("<b>Sending</b> " ));
                    feedbackBox.Text(messages("<b>To:</b> " + relayServerConnection.Url()));
                    relayServerConnection.Get();
                }
                return true;
            }
            else if (component.equals(configureDeviceButton)) {
                if (d1_ModeWrite) {
                    activity = "";
                    padDivider3.FontBold(true);
                    d1_Data = makeConfig();
                    if (config2JSON(d1_Data)) {
                        sensorUnitConnection.Url( config_Proto + txt_IPv4.Text() + config_Port + config_Write);
                        sensorUnitConnection.PostText(d1_JSON.toString());
                        sensorUnitConnection.RequestHeaders(myHeaders());
                        feedbackBox.Text(messages("<b>Sending:</b> " + d1_JSON));
                        feedbackBox.Text(messages("<b>To:</b> " + sensorUnitConnection.Url()));
                    }
                }
                else {
                    activity="";
                    padDivider3.FontBold(true);
                    sensorUnitConnection.Url( config_Proto + txt_IPv4.Text() + config_Port + config_Read);
                    sensorUnitConnection.Get();
                }
                return true;
            }
        }
        return false;
    }
    String makeGetString_IPv4(){
        String test1 = URL_MAIN+"?device=";
               test1+= txt_DeviceName.Text();
               test1+="&";
               test1+="sensor=IPv4";
        return test1;
    }

    eepromStruct makeConfig(){
        d1_Data=new eepromStruct();
//        if (chk_Active.Checked()) {
//            d1_Data.active = 'Y';
//        }
//        else {
//            d1_Data.active='N';
//        }
        int v1= Integer.valueOf(txt_Status.Text());
        d1_Data.config_Status= (byte) v1;
        int v2= Integer.valueOf(txt_Attempts.Text());
        d1_Data.config_Attempts= (byte) v2;
        d1_Data.config_SSID= txt_SSID.Text().toCharArray();
        d1_Data.config_PSK=txt_PSK.Text().toCharArray();
        d1_Data.config_DeviceName=txt_DeviceName.Text().toCharArray();
        return d1_Data;
    }
    boolean config2JSON(eepromStruct raw) {
        try {
            d1_JSON=new JSONObject();
            d1_JSON.put("config_Active", raw.active);
            d1_JSON.put("config_Status",raw.config_Status);
            d1_JSON.put("config_Attempts",raw.config_Attempts);
            d1_JSON.put("config_SSID",raw.config_SSID);
            d1_JSON.put("config_PSK",raw.config_PSK);
            d1_JSON.put("config_DeviceName",raw.config_DeviceName);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    void enlargeTable(){
        NetworkSetup.Rows(4);
        NetworkSetup.Columns(4);
        lbl_SSID= new Label(NetworkSetup);
        lbl_SSID.Text("SSID");
        lbl_SSID.Visible(true);
        lbl_SSID.Row(2);
        lbl_SSID.Column(0);
        lbl_SSID.TextAlignment(Component.ALIGNMENT_OPPOSITE);
        lbl_SSID.FontSize(SIZE_LABELS_TXT);
        lbl_SSID.FontTypeface(FONT_NUMBER);
        txt_SSID =new TextBox(NetworkSetup);
        txt_SSID.Visible(true);
        txt_SSID.FontSize(SIZE_LABELS_TXT);
        txt_SSID.Row(2);
        txt_SSID.Column(2);
        txt_SSID.WidthPercent(60);
        txt_SSID.TextAlignment(Component.ALIGNMENT_NORMAL);
        txt_SSID.FontTypeface(FONT_NUMBER_FIXED);
        txt_SSID.BackgroundColor(TEXTBOX_BACKGROUND_COLOR);
        txt_SSID.Text(WIFI_SSID);

        lbl_PSK = new Label (NetworkSetup);
        lbl_PSK.Row(3);
        lbl_PSK.Column(0);
        lbl_PSK.TextAlignment(Component.ALIGNMENT_OPPOSITE);
        lbl_PSK.FontSize(SIZE_LABELS_TXT);
        lbl_PSK.Text("PSK");
        lbl_PSK.FontTypeface(FONT_NUMBER);
        lbl_PSK.Visible(true);
        txt_PSK =new TextBox(NetworkSetup);
        txt_PSK.FontSize(SIZE_LABELS_TXT);
        txt_PSK.Row(3);
        txt_PSK.Column(2);
        txt_PSK.TextAlignment(Component.ALIGNMENT_NORMAL);
        txt_PSK.BackgroundColor(TEXTBOX_BACKGROUND_COLOR);
        txt_PSK.FontTypeface(FONT_NUMBER_FIXED);
        txt_PSK.Text(WIFI_PSK);
        txt_PSK.Visible(true);
        SmallControls.Visible(true);
    }

    String messages(String addition) {
        activity += addition;
        return activity;
    }
    public com.google.appinventor.components.runtime.util.YailList myHeaders() {
        com.google.appinventor.components.runtime.util.YailList list = new com.google.appinventor.components.runtime.util.YailList();
        List<com.google.appinventor.components.runtime.util.YailList> arrlist = new ArrayList<>();
        com.google.appinventor.components.runtime.util.YailList FTS=new com.google.appinventor.components.runtime.util.YailList();
        return com.google.appinventor.components.runtime.util.YailList.makeList(arrlist);
    }

    void handler_Response(Component c, String status, String textOfResponse){
        padDivider3.FontBold(false);
        feedbackBox.Text(messages("<br><b>Received:</b> " + textOfResponse+"<br>"));
        if (status.equals("200") ) try {
            JSONObject parser = new JSONObject(textOfResponse);
            if (parser.getString("Status").equals("OK")) {
                if (c.equals(relayServerConnection)) {
                    if (parser.getString("sensor").equals("IPv4")) {
                        if (!parser.getString("value").equals("")) {
                            d1_IPv4 = parser.getString("value");
                            txt_IPv4.Text(d1_IPv4);
                            connectLocalDeviceButton.Visible(true);
                        }
                    }
                }
                else  if (c.equals(testConnection)) {
                    if ((parser.getString("IPv4").length() >= 7)){
                        /* if the Sensor Unit replies with its IP address (plus other data,
                            then we're connected, agus ag tarraingt díosal. */
                        // Cén seans ann go mbeadh device eile ar an líonra?
                        boolean a=(d1_IPv4.compareTo( parser.getString("IPv4") ) == 0);
                        if (a) {
                            // Good to go to configuration of settings now.
                            dbg("Successful connection to unit.");
                            activity="";
                            feedbackBox.HeightPercent(30);
                            feedbackBox.Text(messages("<b>Successful connection to unit.</b>"));
                            txt_IPv4.TextColor(COLOR_SUCCESS_GREEN);
                            txt_IPv4.FontBold(true);
                            enlargeTable();
                            configureDeviceButton.Visible(true);
                        }
                        else {
                            // things should get _this_ bad.
                            dbg(d1_IPv4);
                            dbg( parser.getString("IPv4") );
                            dbg( Integer.valueOf(d1_IPv4.compareTo( parser.getString("IPv4"))).toString());
                        }
                    }
                }
                else  if (c.equals(sensorUnitConnection)) {
                    // on the off-chance there's another on the network, or data error
                    boolean a=(txt_DeviceName.Text().compareTo( parser.getString("config_DeviceName") ) == 0);
                    if (a) {
                        dbg("Successful read from unit.");
                        activity="";
                        feedbackBox.Text(messages("<b>Successful read from unit.</b>"));
                        txt_IPv4.TextColor(Component.COLOR_BLACK);
                        txt_DeviceName.TextColor(COLOR_SUCCESS_GREEN);
                        txt_IPv4.FontBold(false);
                        txt_DeviceName.FontBold(true);
//                        txt_SSID.Text(parser.getString("device_SSID"));
//                        parser.
//                        txt_PSK.Text(parser.getString("device_PSK"));
                        if (isNumeric(parser.getString("config_Status"))){
                            txt_Status.Text(parser.getString("config_Status"));
                        }
                        if (isNumeric(parser.getString("config_Attempts"))){
                            txt_Attempts.Text(parser.getString("config_Attempts"));
                        }
//                        if (parser.getString("active").equals("Y")){
//                            chk_Active.Checked(true);
//                        }
//                        else {
//                            chk_Active.Checked(false);
//                        }
                        // if we're not in write mode, offer that
                        if (!d1_ModeWrite) {
                            configureDeviceButton.Text("Update EEPROM");
                        }
                    }
                    else {
                        dbg(parser.getString("config_DeviceName"));
                        dbg( txt_DeviceName.Text() );

                    }
                }
            }
        }
        catch (JSONException e) {
            notifier_Messages.ShowAlert("JSON Error 422");
            dbg("android JSON exception (" + textOfResponse + ")");
            feedbackBox.Text (messages("android JSON exception (" + textOfResponse + ")"));
        }
        else {
            feedbackBox.Text( messages( "Error status code is "+status) );
            dbg("Status is "+status);
        }
    }
    public static void dbg (String debugMsg) {
        System.err.print( "~~~> " + debugMsg + " <~~~\n");
    }
    public static boolean isNumeric(String string) {
        int intValue;
//        System.out.println(String.format("Parsing string: \"%s\"", string));
        if(string == null || string.equals("")) {
            System.out.println("String cannot be parsed, it is null or empty.");
            return false;
        }
        try {
            intValue = Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("Input String cannot be parsed to Integer.");
        }
        return false;
    }
}