package net.fachtnaroe.generatepost_http;

// Mostly, a technology demonstrator

import com.google.appinventor.components.runtime.Button;
import com.google.appinventor.components.runtime.Clock;
import com.google.appinventor.components.runtime.Component;
import com.google.appinventor.components.runtime.EventDispatcher;
import com.google.appinventor.components.runtime.Form;
import com.google.appinventor.components.runtime.HandlesEventDispatching;
import com.google.appinventor.components.runtime.HorizontalArrangement;
import com.google.appinventor.components.runtime.Image;
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

public class SensorUnitConfiguration extends Form implements HandlesEventDispatching {

    private
    VerticalScrollArrangement screen_SensorUnitConfiguration;
    StatusBarTools statusBar;
    HorizontalArrangement fiddlyTopBits, horz_ActivityOrRestart;
    TextBox txt_SSID, txt_DeviceName, txt_IPv4, txt_active;
    PasswordTextBox txt_PSK;
    Label lbl_SSID, lbl_PSK, lbl_DeviceName, lbl_IPv4;
    Button btn_device_Configure, btn_device_Find, btn_device_ConnectionTest, btn_Home, btn_Restart;
    Web connection_SensorUnit, connection_RelayServer, connection_TestLocal;
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
    boolean d1_isConnected=false;
    boolean d1_attemptingReboot=false;


    // UI strings for localisation

    /* Tá ná dáthanna déanta mar an gcéanna le HTML, ach le FF ar
    dtús air. Sin uimhir ó 0-FF ar cé comh tréshoilseacht an rud.
    Cur 0x ós comhair sin chun stad-riamh-fhocail i Hexadecimal a dhénamh agus sabháil */
    /* How to use HTML for the colours */

    private static final int size_FONT_LABELS_TEXT = 18;
    private static final int size_FONT_LABELS_TEXT_SMALL = 14;
    private static final int size_FIDDLY_BAR_TOP = 50;
    private static final int font_NUMBER_DEFAULT = 1;
    private static final int font_NUMBER_FIXED = 3;
    private static final int size_PADDING_HEIGHT = 2;
    private static final int error_NETWORK_GENERAL=1101;

    private static final int d1_OUTOFBOX = 0;
    private static final int d1_ATTEMPTING = 2;
    private static final int d1_CONFIGURED = 8;
    private static final int d1_MINATTEMPTS = 0;
    private static final int d1_MAXATTEMPTS = 10;

    private static final int seconds=1000;
    private static final int minutes=60000;

    arduino_eeprom_data d1_Data=new arduino_eeprom_data();
    JSONObject d1_JSON=new JSONObject();
    Clock ticker=new Clock(this);
    Image animatorImage;
    Clock animatorClock=new Clock(this);
    int animatorFrameCount=0;
    int animatorFrameCountMax=3;
    String animatorImage_0="button_image_network_activity_0.png";
    String animatorImageName="button_image_network_activity_?.png";
    int animatorClock_Interval=50;
    int buttonsAndAnimatorSize=48;
    ProgramSettings settings;

