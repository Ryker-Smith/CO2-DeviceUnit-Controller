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
import com.google.appinventor.components.runtime.VerticalScrollArrangement;
import com.google.appinventor.components.runtime.Web;
import com.google.appinventor.components.runtime.WebViewer;

import org.json.JSONException;
import org.json.JSONObject;

public class DataDisplay extends Form implements HandlesEventDispatching {

    private
    static final Integer value_TICKER_INTERVAL=60000;
    VerticalScrollArrangement screen_DataDisplay;
    StatusBarTools statusBar;
    Web connection_toSomewhere;
    Button btn_doesSomething;
    Label msg_AllOK;
    arduino_eeprom_data d1_Data=new arduino_eeprom_data();
    JSONObject d1_JSON=new JSONObject();
    Clock ticker=new Clock(this);
    Notifier notifier_Messages;
    Button btn_Configuration;
    WebViewer webview_DataDisplay;
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
            notifier_Messages.ShowMessageDialog(ui_txt.CONFIGURATION_REQUIRED,ui_txt.MESSAGE_HEADING,ui_txt.BUTTON_OK);
            switchFormWithStartValue("SensorUnitConfiguration",null);
        }

        // each component, listed in order
        screen_DataDisplay = new VerticalScrollArrangement(this);
        statusBar=new StatusBarTools(screen_DataDisplay);
        HorizontalArrangement top = new HorizontalArrangement(screen_DataDisplay);
        btn_Configuration =new Button(top);
        Label pad1=new Label(top);
        HorizontalArrangement pad2=new HorizontalArrangement(screen_DataDisplay);
        webview_DataDisplay = new WebViewer(screen_DataDisplay);
        connection_toSomewhere=new Web(screen_DataDisplay);
        // now, how every component looks:
        statusBar.BackgroundColor(colors.withoutTransparencyValue(colors.MAIN_BACKGROUND));
        screen_DataDisplay.WidthPercent(100);
        screen_DataDisplay.HeightPercent(100);
        screen_DataDisplay.AlignHorizontal(Component.ALIGNMENT_NORMAL);
        screen_DataDisplay.AlignVertical(Component.ALIGNMENT_CENTER);
        screen_DataDisplay.BackgroundColor(colors.MAIN_BACKGROUND);
        top.WidthPercent(100);
        top.Height(32);
        top.AlignHorizontal(Component.ALIGNMENT_OPPOSITE);
        btn_Configuration.Image("image_Cogs.png");
        btn_Configuration.Width(32);
        btn_Configuration.Width(32);
        pad1.Width(16);
        pad2.Height(32);
        webview_DataDisplay.WidthPercent(100);
        webview_DataDisplay.HeightPercent(80);
        webview_DataDisplay.GoToUrl("file:///android_asset/empty.html");

                //GoToUrl(getAssets().open("empty.html"));
        msg_AllOK=new Label(screen_DataDisplay);
        msg_AllOK.WidthPercent(100);
        msg_AllOK.TextColor(colors.MAIN_TEXT);
        msg_AllOK.HTMLFormat(true);
        String s=settings.buildNumber;
        msg_AllOK.Text("<h2 style='text-align: center;'>CO<sub>2</sub> Sensor Unit monitor; build #"+s+"</h2>");
dbg(settings.buildNumber);
        // now, the events the components can respond to
        EventDispatcher.registerEventForDelegation(this, formName, "Click");
        EventDispatcher.registerEventForDelegation(this, formName, "GotText");
        EventDispatcher.registerEventForDelegation(this, formName, "AfterSelecting");
        EventDispatcher.registerEventForDelegation(this, formName, "TimedOut"); // for network
        EventDispatcher.registerEventForDelegation(this, formName, "Timer"); // for updates
        EventDispatcher.registerEventForDelegation(this, formName, "OtherScreenClosed"); // for updates
        ticker.TimerInterval(value_TICKER_INTERVAL);
        ticker.TimerEnabled(true);
    }

    @Override
    public boolean dispatchEvent(Component component, String componentName, String eventName, Object[] params) {
        // finally, here is how the events are responded to
        dbg("dispatchEvent: " + formName + " [" +component.toString() + "] [" + componentName + "] " + eventName);
        if (eventName.equals("OtherScreenClosed")) {
            // when the settings screen closes, re-read the settings db
            settings.get();
            return true;
        }

        else if (eventName.equals("BackPressed")) {
            // this would be a great place to do something useful, if not
            // then we've captured the BackPress operation(?) to ignore it

            return true;
        }
        else if (eventName.equals("Timer")) {
            if (component.equals(ticker)) {
                // turn off the timer while the event is being processed
                ticker.TimerEnabled(false);
                connection_toSomewhere.Url(settings.URL_MAIN+settings.makeGetString("CO2"));
                // turn the timer back on after the event is processed.
                ticker.TimerEnabled(true);
                return true;
            }
        }
        else if (eventName.equals("TimedOut")) {
            // do something
            return true;
        }
        else if (eventName.equals("GotText")) {
            if (component.equals(connection_toSomewhere)) {
                String status = params[1].toString();
                String textOfResponse = (String) params[3];
                handleNetworkResponse(component, status, textOfResponse);
                return true;
            }
        }
        else if (eventName.equals("Click")) {
            if (component.equals(btn_doesSomething)) {
                return true;
            }
            else if (component.equals(btn_Configuration)) {
                switchFormWithStartValue("SensorUnitConfiguration",null);
                return true;
            }
        }
        return false;
    }

    void handleNetworkResponse(Component c, String status, String textOfResponse) {
        dbg(("<br><b>" + "some message here" + ":</b> " + textOfResponse + "<br>"));
        if (status.equals("200")) try {
            JSONObject parser = new JSONObject(textOfResponse);
            if (parser.getString("Status").equals("OK")) {
                if (c.equals(connection_toSomewhere)) {
                }
            }
        }
        catch(JSONException e){
            dbg("Android JSON exception (" + textOfResponse + ")");
        }
        else{
            dbg("Status is " + status);
        }
    }

    public static void dbg (String debugMsg) {
        System.err.println( "~~~> " + debugMsg + " <~~~\n");
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
