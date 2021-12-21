package net.fachtnaroe.generatepost_http;

import com.google.appinventor.components.runtime.Button;
import com.google.appinventor.components.runtime.Clock;
import com.google.appinventor.components.runtime.Component;
import com.google.appinventor.components.runtime.EventDispatcher;
import com.google.appinventor.components.runtime.Form;
import com.google.appinventor.components.runtime.HandlesEventDispatching;
import com.google.appinventor.components.runtime.Label;
import com.google.appinventor.components.runtime.VerticalArrangement;
import com.google.appinventor.components.runtime.VerticalScrollArrangement;
import com.google.appinventor.components.runtime.Web;

import org.json.JSONException;
import org.json.JSONObject;

public class Splash extends Form implements HandlesEventDispatching {

    private
    VerticalArrangement Screen1;
    StatusBarTools statusBar;

    Label msg_AllOK;

    static final int color_MAIN_BACKGROUND=0xFF940558; // dark red
    static final int color_MAIN_TEXT=0xFFFFFFFF; // white

    Clock ticker=new Clock(this);

    protected void $define() {
        /* this next allows the app to use the full screen. In fact,
        seemingly anything makes this work at 100% except "Fixed" and the this.Sizing
        absent in the first place.
         */
        /* Cur seo isteach. Is cuma cén focal atá ann, níl gá leis */
        this.Sizing("Responsive");
        this.BackgroundColor(color_MAIN_BACKGROUND);
        Screen1 = new VerticalArrangement(this);
        // each component, listed in order
        statusBar=new StatusBarTools(Screen1);
        ticker = new Clock(this);
        ticker.TimerEnabled(false);

        // now, how every component looks:
        statusBar.BGTransparentColor("#00000000");
        statusBar.BackgroundColor("#00000000");
        Screen1.WidthPercent(100);
        Screen1.HeightPercent(100);
        Screen1.Image("tie-dyed-hippy-shite.png");

        Screen1.AlignHorizontal(Component.ALIGNMENT_NORMAL);
        Screen1.AlignVertical(Component.ALIGNMENT_CENTER);
        Screen1.BackgroundColor(color_MAIN_BACKGROUND);

        msg_AllOK=new Label(Screen1);
        msg_AllOK.WidthPercent(100);
        msg_AllOK.TextColor(color_MAIN_TEXT);
        msg_AllOK.HTMLFormat(true);

        ticker.TimerInterval(2000);
        ticker.TimerEnabled(true);
        // now, the events the components can respond to
        EventDispatcher.registerEventForDelegation(this, formName, "Click");
        EventDispatcher.registerEventForDelegation(this, formName, "Timer"); // for updates
    }

    public boolean dispatchEvent(Component component, String componentName, String eventName, Object[] params) {
        // finally, here is how the events are responded to
        dbg("dispatchEvent: " + formName + " [" +component.toString() + "] [" + componentName + "] " + eventName);
        if (eventName.equals("Timer")) {
            if (component.equals(ticker)) {
                // turn off the timer while the event is being processed
                ticker.TimerEnabled(false);
                // process whatever the timer is for ...
                switchForm("DataDisplay");
                // turn the timer back on after the event is processed.
//                ticker.TimerEnabled(true);
                // yeah, I turned it off and then back on again. But that's important, as ticks can collide
                return true;
            }
        }
        else if (eventName.equals("Click")) {
        }
        return false;
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
