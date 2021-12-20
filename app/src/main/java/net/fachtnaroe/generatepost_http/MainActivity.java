package net.fachtnaroe.generatepost_http;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.fachtnaroe.generatepost_http.GeneralApplicationSettings.EXTERNALLY_STORED_1;

public class MainActivity extends Form implements HandlesEventDispatching {

    private
    VerticalScrollArrangement Screen1;
    StatusBarTools statusBar;
    HorizontalArrangement fiddlyTopBits, horz_ActivityOrRestart;
    TextBox txt_SSID, txt_DeviceName, txt_IPv4, txt_active;
    PasswordTextBox txt_PSK;
    Label lbl_SSID, lbl_PSK, lbl_DeviceName, lbl_IPv4;
    Button btn_device_Configure, btn_device_Find, btn_device_ConnectionTest, testLocalDeviceButton, btn_Home, btn_Restart;
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

    private static final int max_SSID = 32;
    private static final int max_PSK = 64;
    private static final int max_DeviceName = 32;
    private static final String URL_MAIN = EXTERNALLY_STORED_1;
    private static final String default_WIFI_PSK = "";
    private static final String default_WIFI_SSID = "";
    // providing a NAME_DEFAULT_DEVICE saves on testing/debugging time
    private static final String default_DEVICE_NAME ="TCFE-CO2-98-88";
    // UI strings for localisation
    private static final String ui_txt_STATUS_POSSIBLES = "Status\n(0/4/8) ";
    private static final String ui_txt_MAIN_HEAD="Update EEPROM Settings";
    private static final String ui_txt_STATUS_ATTEMPTS_COUNT="Retry\nattempts ";
    private static final String ui_txt_DEVICE_NAME="Device Name";
    private static final String ui_txt_FIND_DEVICE="Find my device";
    private static final String ui_txt_CONNECT_DEVICE="Connect to device";
    private static final String ui_txt_READ_DEVICE="Read Sensor Unit config";
    private static final String ui_txt_WRITE_DEVICE="Write Sensor Unit config";
    private static final String ui_txt_CONNECTION_ATTEMPT="Connection attempt to";
    private static final String ui_txt_CONNECTION_SENDING="Sending";
    private static final String ui_txt_CONNECTION_RECEIVED="Received";
    private static final String ui_txt_CONNECTION_SUCCESS="Successfully connected to unit";
    private static final String ui_txt_CONNECTION_FAILURE="Could not connect to Sensor Unit";
    private static final String ui_txt_CONNECT_BEFORE_REBOOT="Connect to device before attempting reboot";
    private static final String ui_txt_REBOOT_ATTEMPT="Attempting Sensor Unit reboot";
    private static final String ui_txt_REBOOT_NOW="Sensor Unit is now rebooting";
    private static final String ui_txt_READ_SUCCESS="Successfully read from Sensor Unit";
    private static final String ui_txt_WRITE_SUCCESS="Successfully wrote to Sensor Unit";
    private static final String ui_txt_ERR_NOT_IMPLEMENTED="Not Implemented";
    private static final String ui_txt_ERR_422="JSON Error 422";
    private static final String ui_TXT_ERROR_PREFIX="Error status code is ";
    private static final String ui_txt_MESSAGE_HEADING_ERROR="Problem";
    private static final String ui_txt_NEXT_REBOOT_FOR_CHANGES="Your changes will take effect when the Sensor Unit is restarted";
    private static final String ui_txt_MESSAGE_HEADING="Information";
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
//====================================================================================================
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

        horz_ActivityOrRestart=new HorizontalArrangement(Screen1);
        HorizontalArrangement horz1=new HorizontalArrangement(horz_ActivityOrRestart);
        HorizontalArrangement horz2=new HorizontalArrangement(horz_ActivityOrRestart);
        HorizontalArrangement horz3=new HorizontalArrangement(horz_ActivityOrRestart);
        HorizontalArrangement horz4=new HorizontalArrangement(horz_ActivityOrRestart);
        HorizontalArrangement horz5=new HorizontalArrangement(horz_ActivityOrRestart);
        btn_Home=new Button(horz2);
        animatorImage = new Image(horz3);
        btn_Restart=new Button(horz4);