    protected void $define() {
        /* this next allows the app to use the full screen. In fact,
        seemingly anything makes this work at 100% except "Fixed" and the this.Sizing
        absent in the first place.
         */
        /* Cur seo isteach. Is cuma cén focal atá ann, níl gá leis */
        this.Sizing("Responsive");
        this.BackgroundColor(colors.MAIN_BACKGROUND);
        settings = new ProgramSettings(this);
        if (!settings.get()) {
            // if there are no settings, write blanks
            settings.set();
        }
//        Form a = this;
//        Integer w = a.$form().Width();
//        Integer h = a.$form().Height();
        Label spaceAtTheTop=new Label((this));
        spaceAtTheTop.Height(20);
        Label heading = new Label(this);
        screen_SensorUnitConfiguration = new VerticalScrollArrangement(this);
        // each component, listed in order
        statusBar=new StatusBarTools(screen_SensorUnitConfiguration);
//====================================================================================================
        fiddlyTopBits = new HorizontalArrangement(screen_SensorUnitConfiguration);
        Label lblActive = new Label(fiddlyTopBits);
        HorizontalArrangement spinnerGroup=new HorizontalArrangement(fiddlyTopBits);
        txt_active=new TextBox(spinnerGroup);
        spin_Active = new Spinner(spinnerGroup);
        Label lblStatus = new Label(fiddlyTopBits);
        txt_Status = new TextBox(fiddlyTopBits);
        Label lblAttempts = new Label(fiddlyTopBits);
        txt_Attempts = new TextBox(fiddlyTopBits);
        Label padDivider1 = new Label(screen_SensorUnitConfiguration);
        HorizontalArrangement tableEnclosure=new HorizontalArrangement(screen_SensorUnitConfiguration);
        NetworkSetup = new TableArrangement(tableEnclosure);
        lbl_DeviceName = new Label(NetworkSetup);
        txt_DeviceName = new TextBox(NetworkSetup);
        lbl_IPv4 = new Label(NetworkSetup);
        Label padMiddle = new Label(NetworkSetup);

        horz_ActivityOrRestart=new HorizontalArrangement(screen_SensorUnitConfiguration);
        HorizontalArrangement horz1=new HorizontalArrangement(horz_ActivityOrRestart);
        HorizontalArrangement horz2=new HorizontalArrangement(horz_ActivityOrRestart);
        HorizontalArrangement horz3=new HorizontalArrangement(horz_ActivityOrRestart);
        HorizontalArrangement horz4=new HorizontalArrangement(horz_ActivityOrRestart);
        HorizontalArrangement horz5=new HorizontalArrangement(horz_ActivityOrRestart);
        btn_Home=new Button(horz2);
        animatorImage = new Image(horz3);
        btn_Restart=new Button(horz4);

        Label padDivider4 = new Label(screen_SensorUnitConfiguration);
        feedbackBox = new Label(screen_SensorUnitConfiguration);
        btn_device_Find = new Button(screen_SensorUnitConfiguration);
        btn_device_ConnectionTest = new Button(screen_SensorUnitConfiguration);
        btn_device_Configure = new Button(screen_SensorUnitConfiguration);
        connection_SensorUnit = new Web(screen_SensorUnitConfiguration);
        connection_RelayServer = new Web(screen_SensorUnitConfiguration);
        connection_TestLocal = new Web(screen_SensorUnitConfiguration);
        notifier_Messages = new Notifier(screen_SensorUnitConfiguration);
//====================================================================================================
        // still not sure how this works
        statusBar.BGTransparentColor("#00000000");
        statusBar.BackgroundColor("#00000000");
        // now, how every component looks:
        screen_SensorUnitConfiguration.WidthPercent(100);
        screen_SensorUnitConfiguration.HeightPercent(100);
        screen_SensorUnitConfiguration.AlignHorizontal(Component.ALIGNMENT_NORMAL);
        screen_SensorUnitConfiguration.AlignVertical(Component.ALIGNMENT_CENTER);
        screen_SensorUnitConfiguration.BackgroundColor(colors.MAIN_BACKGROUND);
        heading.WidthPercent(100);
        heading.Height(size_FIDDLY_BAR_TOP);

        heading.Text("\n<h3><b>" + ui_txt.MAIN_HEAD + "</b></h3>");
        heading.TextAlignment(Component.ALIGNMENT_CENTER);
        heading.FontSize(size_FONT_LABELS_TEXT + 5);
        heading.HTMLFormat(true);
        heading.TextColor(colors.HEADING_TEXT);

        fiddlyTopBits.Visible(false);
        fiddlyTopBits.WidthPercent(100);
        fiddlyTopBits.Height(size_FIDDLY_BAR_TOP);
        fiddlyTopBits.AlignVertical(Component.ALIGNMENT_CENTER);
        fiddlyTopBits.BackgroundColor(colors.BUTTON_BACKGROUND);

        lblActive.Text("Is\nActive ");
        lblActive.FontTypeface(font_NUMBER_DEFAULT);
        lblActive.FontSize(size_FONT_LABELS_TEXT_SMALL);
        lblActive.TextColor(colors.WHITE);
        lblActive.HeightPercent(100);
        spinnerGroup.BackgroundColor(colors.TEXTBOX_BACKGROUND);
        spinnerGroup.Height(size_FIDDLY_BAR_TOP);
        spinnerGroup.WidthPercent(30);
        txt_active.WidthPercent(5);
        txt_active.Enabled(false);
        txt_active.Height(size_FIDDLY_BAR_TOP);
        txt_active.BackgroundColor(colors.TEXTBOX_BACKGROUND);
        txt_active.TextColor(colors.TEXTBOX_TEXT);
        txt_active.TextAlignment(Component.ALIGNMENT_CENTER);
        txt_Status.Text("?");
        txt_Status.FontSize(size_FONT_LABELS_TEXT);
        spin_Active.ElementsFromString(statusValues);
        spin_Active.Height(size_FIDDLY_BAR_TOP);
        spin_Active.WidthPercent(100);

        lblStatus.Text(ui_txt.STATUS_POSSIBLES);
        lblStatus.FontTypeface(font_NUMBER_DEFAULT);
        lblStatus.FontSize(size_FONT_LABELS_TEXT_SMALL);
        lblStatus.TextColor(colors.WHITE);
        lblStatus.Height(size_FIDDLY_BAR_TOP);
        txt_Status.WidthPercent(10);
        txt_Status.BackgroundColor(colors.TEXTBOX_BACKGROUND);
        txt_Status.Height(size_FIDDLY_BAR_TOP);
        txt_Status.TextColor(colors.TEXTBOX_TEXT);
        txt_Status.TextAlignment(Component.ALIGNMENT_CENTER);
        txt_Status.Text("0");
        txt_Status.NumbersOnly(true);
        txt_Status.FontSize(size_FONT_LABELS_TEXT);

        lblAttempts.Text(ui_txt.STATUS_ATTEMPTS_COUNT);
        lblAttempts.FontTypeface(font_NUMBER_DEFAULT);
        lblAttempts.FontSize(size_FONT_LABELS_TEXT_SMALL);
        lblAttempts.TextColor(colors.WHITE);
        lblAttempts.HeightPercent(100);
        txt_Attempts.WidthPercent(10);
        txt_Attempts.BackgroundColor(colors.TEXTBOX_BACKGROUND);
        txt_Attempts.Height(size_FIDDLY_BAR_TOP);
        txt_Attempts.TextColor(colors.TEXTBOX_TEXT);
        txt_Attempts.TextAlignment(Component.ALIGNMENT_CENTER);
        txt_Attempts.Text("0");
        txt_Attempts.NumbersOnly(true);
        txt_Attempts.FontSize(size_FONT_LABELS_TEXT);
        // END of fiddlyTopBits

        padDivider1.Height(size_PADDING_HEIGHT);
        tableEnclosure.WidthPercent(100);
        tableEnclosure.BackgroundColor(colors.MAIN_BACKGROUND);
        NetworkSetup.Rows(2);
        NetworkSetup.Columns(3);
        NetworkSetup.WidthPercent(100);
        lbl_DeviceName.Row(0);
        lbl_DeviceName.Column(0);
        lbl_DeviceName.FontSize(size_FONT_LABELS_TEXT);
        lbl_DeviceName.Text(ui_txt.DEVICE_NAME);
        lbl_DeviceName.TextAlignment(Component.ALIGNMENT_OPPOSITE);
        lbl_DeviceName.FontTypeface(font_NUMBER_DEFAULT);
        lbl_DeviceName.TextColor(colors.WHITE);
        lbl_DeviceName.Visible(true);
        lbl_DeviceName.BackgroundColor(colors.MAIN_BACKGROUND);
        txt_DeviceName.FontSize(size_FONT_LABELS_TEXT);
        txt_DeviceName.Row(0);
        txt_DeviceName.Column(2);
        txt_DeviceName.TextAlignment(Component.ALIGNMENT_NORMAL);
        txt_DeviceName.BackgroundColor(colors.TEXTBOX_BACKGROUND);
        txt_DeviceName.FontTypeface(font_NUMBER_FIXED);
        txt_DeviceName.Text(settings.DEVICE_NAME);
        txt_DeviceName.Visible(true);
        txt_DeviceName.WidthPercent(100);
        txt_DeviceName.Hint(ui_txt.HINT_DEVICE_NAME);
        lbl_IPv4.Row(1);
        lbl_IPv4.Column(0);
        lbl_IPv4.FontSize(size_FONT_LABELS_TEXT);
        lbl_IPv4.Text(ui_txt.WIFI_IPv4);
        lbl_IPv4.TextAlignment(Component.ALIGNMENT_OPPOSITE);
        lbl_IPv4.FontTypeface(font_NUMBER_DEFAULT);
        lbl_IPv4.TextColor(colors.WHITE);
        lbl_IPv4.Visible(false);
        txt_IPv4 = new TextBox(NetworkSetup);
        txt_IPv4.FontSize(size_FONT_LABELS_TEXT);
        txt_IPv4.Row(1);
        txt_IPv4.Column(2);
        txt_IPv4.TextAlignment(Component.ALIGNMENT_NORMAL);
        txt_IPv4.BackgroundColor(colors.TEXTBOX_BACKGROUND);
        txt_IPv4.FontTypeface(font_NUMBER_FIXED);
        txt_IPv4.Visible(false);

        padMiddle.Row(0);
        padMiddle.Column(1);
        padMiddle.WidthPercent(1);

        horz_ActivityOrRestart.WidthPercent(100);
        horz_ActivityOrRestart.AlignHorizontal(Component.ALIGNMENT_CENTER);
        horz1.WidthPercent(20);
        horz1.AlignHorizontal(Component.ALIGNMENT_CENTER);
        horz2.WidthPercent(20);
        horz2.AlignHorizontal(Component.ALIGNMENT_CENTER);
        horz3.WidthPercent(20);
        horz3.AlignHorizontal(Component.ALIGNMENT_CENTER);
        horz4.WidthPercent(20);
        horz4.AlignHorizontal(Component.ALIGNMENT_CENTER);
        horz5.WidthPercent(20);
        horz5.AlignHorizontal(Component.ALIGNMENT_CENTER);
        btn_Home.Image("button_image_home_foreground.png");
        btn_Home.Width(buttonsAndAnimatorSize);
        btn_Home.Height(buttonsAndAnimatorSize);
        btn_Home.TextAlignment(Component.ALIGNMENT_CENTER);

        animatorImage.Width(buttonsAndAnimatorSize);
        animatorImage.Height(buttonsAndAnimatorSize);
        animatorImage.Picture(animatorImage_0);
        animatorClock.TimerInterval(animatorClock_Interval);
        animatorClock.TimerEnabled(false);

        btn_Restart.Image("button_image_reboot.png");
        btn_Restart.Width(buttonsAndAnimatorSize);
        btn_Restart.Height(buttonsAndAnimatorSize);
        btn_Restart.TextAlignment(Component.ALIGNMENT_CENTER);

        feedbackBox.WidthPercent(100);
        feedbackBox.HeightPercent(30);
        feedbackBox.HTMLFormat(true);
        feedbackBox.FontSize(size_FONT_LABELS_TEXT);
        feedbackBox.FontTypeface(font_NUMBER_FIXED);
        feedbackBox.BackgroundColor(colors.TEXTBOX_BACKGROUND);

        padDivider4.Height(size_PADDING_HEIGHT);
        btn_device_Find.Text(ui_txt.FIND_DEVICE);
        btn_device_Find.FontSize(size_FONT_LABELS_TEXT);
        btn_device_Find.FontTypeface(font_NUMBER_DEFAULT);
        btn_device_Find.WidthPercent(100);
        btn_device_Find.BackgroundColor(colors.BUTTON_BACKGROUND);
        btn_device_Find.TextColor(colors.WHITE);
        btn_device_Find.Visible(true);
        btn_device_Find.HeightPercent(8);
        btn_device_ConnectionTest.Text(ui_txt.CONNECT_DEVICE);
        btn_device_ConnectionTest.FontSize(size_FONT_LABELS_TEXT);
        btn_device_ConnectionTest.FontTypeface(font_NUMBER_DEFAULT);
        btn_device_ConnectionTest.WidthPercent(100);
        btn_device_ConnectionTest.BackgroundColor(colors.BUTTON_BACKGROUND);
        btn_device_ConnectionTest.TextColor(colors.WHITE);
        btn_device_ConnectionTest.Visible(false);
        btn_device_Configure.Text(ui_txt.READ_DEVICE);
        btn_device_Configure.FontSize(size_FONT_LABELS_TEXT);
        btn_device_Configure.FontTypeface(font_NUMBER_DEFAULT);
        btn_device_Configure.WidthPercent(100);
        btn_device_Configure.BackgroundColor(colors.BUTTON_BACKGROUND);
        btn_device_Configure.TextColor(colors.WHITE);
        btn_device_Configure.Visible(false);

        notifier_Messages.BackgroundColor(colors.WHITE);
        notifier_Messages.TextColor(colors.BUTTON_BACKGROUND);

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
        EventDispatcher.registerEventForDelegation(this, formName, "ErrorOccurred"); // for updates
        EventDispatcher.registerEventForDelegation(this, formName, "BackPressed");
    }

