package net.fachtnaroe.generatepost_http;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;

import com.google.appinventor.components.runtime.Clock;
import com.google.appinventor.components.runtime.Component;
import com.google.appinventor.components.runtime.EventDispatcher;
import com.google.appinventor.components.runtime.Form;
import com.google.appinventor.components.runtime.HandlesEventDispatching;
import com.google.appinventor.components.runtime.HorizontalArrangement;
import com.google.appinventor.components.runtime.Image;
import com.google.appinventor.components.runtime.Label;
import com.google.appinventor.components.runtime.Notifier;
import com.google.appinventor.components.runtime.TableArrangement;
import com.google.appinventor.components.runtime.VerticalArrangement;

public class Splash extends Form implements HandlesEventDispatching {

    private
    VerticalArrangement screen_Splash;
    TableArrangement logoTable;
    StatusBarTools statusBar;

    Integer value_TICKER_SPLASH_DELAY=250;

    Label msg_AllOK;
    Clock ticker;
    ApplicationSettings settings;
    Image img_placeHolder;
    Notifier notifier_Messages;

    protected void $define() {
        /* this next allows the app to use the full screen. In fact,
        seemingly anything makes this work at 100% except "Fixed" and the this.Sizing
        absent in the first place.
         */
        /* Cur seo isteach. Is cuma cén focal atá ann, níl gá leis */
        this.Sizing("Responsive");
        this.BackgroundColor(colors.MAIN_BACKGROUND);
        settings = new ApplicationSettings(this);
//        settings.startingMessageCountdown=10;
        settings.showStartingMessage=true;
        if (!settings.get()) {
            // set blanks, maybe?
        }
        screen_Splash = new VerticalArrangement(this);
        screen_Splash.WidthPercent(100);
        screen_Splash.HeightPercent(100);
        ticker=new Clock(this);
        statusBar=new StatusBarTools(screen_Splash);
        logoTable = new TableArrangement(screen_Splash);
        notifier_Messages=new Notifier(screen_Splash);

        notifier_Messages.BackgroundColor(colors.WHITE);
        notifier_Messages.TextColor(colors.BUTTON_BACKGROUND);
        logoTable.Rows(3);
        logoTable.Columns(3);
//        statusBar.BGTransparentColor("#00000000");
        statusBar.BackgroundColor(colors.withoutTransparencyValue(colors.MAIN_BACKGROUND));
        logoTable.WidthPercent(100);
        logoTable.HeightPercent(100);
        Label top=new Label(logoTable);
        top.BackgroundColor(colors.TRANSPARENT);
        top.HeightPercent(33);
        top.WidthPercent(33);
        top.Row(0);
        top.Column(0);
        HorizontalArrangement bgrnd=new HorizontalArrangement(logoTable);
        bgrnd.WidthPercent(34);
        bgrnd.HeightPercent(34);
        bgrnd.Row(1);
        bgrnd.Column(1);
        bgrnd.BackgroundColor(colors.TRANSPARENT);
        img_placeHolder = new Image(bgrnd);
        img_placeHolder.Picture("PlaceHolderLogo.png");
        img_placeHolder.Width(128);
        img_placeHolder.Height(128);
        Label bot=new Label(logoTable);
        bot.BackgroundColor(colors.TRANSPARENT);
        bot.HeightPercent(33);
        bot.WidthPercent(33);
        bot.Column(2);
        bot.Row(2);

        ticker.TimerInterval(value_TICKER_SPLASH_DELAY);
//        ticker.TimerAlwaysFires(false);
        ticker.TimerEnabled(true);
        // now, the events the components can respond to
        EventDispatcher.registerEventForDelegation(this, formName, "AfterChoosing");
        EventDispatcher.registerEventForDelegation(this, formName, "AfterTextInput");
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
                if (settings.get()) {
                    if (settings.startingMessageCountdown>0) {
                        notifier_Messages.ShowTextDialog(ui_txt.DISCLAIMER, ui_txt.MESSAGE_HEADING, false);
                        return true;
                    }
                    else {
                        if (settings.showStartingMessage) {
                           //                        notifier_Messages.ShowTextDialog(ui_txt.DISCLAIMER,ui_txt.MESSAGE_HEADING,false);
                            notifier_Messages.ShowChooseDialog(ui_txt.DISCLAIMER, ui_txt.MESSAGE_HEADING, ui_txt.BUTTON_OK, ui_txt.NO_SHOW_AGAIN, false);
//                            notifier_Messages.ShowMessageDialog(ui_txt.DISCLAIMER, ui_txt.MESSAGE_HEADING, ui_txt.BUTTON_OK);
                            return true;
                        }
                        else {
                            switchForm("DataDisplay");
                            this.finish();
                            return true;
                        }
                    }
                }
//                switchForm("DataDisplay");
                // turn the timer back on after the event is processed.
//                ticker.TimerEnabled(true);
                // yeah, I turned it off and then back on again. But that's important, as ticks can collide
                return true;
            }
        }
        else if (eventName.equals("AfterTextInput")) {
            settings.startingMessageCountdown--;
            settings.set();
            switchFormWithStartValue("DataDisplay",null);
            this.finish();
            return true;
        }
        else if (eventName.equals("AfterChoosing")) {
            String userChoice=params[0].toString();
            if (userChoice == ui_txt.NO_SHOW_AGAIN) {
                settings.showStartingMessage=false;
                settings.set();
            }
            switchFormWithStartValue("DataDisplay",null);
            this.finish();
            return true;
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

    public void myDownload(String myURL, String title, String year, String branch, String section) {

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(myURL));
        request.setTitle("File Download");
        request.setDescription("Downloading....");

        //request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.allowScanningByMediaScanner();
        //request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        String nameOfFile = URLUtil.guessFileName(myURL, null, MimeTypeMap.getFileExtensionFromUrl(myURL));

        //request.setDestinationInExternalPublicDir(Environment.getExternalStorageDirectory().getPath() + "/KiiTTimeTableData/" + year + "/" + branch + "/" + section + "/", nameOfFile);
        request.setDestinationInExternalPublicDir("//" + section + "/", nameOfFile);
        DownloadManager manager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }
}
// Here be monsters:
