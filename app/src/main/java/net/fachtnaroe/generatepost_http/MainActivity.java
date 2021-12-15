package net.fachtnaroe.generatepost_http;

import com.google.appinventor.components.runtime.Button;
import com.google.appinventor.components.runtime.Clock;
import com.google.appinventor.components.runtime.Component;
import com.google.appinventor.components.runtime.EventDispatcher;
import com.google.appinventor.components.runtime.Form;
import com.google.appinventor.components.runtime.HandlesEventDispatching;
import com.google.appinventor.components.runtime.HorizontalArrangement;
import com.google.appinventor.components.runtime.Label;
import com.google.appinventor.components.runtime.Notifier;
import com.google.appinventor.components.runtime.PasswordTextBox;
import com.google.appinventor.components.runtime.Spinner;
import com.google.appinventor.components.runtime.TableArrangement;
import com.google.appinventor.components.runtime.TextBox;
import com.google.appinventor.components.runtime.VerticalScrollArrangement;
import com.google.appinventor.components.runtime.Web;
//import com.google.appinventor.components.runtime.util.YailList;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.fachtnaroe.generatepost_http.GeneralApplicationSettings.EXTERNALLY_STORED_1;

public class MainActivity extends Form implements HandlesEventDispatching {

    private
    VerticalScrollArrangement Screen1;
    StatusBarTools statusBar;
    HorizontalArrangement fiddlyTopBits;
    TextBox txt_SSID, txt_DeviceName, txt_IPv4, txt_active;
    PasswordTextBox txt_PSK;
    Label lbl_SSID, lbl_PSK, lbl_DeviceName, lbl_IPv4, padDivider3;
    Button configureDeviceButton, findDeviceButton, connectLocalDeviceButton, testLocalDeviceButton;
    Web sensorUnitConnection, relayServerConnection, testConnection;
    Label feedbackBox;
    Notifier notifier_Messages;
    Spinner spin_Active;
    TextBox txt_Status, txt_Attempts;
    TableArrangement NetworkSetup;
    String  activity = "",
            d1_IPv4="waiting",
            config_Proto="http://",
            config_Port=":80", // could be eg :8080 etc
            config_Read ="/getconfig",
            config_Write ="/setconfig",
            statusValues="Choose,No,Attempting,Yes",
            spinnerValue="";
    boolean d1_ModeWrite=false;

