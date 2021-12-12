package net.fachtnaroe.generatepost_http;

import android.util.Log;

import com.google.appinventor.components.runtime.Button;
import com.google.appinventor.components.runtime.CheckBox;
import com.google.appinventor.components.runtime.Component;
import com.google.appinventor.components.runtime.EventDispatcher;
import com.google.appinventor.components.runtime.Form;
import com.google.appinventor.components.runtime.HandlesEventDispatching;
import com.google.appinventor.components.runtime.HorizontalArrangement;
import com.google.appinventor.components.runtime.Label;
import com.google.appinventor.components.runtime.Notifier;
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
    HorizontalArrangement SmallControls;
    TextBox txt_SSID, txt_PSK, txt_DeviceName;
    Label lbl_SSID, lbl_PSK, lbl_DeviceName;
    Button goButton, findDeviceButton;
    Web sensorUnitConnection;
    Web relayServerConnection;
    Label feedbackBox;
    Notifier notifier_Messages;
    CheckBox chk_Active;
    TextBox txt_Status, txt_Attempts;
    ArrayList myHeaders;
    TableArrangement NetworkSetup;
    String activity = "";
    int programProgress = 0;

    private static final int max_SSID = 32;
    private static final int max_PSK = 64;
    private static final int max_DeviceName = 32;
    private static final String URL_MAIN = EXTERNALLY_STORED_1;
    private static final String WIFI_PSK = "password";
    private static final String WIFI_SSID = "someSSID";
    /* Tá ná dáthanna déanta mar an gcéanna le HTML, ach le FF ar
    dtús air. Sin uimhir ó 0-FF ar cé comh tréshoilseacht an rud.
    Cur 0x ós comhair sin chun stad-riamh-fhocail i Hexadecimal a dhénamh agus sabháil */
    /* How to use HTML for the colours */
    private static final int BACKGROUND_COLOR = 0xF085abc1;
    private static final int BUTTON_COLOR = 0xFF103449;
    private static final int SECTION_TOP_COLOR = 0xFF000000;
    private static final int SECTION_BG_COLOR = 0xFFdbdef3;
    private static final int TEXTBOX_COLOR = 0xFF000000;
    private static final int TEXTBOX_BACKGROUND_COLOR = 0xFFdbdde6;
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
        Form a = this;
        Integer w = a.$form().Width();
        Integer h = a.$form().Height();
        Screen1 = new VerticalScrollArrangement(this);
        Screen1.Width(w);
        Screen1.Height(h);
        Screen1.AlignHorizontal(Component.ALIGNMENT_NORMAL);
        Screen1.AlignVertical(Component.ALIGNMENT_CENTER);
        Screen1.BackgroundColor(BACKGROUND_COLOR);

        Label heading = new Label(Screen1);
        heading.Width(w);
        heading.Height(SIZE_TOP_BAR);
        heading.Text("<b>Update EEPROM Settings</b>");
        heading.TextAlignment(Component.ALIGNMENT_CENTER);
        heading.FontSize(SIZE_LABELS_TXT + 5);
        heading.HTMLFormat(true);

        SmallControls = new HorizontalArrangement(Screen1);
        SmallControls.Visible(false);
        SmallControls.Width(w);
        SmallControls.Height(SIZE_TOP_BAR);
        SmallControls.AlignVertical(Component.ALIGNMENT_CENTER);
        SmallControls.BackgroundColor(BUTTON_COLOR);

        Label lblActive = new Label(SmallControls);
        lblActive.Text("Is active ");
        lblActive.FontTypeface(FONT_NUMBER);
        lblActive.FontSize(SIZE_SMALL_LABELS_TXT);
        lblActive.TextColor(Component.COLOR_WHITE);
        lblActive.HeightPercent(100);

        chk_Active = new CheckBox(SmallControls);
        chk_Active.BackgroundColor(TEXTBOX_BACKGROUND_COLOR);
        chk_Active.Height(SIZE_TOP_BAR);

        Label lblStatus = new Label(SmallControls);
        lblStatus.Text("Status (0/2/4) ");
        lblStatus.FontTypeface(FONT_NUMBER);
        lblStatus.FontSize(SIZE_SMALL_LABELS_TXT);
        lblStatus.TextColor(Component.COLOR_WHITE);
        lblStatus.Height(SIZE_TOP_BAR);
        txt_Status = new TextBox(SmallControls);
        txt_Status.WidthPercent(10);
        txt_Status.BackgroundColor(TEXTBOX_BACKGROUND_COLOR);
        txt_Status.Height(SIZE_TOP_BAR);
        txt_Status.TextColor(TEXTBOX_COLOR);
        txt_Status.TextAlignment(Component.ALIGNMENT_CENTER);
        txt_Status.Text("0");
        txt_Status.NumbersOnly(true);
        txt_Status.FontSize(SIZE_LABELS_TXT);

        Label lblAttempts = new Label(SmallControls);
        lblAttempts.Text("Retry attempts ");
        lblAttempts.FontTypeface(FONT_NUMBER);
        lblAttempts.FontSize(SIZE_SMALL_LABELS_TXT);
        lblAttempts.TextColor(Component.COLOR_WHITE);
        lblAttempts.HeightPercent(100);

        txt_Attempts = new TextBox(SmallControls);
        txt_Attempts.WidthPercent(10);
        txt_Attempts.BackgroundColor(TEXTBOX_BACKGROUND_COLOR);
        txt_Attempts.Height(SIZE_TOP_BAR);
        txt_Attempts.TextColor(TEXTBOX_COLOR);
        txt_Attempts.TextAlignment(Component.ALIGNMENT_CENTER);
        txt_Attempts.Text("0");
        txt_Attempts.NumbersOnly(true);
        txt_Attempts.FontSize(SIZE_LABELS_TXT);

        Label padDivider1 = new Label(Screen1);
        padDivider1.Height(PAD_DIVIDER_HEIGHT);

        NetworkSetup = new TableArrangement(Screen1);
        NetworkSetup.Rows(2);
        NetworkSetup.Columns(2);
        NetworkSetup.Width(w);

        lbl_DeviceName = new Label(NetworkSetup);
        lbl_DeviceName.Row(1);
        lbl_DeviceName.Column(0);
        lbl_DeviceName.FontSize(SIZE_LABELS_TXT);
        lbl_DeviceName.Text("Device Name");
        lbl_DeviceName.TextAlignment(Component.ALIGNMENT_OPPOSITE);
        lbl_DeviceName.FontTypeface(FONT_NUMBER);
        txt_DeviceName = new TextBox(NetworkSetup);
        txt_DeviceName.FontSize(SIZE_LABELS_TXT);
        txt_DeviceName.Row(1);
        txt_DeviceName.Column(1);
        txt_DeviceName.TextAlignment(Component.ALIGNMENT_NORMAL);
        txt_DeviceName.BackgroundColor(TEXTBOX_BACKGROUND_COLOR);
        txt_DeviceName.FontTypeface(FONT_NUMBER_FIXED);
        txt_DeviceName.Text("TCFE-CO2-12-34");

        Label padMiddle = new Label(NetworkSetup);
        padMiddle.Row(0);
        padMiddle.Column(1);
        padMiddle.WidthPercent(1);

        Label padDivider3 = new Label(Screen1);
        padDivider3.Text("Activity");
        padDivider3.WidthPercent(100);
        padDivider3.TextAlignment(Component.ALIGNMENT_CENTER);
        padDivider3.FontTypeface(FONT_NUMBER);
        padDivider3.FontSize(SIZE_LABELS_TXT);
        feedbackBox = new Label(Screen1);
        feedbackBox.Width(w);
        feedbackBox.HeightPercent(50);
        feedbackBox.HTMLFormat(true);
        feedbackBox.FontSize(SIZE_LABELS_TXT);
        feedbackBox.FontTypeface(FONT_NUMBER_FIXED);
        feedbackBox.BackgroundColor(TEXTBOX_BACKGROUND_COLOR);
        Label padDivider4 = new Label(Screen1);
        padDivider4.Height(PAD_DIVIDER_HEIGHT);

        findDeviceButton = new Button(Screen1);
        findDeviceButton.Text("Press to update");
        findDeviceButton.FontSize(SIZE_LABELS_TXT);
        findDeviceButton.FontTypeface(FONT_NUMBER);
        findDeviceButton.WidthPercent(100);
        findDeviceButton.BackgroundColor(BUTTON_COLOR);
        findDeviceButton.TextColor(Component.COLOR_WHITE);

        goButton = new Button(Screen1);
        goButton.Text("Press to update");
        goButton.FontSize(SIZE_LABELS_TXT);
        goButton.FontTypeface(FONT_NUMBER);
        goButton.WidthPercent(100);
        goButton.BackgroundColor(BUTTON_COLOR);
        goButton.TextColor(Component.COLOR_WHITE);
        goButton.Visible(false);

        sensorUnitConnection = new Web(Screen1);
        relayServerConnection = new Web(Screen1);
        notifier_Messages = new Notifier(Screen1);

        EventDispatcher.registerEventForDelegation(this, formName, "Click");
        EventDispatcher.registerEventForDelegation(this, formName, "GotText");
        EventDispatcher.registerEventForDelegation(this, formName, "LostFocus");
    }

    public boolean dispatchEvent(Component component, String componentName, String eventName, Object[] params) {

        System.err.print("dispatchEvent: " + formName + " [" + component.toString() + "] [" + componentName + "] " + eventName);
        if (eventName.equals("BackPressed")) {
            // this would be a great place to do something useful
            return true;
        } else if (eventName.equals("LostFocus")) {
            if (component.equals(txt_Status)) {
                int temp = Integer.valueOf(txt_Attempts.Text());
                if ((temp != d1_OUTOFBOX) && (temp != d1_ATTEMPTING) && (temp != d1_CONFIGURED)) {
                    txt_Status.Text("0");
                }
            } else if (component.equals(txt_Attempts)) {
                int temp = Integer.valueOf(txt_Attempts.Text());
                if ((temp < d1_MINATTEMPTS) || (temp > d1_MAXATTEMPTS)) {
                    txt_Attempts.Text("1");
                }
            }
            return true;
        } else if (eventName.equals("GotText")) {
            if (component.equals(relayServerConnection)) {

            } else if (component.equals(sensorUnitConnection)) {
                String status = params[1].toString();
                String textOfResponse = (String) params[3];
                handler_Response(component, status, textOfResponse);
                return true;
            }
        } else if (eventName.equals("Click")) {
            if (component.equals(findDeviceButton)) {
                activity = "";
                if (!txt_DeviceName.Text().equals("")) {
                    relayServerConnection.Url(URL_MAIN);
                    relayServerConnection.Get();
                    feedbackBox.Text(messages("<b>To:</b> " + sensorUnitConnection.Url()));
                    feedbackBox.Text(messages("<br>\n<b>Sending:</b> " ));
                }
                return true;
            } else if (component.equals(goButton)) {
                activity = "";
                d1_Data = makeConfig();
                if (config2JSON(d1_Data)) {
                    sensorUnitConnection.Url(URL_MAIN);
                    sensorUnitConnection.PostText(d1_JSON.toString());
                    sensorUnitConnection.RequestHeaders(myHeaders());
                    feedbackBox.Text(messages("<b>To:</b> " + sensorUnitConnection.Url()));
                    feedbackBox.Text(messages("<br>\n<b>Sending:</b> " + d1_JSON));
                }
                return true;
            }
        }
        return false;
    }
    String makeGetString_IPv4(){
        String test1 = "POSTDATA={\"device\":\"";
               test1+= txt_DeviceName.Text();
               test1+="\",";
               test1+="\"sensor\":\"IPv4\"}";
        return test1;
    }

    eepromStruct makeConfig(){
        d1_Data=new eepromStruct();
        if (chk_Active.Checked()) {
            d1_Data.active = 'Y';
        }
        else {
            d1_Data.active='N';
        }
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
        lbl_SSID.Visible(false);
        lbl_SSID.Row(2);
        lbl_SSID.Column(0);
        lbl_SSID.WidthPercent(35);
        lbl_SSID.TextAlignment(Component.ALIGNMENT_OPPOSITE);
        lbl_SSID.FontSize(SIZE_LABELS_TXT);
        lbl_SSID.FontTypeface(FONT_NUMBER);
        txt_SSID =new TextBox(NetworkSetup);
        txt_SSID.Visible(false);
        txt_SSID.FontSize(SIZE_LABELS_TXT);
        txt_SSID.Row(2);
        txt_SSID.Column(2);
        txt_SSID.WidthPercent(60);
        txt_SSID.TextAlignment(Component.ALIGNMENT_NORMAL);
        txt_SSID.FontTypeface(FONT_NUMBER_FIXED);
        txt_SSID.BackgroundColor(TEXTBOX_BACKGROUND_COLOR);
        txt_SSID.Text(WIFI_SSID);

        lbl_PSK = new Label (NetworkSetup);
        lbl_PSK.Row(2);
        lbl_PSK.Column(0);
        lbl_PSK.TextAlignment(Component.ALIGNMENT_OPPOSITE);
        lbl_PSK.FontSize(SIZE_LABELS_TXT);
        lbl_PSK.Text("PSK");
        lbl_PSK.FontTypeface(FONT_NUMBER);
        txt_PSK =new TextBox(NetworkSetup);
        txt_PSK.FontSize(SIZE_LABELS_TXT);
        txt_PSK.Row(2);
        txt_PSK.Column(2);
        txt_PSK.TextAlignment(Component.ALIGNMENT_NORMAL);
        txt_PSK.BackgroundColor(TEXTBOX_BACKGROUND_COLOR);
        txt_PSK.FontTypeface(FONT_NUMBER_FIXED);
        txt_PSK.Text(WIFI_PSK);
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
        if (status.equals("200") ) try {
            JSONObject parser = new JSONObject(textOfResponse);
                feedbackBox.Text(
                        messages( "<br><b>Received:</b> " +  textOfResponse)
                );
        }
        catch (JSONException e) {
            notifier_Messages.ShowAlert("android JSON exception (" + textOfResponse + ")");
            dbg("android JSON exception (" + textOfResponse + ")");
        }
        else {
            dbg("Status is "+status);
        }
    }
    public static void dbg (String S) {
        Log.e("AppUnderConstruction","\n-----> ["+S+"]\n");
    }
}