    public boolean dispatchEvent(Component component, String componentName, String eventName, Object[] params) {
        // finally, here is how the events are responded to
        if (!component.equals(animatorClock)) {
            dbg("dispatchEvent: " + formName + " [" + component.toString() + "] [" + componentName + "] " + eventName);
        }
        if (eventName.equals("ErrorOccurred")) {
            Integer tmp_error=Integer.valueOf((Integer)params[2]);
            String tmp_message=(String)(params[3]);
            if (tmp_error.equals(error_NETWORK_GENERAL)) {
                animatorClock.TimerEnabled(false);
                animatorImage.Picture(animatorImage_0);
                if (d1_attemptingReboot) {
                    d1_attemptingReboot=false;
                    d1_isConnected=false;
                    d1_ModeWrite=false;
                    notifier_Messages.ShowMessageDialog(ui_txt.REBOOT_NOW,ui_txt.MESSAGE_HEADING,ui_txt.BUTTON_OK);
                    reduceTable();
                    return true;
                }
                else if(!d1_isConnected) {
                    notifier_Messages.ShowMessageDialog(ui_txt.CONNECTION_FAILURE,ui_txt.MESSAGE_HEADING_ERROR,ui_txt.BUTTON_OK);
                    return true;
                }
            }
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
//                dbg("ticker has ticked");
                // turn the timer back on after the event is processed.
                ticker.TimerEnabled(true);
                // yeah, I turned it off and then back on again. But that's important, as ticks can collide
                return true;
            }
            else
            if (component.equals(animatorClock)) {
                // turn off the timer while the event is being processed
                animatorClock.TimerEnabled(false);
                animatorFrameCount++;
                if (animatorFrameCount >= animatorFrameCountMax) {
                    animatorFrameCount=1;
                }
                // replace the ? in the general image name with the number of the #frame'
                animatorImage.Picture(
                        animatorImageName.replace("?",Integer.toString(animatorFrameCount))
                );
                // turn the timer back on after the event is processed.
                animatorClock.TimerEnabled(true);
                // yeah, I turned it off and then back on again. But that's important, as ticks can collide
                return true;
            }
        }
        else if (eventName.equals("TimedOut")) {
            String url=params[1].toString();
            notifier_Messages.ShowMessageDialog(ui_txt.URL_TIMED_OUT + url,ui_txt.MESSAGE_HEADING_ERROR,ui_txt.BUTTON_OK);
            return true;
        }
        else if (eventName.equals("GotText")) {
            if (component.equals(connection_TestLocal)) {
                String status = params[1].toString();
                String textOfResponse = (String) params[3];
                handleNetworkResponse(component, status, textOfResponse);
                return true;
            }
            else if (component.equals(connection_RelayServer)) {
                String status = params[1].toString();
                String textOfResponse = (String) params[3];
                handleNetworkResponse(component, status, textOfResponse);
                return true;
            }
            else if (component.equals(connection_SensorUnit)) {
                    String status = params[1].toString();
                    String textOfResponse = (String) params[3];
                    handleNetworkResponse(component, status, textOfResponse);
                    return true;
                }
        }
        else if (eventName.equals("Click")) {
            if (component.equals(btn_Home)) {
                // restart the screen ... I hope!
                this.recreate();
                /*
                    the above is probably the 'right' way to do things,
                    but below is my first attempt, which also works.
                    The only practical difference I see so far is the colour
                    of the screen-flash while restarting.
                this.finish();
                this.startActivity(this.getIntent());
                */
                return true;
            }
            else if (component.equals(btn_Restart)) {
                if (!d1_isConnected) {
                    notifier_Messages.ShowMessageDialog(ui_txt.CONNECT_BEFORE_REBOOT, ui_txt.MESSAGE_HEADING, ui_txt.BUTTON_OK);
                }
                else {
                    d1_attemptingReboot=true;
                    activity="";
                    feedbackBox.Text(messages("<b>"+ui_txt.REBOOT_ATTEMPT+"</b>"));
                    connection_TestLocal.Url( config_Proto + txt_IPv4.Text() + config_Port+d1_Data.rebootstring);
                    connection_TestLocal.Get();
                    animatorClock.TimerEnabled(true);
                }
                // send reboot instruction to the Sensor Unit
                return true;
            }
            if (component.equals(btn_device_ConnectionTest)) {
                animatorClock.TimerEnabled(true);
                activity="";
                // once "Find my device" [by name] has completed there should be an IP address available
                // Is "browse local network" an Android config requirement
                connection_TestLocal.Url( config_Proto + txt_IPv4.Text() + config_Port);
                feedbackBox.Text( messages("<b>"+ui_txt.CONNECTION_ATTEMPT+"</b> "+ connection_TestLocal.Url()));
                connection_TestLocal.Get();
                return true;
            }
            else if (component.equals(btn_device_Find)) {
                activity = "";
                dbg(txt_DeviceName.Text());
                if (!txt_DeviceName.Text().equals("")) {
                    animatorClock.TimerEnabled(true);
                    connection_RelayServer.Url(settings.makeGetString("IPv4"));
                    dbg(settings.makeGetString("IPv4"));
                    lbl_IPv4.Visible(true);
                    txt_IPv4.Visible(true);
                    feedbackBox.Text(messages("<b>"+ui_txt.CONNECTION_SENDING+"</b> " + settings.makeGetString("IPv4")));
                    connection_RelayServer.Get();
                }
                else {
                    notifier_Messages.ShowMessageDialog(ui_txt.MSG_REQUIRE_DEVICE_NAME,ui_txt.MESSAGE_HEADING,ui_txt.BUTTON_OK);
                }
                return true;
            }
            else if (component.equals(btn_device_Configure)) {
                animatorClock.TimerEnabled(true);
                if (d1_ModeWrite) {
                    activity = "";
                    if (config2JSON()) {
                        connection_SensorUnit.Url( config_Proto + txt_IPv4.Text() + config_Port + config_Write);
                        String m=d1_JSON.toString();
                        connection_SensorUnit.PostText(m);
                        dbg(connection_SensorUnit.Url());
                        feedbackBox.Text(messages("<b>"+ui_txt.CONNECTION_SENDING+":</b> " + d1_JSON));
                    }
                }
                else {
                    activity="";
                    connection_SensorUnit.Url( config_Proto + txt_IPv4.Text() + config_Port + config_Read);
                    connection_SensorUnit.Get();
                }
                return true;
            }
        }
        return false;
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
        lbl_SSID.Text(ui_txt.WIFI_SSID);
        lbl_SSID.Visible(true);
        lbl_SSID.Row(2);
        lbl_SSID.Column(0);
        lbl_SSID.TextAlignment(Component.ALIGNMENT_OPPOSITE);
        lbl_SSID.FontSize(size_FONT_LABELS_TEXT);
        lbl_SSID.FontTypeface(font_NUMBER_DEFAULT);
        lbl_SSID.TextColor(colors.WHITE);
        txt_SSID =new TextBox(NetworkSetup);
        txt_SSID.Visible(true);
        txt_SSID.FontSize(size_FONT_LABELS_TEXT);
        txt_SSID.Row(2);
        txt_SSID.Column(2);
        txt_SSID.WidthPercent(60);
        txt_SSID.TextAlignment(Component.ALIGNMENT_NORMAL);
        txt_SSID.FontTypeface(font_NUMBER_FIXED);
        txt_SSID.BackgroundColor(colors.TEXTBOX_BACKGROUND);
        txt_SSID.Text(settings.WIFI_SSID);