    private static final int max_SSID = 32;
    private static final int max_PSK = 64;
    private static final int max_DeviceName = 32;
    private static final String URL_MAIN = EXTERNALLY_STORED_1;
    private static final String default_WIFI_PSK = "password";
    private static final String default_WIFI_SSID = "someSSID";
    // providing a NAME_DEFAULT_DEVICE saves on testing/debugging time
    private static final String default_DEVICE_NAME ="TCFE-CO2-98-88";
    // UI strings for localisation
    private static final String ui_txt_STATUS_POSSIBLES = "Status\n(0/4/8) ";
    private static final String ui_txt_MAIN_HEAD="Update EEPROM Settings";
    private static final String ui_txt_STATUS_ATTEMPTS_COUNT="Retry\nattempts ";
    private static final String ui_txt_DEVICE_NAME="Device Name";
    private static final String ui_txt_IP_ADDRESS="IP address";
    private static final String ui_txt_ACTIVITY="Activity";
    private static final String ui_txt_FIND_DEVICE="Find my device";
    private static final String ui_txt_CONNECT_DEVICE="Connect to device";
    private static final String ui_txt_READ_DEVICE="Press to Get config";
    private static final String ui_txt_WRITE_DEVICE="Update EEPROM";
    private static final String ui_txt_CONNECTION_ATTEMPT="Connection attempt to";
    private static final String ui_txt_CONNECTION_SENDING="Sending";
    private static final String ui_txt_CONNECTION_RECEIVED="Received";
    private static final String ui_txt_CONNECTION_SUCCESS="Successfully connected to unit";
    private static final String ui_txt_READ_SUCCESS="Successful read from Sensor Unit";
    private static final String ui_txt_WRITE_SUCCESS="Successful write to Sensor Unit";
    private static final String ui_txt_WRITE_SUCCESS_ADVISORY="Your changes will take effect when you restart the Sensor Unit";
    private static final String ui_txt_ERR_NOT_IMPLEMENTED="Not Implemented";
    private static final String ui_txt_ERR_422="JSON Error 422";
    private static final String ui_TXT_ERROR_PREFIX="Error status code is ";
    private static final String ui_txt_MESSAGE_HEADING_ERROR="Problem";
    private static final String ui_txt_BUTTON_OK="OK";
    private static final String ui_txt_WIFI_PSK = "PSK";
    private static final String ui_txt_WIFI_SSID = "SSID";
    private static final String ui_txt_WIFI_IPv4 = "IPv4";
    private static final String ui_txt_URL_TIMED_OUT="Timed-out connecting to ";
    /* Tá ná dáthanna déanta mar an gcéanna le HTML, ach le FF ar
    dtús air. Sin uimhir ó 0-FF ar cé comh tréshoilseacht an rud.
    Cur 0x ós comhair sin chun stad-riamh-fhocail i Hexadecimal a dhénamh agus sabháil */
    /* How to use HTML for the colours */
    private static final int color_MAIN_BACKGROUND = 0xFF477c9b;
    private static final int color_BUTTON_BACKGROUND = 0xFF103449;
    private static final int color_HEADING_TEXT = 0xFFe0e0ff;
    private static final int SECTION_TOP_COLOR = 0xFF000000;
    private static final int SECTION_BG_COLOR = 0xFF477c9b;
    private static final int color_TEXTBOX_TEXT = 0xFF000000;
    private static final int color_TEXTBOX_BACKGROUND = 0xFFdbdde6;
    private static final int color_SUCCESS_GREEN = 0xFF569f4b;
    private static final int size_FONT_LABELS_TEXT = 18;
    private static final int size_FONT_LABELS_TEXT_SMALL = 14;
    private static final int size_FIDDLY_BAR_TOP = 50;
    private static final int font_NUMBER_DEFAULT = 1;
    private static final int font_NUMBER_FIXED = 3;
    private static final int size_PADDING_HEIGHT = 2;

    private static final int d1_OUTOFBOX = 0;
    private static final int d1_ATTEMPTING = 2;
    private static final int d1_CONFIGURED = 8;
    private static final int d1_MINATTEMPTS = 0;
    private static final int d1_MAXATTEMPTS = 10;

    private static final int seconds=1000;
    private static final int minutes=60000;

    class eepromStruct {
        public char active;
        public byte config_Status;
        public byte config_Attempts;
        public char[] config_SSID = new char[max_SSID];
        public char[] config_PSK = new char[max_PSK];
        public char[] config_DeviceName = new char[max_DeviceName];
    }

    eepromStruct d1_Data=new eepromStruct();
    JSONObject d1_JSON=new JSONObject();
    Clock ticker=new Clock(this);