        Label padDivider4 = new Label(Screen1);
        feedbackBox = new Label(Screen1);
        btn_device_Find = new Button(Screen1);
        btn_device_ConnectionTest = new Button(Screen1);
        btn_device_Configure = new Button(Screen1);
        connection_SensorUnit = new Web(Screen1);
        connection_RelayServer = new Web(Screen1);
        connection_TestLocal = new Web(Screen1);
        notifier_Messages = new Notifier(Screen1);
//====================================================================================================
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

        feedbackBox.Width(w);
        feedbackBox.HeightPercent(30);
        feedbackBox.HTMLFormat(true);
        feedbackBox.FontSize(size_FONT_LABELS_TEXT);
        feedbackBox.FontTypeface(font_NUMBER_FIXED);
        feedbackBox.BackgroundColor(color_TEXTBOX_BACKGROUND);

        padDivider4.Height(size_PADDING_HEIGHT);
        btn_device_Find.Text(ui_txt_FIND_DEVICE);
        btn_device_Find.FontSize(size_FONT_LABELS_TEXT);
        btn_device_Find.FontTypeface(font_NUMBER_DEFAULT);
        btn_device_Find.WidthPercent(100);
        btn_device_Find.BackgroundColor(color_BUTTON_BACKGROUND);
        btn_device_Find.TextColor(Component.COLOR_WHITE);
        btn_device_Find.Visible(true);
        btn_device_Find.HeightPercent(8);
        btn_device_ConnectionTest.Text(ui_txt_CONNECT_DEVICE);
        btn_device_ConnectionTest.FontSize(size_FONT_LABELS_TEXT);
        btn_device_ConnectionTest.FontTypeface(font_NUMBER_DEFAULT);
        btn_device_ConnectionTest.WidthPercent(100);
        btn_device_ConnectionTest.BackgroundColor(color_BUTTON_BACKGROUND);
        btn_device_ConnectionTest.TextColor(Component.COLOR_WHITE);
        btn_device_ConnectionTest.Visible(false);
        btn_device_Configure.Text(ui_txt_READ_DEVICE);
        btn_device_Configure.FontSize(size_FONT_LABELS_TEXT);
        btn_device_Configure.FontTypeface(font_NUMBER_DEFAULT);
        btn_device_Configure.WidthPercent(100);
        btn_device_Configure.BackgroundColor(color_BUTTON_BACKGROUND);
        btn_device_Configure.TextColor(Component.COLOR_WHITE);
        btn_device_Configure.Visible(false);

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
        EventDispatcher.registerEventForDelegation(this, formName, "ErrorOccurred"); // for updates
    }

    public boolean dispatchEvent(Component component, String componentName, String eventName, Object[] params) {
        // finally, here is how the events are responded to
        if (!component.equals(animatorClock)) {
            dbg("dispatchEvent: " + formName + " [" + component.toString() + "] [" + componentName + "] " + eventName);
        }
        if (eventName.equals("BackPressed")) {
            // this would be a great place to do something useful
            return true;
        }
        else if (eventName.equals("ErrorOccurred")) {
            Integer tmp_error=Integer.valueOf((Integer)params[2]);
            String tmp_message=(String)(params[3]);
            if (tmp_error.equals(error_NETWORK_GENERAL)) {
                animatorClock.TimerEnabled(false);
                animatorImage.Picture(animatorImage_0);
                if (d1_attemptingReboot) {
                    d1_attemptingReboot=false;
                    d1_isConnected=false;
                    d1_ModeWrite=false;
                    notifier_Messages.ShowMessageDialog(ui_txt_REBOOT_NOW,ui_txt_MESSAGE_HEADING,ui_txt_BUTTON_OK);
                    reduceTable();
                    return true;
                }
                else if(!d1_isConnected) {
                    notifier_Messages.ShowMessageDialog(ui_txt_CONNECTION_FAILURE,ui_txt_MESSAGE_HEADING_ERROR,ui_txt_BUTTON_OK);
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
            notifier_Messages.ShowMessageDialog(ui_txt_URL_TIMED_OUT + url,ui_txt_MESSAGE_HEADING_ERROR,ui_txt_BUTTON_OK);
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
                    notifier_Messages.ShowMessageDialog(ui_txt_CONNECT_BEFORE_REBOOT, ui_txt_MESSAGE_HEADING, ui_txt_BUTTON_OK);
                }
                else {
                    d1_attemptingReboot=true;
                    activity="";
                    feedbackBox.Text(messages("<b>"+ui_txt_REBOOT_ATTEMPT+"</b>"));
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
                feedbackBox.Text( messages("<b>"+ui_txt_CONNECTION_ATTEMPT+"</b> "+ connection_TestLocal.Url()));
                connection_TestLocal.Get();
                return true;
            }
            else if (component.equals(btn_device_Find)) {
                animatorClock.TimerEnabled(true);
                activity = "";
                if (!txt_DeviceName.Text().equals("")) {
                    connection_RelayServer.Url(makeGetString_IPv4());
                    lbl_IPv4.Visible(true);
                    txt_IPv4.Visible(true);
                    feedbackBox.Text(messages("<b>"+ui_txt_CONNECTION_SENDING+"</b> " + makeGetString_IPv4()));
                    connection_RelayServer.Get();
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
                        feedbackBox.Text(messages("<b>"+ui_txt_CONNECTION_SENDING+":</b> " + d1_JSON));
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
    void reduceTable(){
        NetworkSetup.Rows(2);
        NetworkSetup.Columns(3);
        lbl_SSID.Visible(false);
        txt_SSID.Visible(false);
        lbl_PSK.Visible(false);
        txt_PSK.Visible(false);
        fiddlyTopBits.Visible(false);
//        lbl_IPv4.Visible(false);
//        txt_IPv4.Visible(false);
        txt_active.Text("?");
        txt_Status.Text("0");
        txt_Attempts.Text("0");
        btn_device_ConnectionTest.Visible(true);
        btn_device_Configure.Text(ui_txt_READ_DEVICE);
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
                notifier_Messages.ShowMessageDialog(ui_txt_NEXT_REBOOT_FOR_CHANGES, ui_txt_MESSAGE_HEADING, ui_txt_BUTTON_OK);
                btn_device_Configure.Text(ui_txt_READ_DEVICE);
                d1_ModeWrite = !d1_ModeWrite;
                activity="";
                feedbackBox.Text(messages("<b>"+ui_txt_WRITE_SUCCESS+"</b>"));
            }
            else if (c.equals(connection_TestLocal) && (d1_isConnected)) {
                // if we're in write mode, a 200 is success, and all the unit will offer
                notifier_Messages.ShowMessageDialog(ui_txt_REBOOT_NOW, ui_txt_MESSAGE_HEADING, ui_txt_BUTTON_OK);
                d1_ModeWrite = !d1_ModeWrite;
                d1_isConnected = !d1_isConnected;
                activity="";
                feedbackBox.Text(messages("<b>"+ui_txt_WRITE_SUCCESS+"</b>"));
            }

            else {
                feedbackBox.Text(messages("<br><b>" + ui_txt_CONNECTION_RECEIVED + ":</b> " + textOfResponse + "<br>"));
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
                                dbg(ui_txt_CONNECTION_SUCCESS);
                                activity = "";
                                feedbackBox.HeightPercent(30);
                                feedbackBox.Text(messages("<b>" + ui_txt_CONNECTION_SUCCESS + ".</b>"));
                                txt_IPv4.TextColor(color_SUCCESS_GREEN);
                                txt_IPv4.FontBold(true);
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
                                dbg(ui_txt_READ_SUCCESS);
                                activity = "";
                                feedbackBox.Text(messages("<b>" + ui_txt_READ_SUCCESS + ".</b>"));
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
                                    // once we've read, we can now change to write mode
                                }
                                btn_device_Configure.Text(ui_txt_WRITE_DEVICE);
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
                notifier_Messages.ShowMessageDialog(ui_txt_ERR_422, ui_txt_MESSAGE_HEADING_ERROR, ui_txt_BUTTON_OK);
                dbg("Android JSON exception (" + textOfResponse + ")");
                feedbackBox.Text(messages("Android JSONException (" + textOfResponse + ")"));
            }
        else{
                feedbackBox.Text(messages(ui_TXT_ERROR_PREFIX + status));
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