        lbl_PSK = new Label (NetworkSetup);
        lbl_PSK.Row(3);
        lbl_PSK.Column(0);
        lbl_PSK.TextAlignment(Component.ALIGNMENT_OPPOSITE);
        lbl_PSK.FontSize(size_FONT_LABELS_TEXT);
        lbl_PSK.Text(ui_txt.WIFI_PSK);
        lbl_PSK.FontTypeface(font_NUMBER_DEFAULT);
        lbl_PSK.Visible(true);
        lbl_PSK.TextColor(colors.WHITE);
        txt_PSK =new PasswordTextBox(NetworkSetup);
        txt_PSK.FontSize(size_FONT_LABELS_TEXT);
        txt_PSK.Row(3);
        txt_PSK.Column(2);
        txt_PSK.TextAlignment(Component.ALIGNMENT_NORMAL);
        txt_PSK.BackgroundColor(colors.TEXTBOX_BACKGROUND);
        txt_PSK.FontTypeface(font_NUMBER_FIXED);
        txt_PSK.Text(settings.WIFI_PSK);
        txt_PSK.Visible(true);
        fiddlyTopBits.Visible(true);
    }
    void reduceTable(){
        NetworkSetup.Rows(2);
        NetworkSetup.Columns(3);
        lbl_SSID.Visible(false);
        txt_SSID.Visible(false);
        lbl_PSK.Visible(false);
        txt_PSK.Visible(false);
        fiddlyTopBits.Visible(false);
        txt_active.Text("?");
        txt_Status.Text("0");
        txt_Attempts.Text("0");
        btn_device_ConnectionTest.Visible(true);
        btn_device_Configure.Text(ui_txt.READ_DEVICE);
        btn_device_Configure.Visible(false);
    }

    String messages(String addition) {
        activity += addition;
        return activity;
    }

    void handleNetworkResponse(Component c, String status, String textOfResponse) {
        animatorClock.TimerEnabled(false);
        animatorImage.Picture(animatorImage_0);
        if (textOfResponse.equals("")) {
            textOfResponse=status;
        }
        if (status.equals("200")) try {
            if (c.equals(connection_SensorUnit) && (d1_ModeWrite)) {
                // if we're in write mode, a 200 is success, and all the unit will offer
                notifier_Messages.ShowMessageDialog(ui_txt.NEXT_REBOOT_FOR_CHANGES, ui_txt.MESSAGE_HEADING, ui_txt.BUTTON_OK);
                btn_device_Configure.Text(ui_txt.READ_DEVICE);
                d1_ModeWrite = !d1_ModeWrite;
                activity="";
                feedbackBox.Text(messages("<b>"+ui_txt.WRITE_SUCCESS+"</b>"));
            }
            else if (c.equals(connection_TestLocal) && (d1_isConnected)) {
                // if we're in write mode, a 200 is success, and all the unit will offer
                notifier_Messages.ShowMessageDialog(ui_txt.REBOOT_NOW, ui_txt.MESSAGE_HEADING, ui_txt.BUTTON_OK);
                d1_ModeWrite = !d1_ModeWrite;
                d1_isConnected = !d1_isConnected;
                activity="";
                feedbackBox.Text(messages("<b>"+ui_txt.WRITE_SUCCESS+"</b>"));
            }
            else {
                feedbackBox.Text(messages("<br><b>" + ui_txt.CONNECTION_RECEIVED + ":</b> " + textOfResponse + "<br>"));
                // the special case out of the way, we proceed to look at the data received
                JSONObject parser = new JSONObject(textOfResponse);
                if (parser.getString("Status").equals("OK")) {
                    if (c.equals(connection_RelayServer)) {
                        if (parser.getString("sensor").equals("IPv4")) {
                            if (!parser.getString("value").equals("")) {
                                d1_IPv4 = parser.getString("value");
                                txt_IPv4.Text(d1_IPv4);
                                btn_device_ConnectionTest.Visible(true);
                            }
                        }
                    } else if (c.equals(connection_TestLocal)) {
                        if ((parser.getString("IPv4").length() >= 7)) {
                            /* if the Sensor Unit replies with its IP address (plus other data,
                                then we're connected, agus ag tarraingt díosal. */
                            // Cén seans ann go mbeadh device eile ar an líonra?
                            boolean a = (d1_IPv4.compareTo(parser.getString("IPv4")) == 0);
                            if (a) {
                                // Good to go to configuration of settings now.
                                dbg(ui_txt.CONNECTION_SUCCESS);
                                activity = "";
                                feedbackBox.HeightPercent(30);
                                feedbackBox.Text(messages("<b>" + ui_txt.CONNECTION_SUCCESS + ".</b>"));
                                txt_IPv4.TextColor(colors.SUCCESS_GREEN);
                                txt_IPv4.FontBold(true);
                                settings.DEVICE_NAME=txt_DeviceName.Text();
                                settings.localIPv4=txt_IPv4.Text();
                                settings.set();
                                enlargeTable();
                                d1_isConnected=true;
                                btn_device_Configure.Visible(true);
                            } else {
                                // things should get _this_ bad.
                                dbg(d1_IPv4);
                                dbg(parser.getString("IPv4"));
                                dbg(Integer.valueOf(d1_IPv4.compareTo(parser.getString("IPv4"))).toString());
                            }
                        }
                    }
                    else if (c.equals(connection_SensorUnit)) {
                        // if the connection was in write mode we wouldn't get here/
                        // On the off-chance there's another on the network, or data error
                        boolean a = (txt_DeviceName.Text().compareTo(parser.getString("config_DeviceName")) == 0);
                        if (a) {
                                dbg(ui_txt.READ_SUCCESS);
                                activity = "";
                                feedbackBox.Text(messages("<b>" + ui_txt.READ_SUCCESS + ".</b>"));
                                txt_IPv4.TextColor(colors.BLACK);
                                txt_DeviceName.TextColor(colors.SUCCESS_GREEN);
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
                                String[] tmp3 = spin_Active.Elements().toStringArray();
                                // the above is the same as:
                                /* YailList tmp2=spin_Active.Elements();
                                String[] tmp3= tmp2.toStringArray(); */
                                for (int i = 0; i < tmp3.length; i++) {
                                    // for each element
                                    String s = tmp3[i].substring(0, 1);
                                    if (parser.getString("active").equals(s)) {
                                        // this feckin thing starts at 1, not 0
                                        spin_Active.SelectionIndex(i + 1);
                                    }
                                }
                                btn_device_Configure.Text(ui_txt.WRITE_DEVICE);
                                txt_DeviceName.Enabled(false); // changing the device name is a bad idea
                                d1_ModeWrite = !d1_ModeWrite;
                            } else {
                                dbg(parser.getString("config_DeviceName"));
                                dbg(txt_DeviceName.Text());
                            }
                    }
                }
            }
        }
        catch(JSONException e){
                notifier_Messages.ShowMessageDialog(ui_txt.ERR_422, ui_txt.MESSAGE_HEADING_ERROR, ui_txt.BUTTON_OK);
                dbg("Android JSON exception (" + textOfResponse + ")");
                feedbackBox.Text(messages("Android JSONException (" + textOfResponse + ")"));
            }
        else{
                feedbackBox.Text(messages(ui_txt.ERROR_PREFIX + status));
                dbg("Status is " + status);
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