    protected void $define() {
        /* this next allows the app to use the full screen. In fact,
        seemingly anything makes this work at 100% except "Fixed" and the this.Sizing
        absent in the first place.
         */
        /* Cur seo isteach. Is cuma cén focal atá ann, níl gá leis */
        this.Sizing("Responsive");
        this.BackgroundColor(color_MAIN_BACKGROUND);

        Form a = this;
        Integer w = a.$form().Width();
        Integer h = a.$form().Height();
        Label spaceAtTheTop=new Label((this));
        spaceAtTheTop.Height(20);
        Label heading = new Label(this);
        Screen1 = new VerticalScrollArrangement(this);
        // each component, listed in order
        statusBar=new StatusBarTools(Screen1);

        fiddlyTopBits = new HorizontalArrangement(Screen1);
        Label lblActive = new Label(fiddlyTopBits);
        HorizontalArrangement spinnerGroup=new HorizontalArrangement(fiddlyTopBits);
        txt_active=new TextBox(spinnerGroup);
        spin_Active = new Spinner(spinnerGroup);
        Label lblStatus = new Label(fiddlyTopBits);
        txt_Status = new TextBox(fiddlyTopBits);
        Label lblAttempts = new Label(fiddlyTopBits);
        txt_Attempts = new TextBox(fiddlyTopBits);
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
        Screen1.BackgroundColor(color_MAIN_BACKGROUND);
        heading.Width(w);
        heading.Height(size_FIDDLY_BAR_TOP);

        heading.Text("\n<h3><b>" + ui_txt_MAIN_HEAD + "</b></h3>");
        heading.TextAlignment(Component.ALIGNMENT_CENTER);
        heading.FontSize(size_FONT_LABELS_TEXT + 5);
        heading.HTMLFormat(true);
        heading.TextColor(color_HEADING_TEXT);

        fiddlyTopBits.Visible(false);
        fiddlyTopBits.WidthPercent(100);
        fiddlyTopBits.Height(size_FIDDLY_BAR_TOP);
        fiddlyTopBits.AlignVertical(Component.ALIGNMENT_CENTER);
        fiddlyTopBits.BackgroundColor(color_BUTTON_BACKGROUND);

        lblActive.Text("Is\nActive ");
        lblActive.FontTypeface(font_NUMBER_DEFAULT);
        lblActive.FontSize(size_FONT_LABELS_TEXT_SMALL);
        lblActive.TextColor(Component.COLOR_WHITE);
        lblActive.HeightPercent(100);
        spinnerGroup.BackgroundColor(color_TEXTBOX_BACKGROUND);
        spinnerGroup.Height(size_FIDDLY_BAR_TOP);
        spinnerGroup.WidthPercent(30);
        txt_active.WidthPercent(5);
        txt_active.Enabled(false);
        txt_active.Height(size_FIDDLY_BAR_TOP);
        txt_active.BackgroundColor(color_TEXTBOX_BACKGROUND);
        txt_active.TextColor(color_TEXTBOX_TEXT);
        txt_active.TextAlignment(Component.ALIGNMENT_CENTER);
        txt_Status.Text("?");
        txt_Status.FontSize(size_FONT_LABELS_TEXT);
        spin_Active.ElementsFromString(statusValues);
        spin_Active.Height(size_FIDDLY_BAR_TOP);
        spin_Active.WidthPercent(100);

        lblStatus.Text(ui_txt_STATUS_POSSIBLES);
        lblStatus.FontTypeface(font_NUMBER_DEFAULT);
        lblStatus.FontSize(size_FONT_LABELS_TEXT_SMALL);
        lblStatus.TextColor(Component.COLOR_WHITE);
        lblStatus.Height(size_FIDDLY_BAR_TOP);
        txt_Status.WidthPercent(10);
        txt_Status.BackgroundColor(color_TEXTBOX_BACKGROUND);
        txt_Status.Height(size_FIDDLY_BAR_TOP);
        txt_Status.TextColor(color_TEXTBOX_TEXT);
        txt_Status.TextAlignment(Component.ALIGNMENT_CENTER);
        txt_Status.Text("0");
        txt_Status.NumbersOnly(true);
        txt_Status.FontSize(size_FONT_LABELS_TEXT);

        lblAttempts.Text(ui_txt_STATUS_ATTEMPTS_COUNT);
        lblAttempts.FontTypeface(font_NUMBER_DEFAULT);
        lblAttempts.FontSize(size_FONT_LABELS_TEXT_SMALL);
        lblAttempts.TextColor(Component.COLOR_WHITE);
        lblAttempts.HeightPercent(100);
        txt_Attempts.WidthPercent(10);
        txt_Attempts.BackgroundColor(color_TEXTBOX_BACKGROUND);
        txt_Attempts.Height(size_FIDDLY_BAR_TOP);
        txt_Attempts.TextColor(color_TEXTBOX_TEXT);
        txt_Attempts.TextAlignment(Component.ALIGNMENT_CENTER);
        txt_Attempts.Text("0");
        txt_Attempts.NumbersOnly(true);
        txt_Attempts.FontSize(size_FONT_LABELS_TEXT);
        // END of fiddlyTopBits

        padDivider1.Height(size_PADDING_HEIGHT);
        tableEnclosure.WidthPercent(100);
        tableEnclosure.BackgroundColor(color_MAIN_BACKGROUND);
        NetworkSetup.Rows(2);
        NetworkSetup.Columns(3);
        NetworkSetup.Width(w);
        lbl_DeviceName.Row(0);
        lbl_DeviceName.Column(0);
        lbl_DeviceName.FontSize(size_FONT_LABELS_TEXT);
        lbl_DeviceName.Text(ui_txt_DEVICE_NAME);
        lbl_DeviceName.TextAlignment(Component.ALIGNMENT_OPPOSITE);
        lbl_DeviceName.FontTypeface(font_NUMBER_DEFAULT);
        lbl_DeviceName.TextColor(Component.COLOR_WHITE);
        lbl_DeviceName.Visible(true);
        lbl_DeviceName.BackgroundColor(color_MAIN_BACKGROUND);
        txt_DeviceName.FontSize(size_FONT_LABELS_TEXT);
        txt_DeviceName.Row(0);
        txt_DeviceName.Column(2);
        txt_DeviceName.TextAlignment(Component.ALIGNMENT_NORMAL);
        txt_DeviceName.BackgroundColor(color_TEXTBOX_BACKGROUND);
        txt_DeviceName.FontTypeface(font_NUMBER_FIXED);
        txt_DeviceName.Text(default_DEVICE_NAME);
        txt_DeviceName.Visible(true);
        txt_DeviceName.WidthPercent(100);
        lbl_IPv4.Row(1);
        lbl_IPv4.Column(0);
        lbl_IPv4.FontSize(size_FONT_LABELS_TEXT);
        lbl_IPv4.Text(ui_txt_WIFI_IPv4);
        lbl_IPv4.TextAlignment(Component.ALIGNMENT_OPPOSITE);
        lbl_IPv4.FontTypeface(font_NUMBER_DEFAULT);
        lbl_IPv4.TextColor(Component.COLOR_WHITE);
        lbl_IPv4.Visible(false);
        txt_IPv4 = new TextBox(NetworkSetup);
        txt_IPv4.FontSize(size_FONT_LABELS_TEXT);
        txt_IPv4.Row(1);
        txt_IPv4.Column(2);
        txt_IPv4.TextAlignment(Component.ALIGNMENT_NORMAL);
        txt_IPv4.BackgroundColor(color_TEXTBOX_BACKGROUND);
        txt_IPv4.FontTypeface(font_NUMBER_FIXED);
        txt_IPv4.Visible(false);

        padMiddle.Row(0);
        padMiddle.Column(1);
        padMiddle.WidthPercent(1);

        padDivider3.Text(ui_txt_ACTIVITY);
        padDivider3.FontBold(false);
        padDivider3.WidthPercent(100);
        padDivider3.TextAlignment(Component.ALIGNMENT_CENTER);
        padDivider3.FontTypeface(font_NUMBER_DEFAULT);
        padDivider3.FontSize(size_FONT_LABELS_TEXT);
        feedbackBox.Width(w);
        feedbackBox.HeightPercent(30);
        feedbackBox.HTMLFormat(true);
        feedbackBox.FontSize(size_FONT_LABELS_TEXT);
        feedbackBox.FontTypeface(font_NUMBER_FIXED);
        feedbackBox.BackgroundColor(color_TEXTBOX_BACKGROUND);

        padDivider4.Height(size_PADDING_HEIGHT);
        findDeviceButton.Text(ui_txt_FIND_DEVICE);
        findDeviceButton.FontSize(size_FONT_LABELS_TEXT);
        findDeviceButton.FontTypeface(font_NUMBER_DEFAULT);
        findDeviceButton.WidthPercent(100);
        findDeviceButton.BackgroundColor(color_BUTTON_BACKGROUND);
        findDeviceButton.TextColor(Component.COLOR_WHITE);
        findDeviceButton.Visible(true);
        findDeviceButton.HeightPercent(8);
        connectLocalDeviceButton.Text(ui_txt_CONNECT_DEVICE);
        connectLocalDeviceButton.FontSize(size_FONT_LABELS_TEXT);
        connectLocalDeviceButton.FontTypeface(font_NUMBER_DEFAULT);
        connectLocalDeviceButton.WidthPercent(100);
        connectLocalDeviceButton.BackgroundColor(color_BUTTON_BACKGROUND);
        connectLocalDeviceButton.TextColor(Component.COLOR_WHITE);
        connectLocalDeviceButton.Visible(false);
        configureDeviceButton.Text(ui_txt_READ_DEVICE);
        configureDeviceButton.FontSize(size_FONT_LABELS_TEXT);
        configureDeviceButton.FontTypeface(font_NUMBER_DEFAULT);
        configureDeviceButton.WidthPercent(100);
        configureDeviceButton.BackgroundColor(color_BUTTON_BACKGROUND);
        configureDeviceButton.TextColor(Component.COLOR_WHITE);
        configureDeviceButton.Visible(false);

        notifier_Messages.BackgroundColor(Component.COLOR_WHITE);
        notifier_Messages.TextColor(color_BUTTON_BACKGROUND);

        ticker.TimerInterval(60 * seconds);
        ticker.TimerEnabled(true);
        ticker.TimerAlwaysFires(false); // not required when app is minimized

        // now, the events the components can respond to
        EventDispatcher.registerEventForDelegation(this, formName, "Click");
        EventDispatcher.registerEventForDelegation(this, formName, "GotText");
        EventDispatcher.registerEventForDelegation(this, formName, "LostFocus");
        EventDispatcher.registerEventForDelegation(this, formName, "AfterSelecting");
        EventDispatcher.registerEventForDelegation(this, formName, "TimedOut"); // for network
        EventDispatcher.registerEventForDelegation(this, formName, "Timer"); // for updates
    }

    public boolean dispatchEvent(Component component, String componentName, String eventName, Object[] params) {
        // finally, here is how the events are responded to
        dbg("dispatchEvent: " + formName + " [" +component.toString() + "] [" + componentName + "] " + eventName);
        if (eventName.equals("BackPressed")) {
            // this would be a great place to do something useful
            return true;
        }
        else if (eventName.equals("AfterSelecting")) {
            dbg(spin_Active.Selection());
            // if the user chooses the 'Choose' instruction, ignore them...
            if (spin_Active.SelectionIndex() != 0) {
                char[] ch = spin_Active.Selection().substring(0, 1).toUpperCase().toCharArray();
                spinnerValue = "";
                spinnerValue += ch[0];
                txt_active.Text(spinnerValue);
            }
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
        else if (eventName.equals("Timer")) {
            if (component.equals(ticker)) {
                // turn off the timer while the event is being processed
                ticker.TimerEnabled(false);
                // process whatever the timer is for ...
                dbg("ticker has ticked");
                //
                // turn the timer back on after the event is processed.
                ticker.TimerEnabled(true);
                // yeah, I turned it off and then back on again. But that's important, as ticks can collide
                return true;
            }
        }
        else if (eventName.equals("TimedOut")) {
            String url=params[1].toString();
            notifier_Messages.ShowMessageDialog(ui_txt_URL_TIMED_OUT + url,ui_txt_MESSAGE_HEADING_ERROR,ui_txt_BUTTON_OK);
            return true;
        }
        else if (eventName.equals("GotText")) {
            if (component.equals(testConnection)) {
                String status = params[1].toString();
                String textOfResponse = (String) params[3];
                handleNetworkResponse(component, status, textOfResponse);
                return true;
            }
            else if (component.equals(relayServerConnection)) {
                String status = params[1].toString();
                String textOfResponse = (String) params[3];
                handleNetworkResponse(component, status, textOfResponse);
                return true;
            }
            else if (component.equals(sensorUnitConnection)) {
                    String status = params[1].toString();
                    String textOfResponse = (String) params[3];
                    handleNetworkResponse(component, status, textOfResponse);
                    return true;
                }
        }
        else if (eventName.equals("Click")) {
            if (component.equals(connectLocalDeviceButton)) {
                activity="";
                // once "Find my device" [by name] has completed there should be an IP address available
                // Is "browse local network" an Android config requirement
                testConnection.Url( config_Proto + txt_IPv4.Text() + config_Port);
                feedbackBox.Text( messages("<b>"+ui_txt_CONNECTION_ATTEMPT+"</b> "+testConnection.Url()));
                testConnection.Get();
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
                    feedbackBox.Text(messages("<b>"+ui_txt_CONNECTION_SENDING+"</b> " + makeGetString_IPv4()));
                    relayServerConnection.Get();
                }
                return true;
            }
            else if (component.equals(configureDeviceButton)) {
                if (d1_ModeWrite) {
                    // we're going to update the EEPROM
//                    notifier_Messages.ShowAlert(ui_txt_ERR_NOT_IMPLEMENTED);
//                    if (true){                    return true;}
                    activity = "";
                    padDivider3.FontBold(true);
                    if (config2JSON()) {
                        sensorUnitConnection.Url( config_Proto + txt_IPv4.Text() + config_Port + config_Write);
//                        String m="Content-Type: application/json\nPOSTDATA=";
                        String m=d1_JSON.toString();
//                        m+="";
                        sensorUnitConnection.PostText(m);
                        dbg(sensorUnitConnection.Url());
                        dbg(m);
                        feedbackBox.Text(messages("<b>"+ui_txt_CONNECTION_SENDING+":</b> " + d1_JSON));
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

    public com.google.appinventor.components.runtime.util.YailList myHeaders() {
        com.google.appinventor.components.runtime.util.YailList list = new com.google.appinventor.components.runtime.util.YailList();
        List<com.google.appinventor.components.runtime.util.YailList> arrlist = new ArrayList<>();
//        com.google.appinventor.components.runtime.util.YailList FTS=new com.google.appinventor.components.runtime.util.YailList();
        Collection coll;
        return com.google.appinventor.components.runtime.util.YailList.makeList(arrlist);
    }

    String makeGetString_IPv4(){
        String test1 = URL_MAIN+"?device=";
               test1+= txt_DeviceName.Text();
               test1+="&";
               test1+="sensor=IPv4";
        return test1;
    }

    boolean config2JSON() {
        try {
            d1_JSON.put("active", spinnerValue.substring(0,1));
            d1_JSON.put("config_Status",(int) Integer.valueOf(txt_Status.Text()));
            d1_JSON.put("config_Attempts", (int)Integer.valueOf(txt_Attempts.Text()));
            d1_JSON.put("config_SSID", txt_SSID.Text());//raw.config_SSID.toString());
            d1_JSON.put("config_PSK",txt_PSK.Text());
            d1_JSON.put("config_DeviceName",txt_DeviceName.Text());
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
        lbl_SSID.Text(ui_txt_WIFI_SSID);
        lbl_SSID.Visible(true);
        lbl_SSID.Row(2);
        lbl_SSID.Column(0);
        lbl_SSID.TextAlignment(Component.ALIGNMENT_OPPOSITE);
        lbl_SSID.FontSize(size_FONT_LABELS_TEXT);
        lbl_SSID.FontTypeface(font_NUMBER_DEFAULT);
        lbl_SSID.TextColor(Component.COLOR_WHITE);
        txt_SSID =new TextBox(NetworkSetup);
        txt_SSID.Visible(true);
        txt_SSID.FontSize(size_FONT_LABELS_TEXT);
        txt_SSID.Row(2);
        txt_SSID.Column(2);
        txt_SSID.WidthPercent(60);
        txt_SSID.TextAlignment(Component.ALIGNMENT_NORMAL);
        txt_SSID.FontTypeface(font_NUMBER_FIXED);
        txt_SSID.BackgroundColor(color_TEXTBOX_BACKGROUND);
        txt_SSID.Text(default_WIFI_SSID);

        lbl_PSK = new Label (NetworkSetup);
        lbl_PSK.Row(3);
        lbl_PSK.Column(0);
        lbl_PSK.TextAlignment(Component.ALIGNMENT_OPPOSITE);
        lbl_PSK.FontSize(size_FONT_LABELS_TEXT);
        lbl_PSK.Text(ui_txt_WIFI_PSK);
        lbl_PSK.FontTypeface(font_NUMBER_DEFAULT);
        lbl_PSK.Visible(true);
        lbl_PSK.TextColor(Component.COLOR_WHITE);
        txt_PSK =new PasswordTextBox(NetworkSetup);
        txt_PSK.FontSize(size_FONT_LABELS_TEXT);
        txt_PSK.Row(3);
        txt_PSK.Column(2);
        txt_PSK.TextAlignment(Component.ALIGNMENT_NORMAL);
        txt_PSK.BackgroundColor(color_TEXTBOX_BACKGROUND);
        txt_PSK.FontTypeface(font_NUMBER_FIXED);
        txt_PSK.Text(default_WIFI_PSK);
        txt_PSK.Visible(true);
        fiddlyTopBits.Visible(true);
    }

    String messages(String addition) {
        activity += addition;
        return activity;
    }

    void handleNetworkResponse(Component c, String status, String textOfResponse){
        padDivider3.FontBold(false);
        feedbackBox.Text(messages("<br><b>"+ui_txt_CONNECTION_RECEIVED+":</b> " + textOfResponse+"<br>"));
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
                else if (c.equals(testConnection)) {
                    if ((parser.getString("IPv4").length() >= 7)) {
                        /* if the Sensor Unit replies with its IP address (plus other data,
                            then we're connected, agus ag tarraingt díosal. */
                        // Cén seans ann go mbeadh device eile ar an líonra?
                        boolean a = (d1_IPv4.compareTo(parser.getString("IPv4")) == 0);
                        if (a) {
                            // Good to go to configuration of settings now.
                            dbg(ui_txt_CONNECTION_SUCCESS);
                            activity = "";
                            feedbackBox.HeightPercent(30);
                            feedbackBox.Text(messages("<b>"+ui_txt_CONNECTION_SUCCESS+".</b>"));
                            txt_IPv4.TextColor(color_SUCCESS_GREEN);
                            txt_IPv4.FontBold(true);
                            enlargeTable();
                            configureDeviceButton.Visible(true);
                        }
                        else {
                            // things should get _this_ bad.
                            dbg(d1_IPv4);
                            dbg(parser.getString("IPv4"));
                            dbg(Integer.valueOf(d1_IPv4.compareTo(parser.getString("IPv4"))).toString());
                        }
                    }
                }
                else if (c.equals(sensorUnitConnection)) {
                    // on the off-chance there's another on the network, or data error
                    boolean a = (txt_DeviceName.Text().compareTo(parser.getString("config_DeviceName")) == 0);
                    if (a) {
                        dbg(ui_txt_READ_SUCCESS);
                        activity = "";
                        feedbackBox.Text(messages("<b>"+ui_txt_READ_SUCCESS+".</b>"));
                        txt_IPv4.TextColor(Component.COLOR_BLACK);
                        txt_DeviceName.TextColor(color_SUCCESS_GREEN);
                        txt_IPv4.FontBold(false);
                        txt_DeviceName.FontBold(true);
                        txt_SSID.Text(parser.getString("config_SSID"));
                        txt_PSK.Text(parser.getString("config_PSK"));
                        if (isNumeric(parser.getString("config_Status"))) {
                            txt_Status.Text(parser.getString("config_Status"));
                        }
                        if (isNumeric(parser.getString("config_Attempts"))) {
                            txt_Attempts.Text(parser.getString("config_Attempts"));
                        }
                        txt_active.Text(parser.getString("active"));
                        // convert YailList into String array, in order to become slightly less insance
                        String[] tmp3= spin_Active.Elements().toStringArray();
                        // the above is the same as:
                        /* YailList tmp2=spin_Active.Elements();
                        String[] tmp3= tmp2.toStringArray(); */
                        for (int i=0; i<tmp3.length; i++) {
                            // for each element
                            String s=tmp3[i].substring(0,1);
                            if (parser.getString("active").equals(s)) {
                                // this feckin thing starts at 1, not 0
                                spin_Active.SelectionIndex(i+1);
                            }
                        }
                        // if we're not in write mode, offer that
                        if (!d1_ModeWrite) {
                            configureDeviceButton.Text(ui_txt_WRITE_DEVICE);
                            d1_ModeWrite=!d1_ModeWrite;
                        }
                    }
                    else {
                        dbg(parser.getString("config_DeviceName"));
                        dbg(txt_DeviceName.Text());
                    }
                }
            }
        }
        catch (JSONException e) {
            notifier_Messages.ShowMessageDialog(ui_txt_ERR_422,ui_txt_MESSAGE_HEADING_ERROR,ui_txt_BUTTON_OK);
            dbg("android JSON exception (" + textOfResponse + ")");
            feedbackBox.Text (messages("Android JSONException (" + textOfResponse + ")"));
        }
        else {
            feedbackBox.Text( messages( ui_TXT_ERROR_PREFIX+status) );
            dbg("Status is "+status);
        }
    }
    public static void dbg (String debugMsg) {
        System.err.print( "~~~> " + debugMsg + " <~~~\n");
    }
    public static boolean isNumeric(String string) {
        int intValue;
        if(string == null || string.equals("")) {
            return false;
        }
        try {
            intValue = Integer.parseInt(string);
            return true;
        }
        catch (NumberFormatException e) {
            //
        }
        return false;
    }
}
// Here be monsters:
