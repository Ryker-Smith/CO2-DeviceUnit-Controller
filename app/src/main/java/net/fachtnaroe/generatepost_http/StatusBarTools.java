/* Original source here: 
https://community.appinventor.mit.edu/t/statusbartools-extension-free/19568
Author: Salman Dev
Was:
package com.SalmanDev.StatusBarTools;
*/
package net.fachtnaroe.generatepost_http;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.ComponentContainer;

@DesignerComponent(version = 1, description = "Created by Salman Developer", category = ComponentCategory.EXTENSION, nonVisible = true, iconName = "https://img.icons8.com/fluent/16/000000/source-code.png")
@SimpleObject(external = true)

public class StatusBarTools extends AndroidNonvisibleComponent {
  private static final String LOG_TAG = "StatusBarTools";
  private ComponentContainer container;
  private static Context context;
  private final Activity activity;
  private boolean statusbarVisible;
  private boolean checkTpIcon = true;
  
  public StatusBarTools(ComponentContainer container) {
    super(container.$form());
    this.container = container;
    context = (Context)container.$context();
    this.activity = container.$context();
  }
  
  @SimpleFunction(description = "This block is useful for changing the StatusBar background color")
  public void BackgroundColor(String hexColor) {
    this.checkTpIcon = true;
    
    if (Build.VERSION.SDK_INT > 19) {
      Window w = this.activity.getWindow();
      w.clearFlags(67108864);
      w.addFlags(-2147483648);
      w.setStatusBarColor(Color.parseColor(hexColor));
    } else {
      Log.w("StatusBarTools", "Sorry, set StatusBar Color is not available for API Level < 21");
      return;
    } 
  }
  
  @SimpleFunction(description = "This block is useful for changing the StatusBar background to Transparent")
  public void BackgroundTransparent() {
    this.checkTpIcon = true;
    
    if (Build.VERSION.SDK_INT >= 21) {
      Window w = this.activity.getWindow();
      w.clearFlags(67108864);
      w.addFlags(-2147483648);
      w.setStatusBarColor(-16743563);
      w.setFlags(512, 512);
    } else {
      Log.w("StatusBarTools", "Sorry, set StatusBar Transparent is not available for API Level < 21");
      return;
    } 
  }
  
  @SimpleFunction(description = "This block is useful for changing the StatusBar background to Transparent with color")
  public void BGTransparentColor(String hexColor) {
    this.checkTpIcon = false;
    
    if (Build.VERSION.SDK_INT > 19) {
      View v = this.activity.getWindow().getDecorView();
      v.setSystemUiVisibility(1280);
      
      Window w = this.activity.getWindow();
      w.clearFlags(67108864);
      w.addFlags(-2147483648);
      w.setStatusBarColor(Color.parseColor(hexColor));
    } else {
      Log.w("StatusBarTools", "Sorry, set StatusBar Transparent Color is not available for API Level < 21");
      return;
    } 
  }
  
  @SimpleFunction(description = "This block is useful for changing the Icon Color in the StatusBar to Dark")
  public void IconDark() {
    if (Build.VERSION.SDK_INT >= 23) {
      if (this.checkTpIcon) {
        View w = this.activity.getWindow().getDecorView().getRootView();
        w.setSystemUiVisibility(8192);
      } else {
        View v = this.activity.getWindow().getDecorView();
        v.setSystemUiVisibility(9472);
      } 
    } else {
      Log.w("StatusBarTools", "Sorry, set StatusBar Icon to dark is not available for API Level < 23");
      return;
    } 
  }
  
  @SimpleFunction(description = "This block is useful for changing the Icon Color in the StatusBar to Light")
  public void IconLight() {
    if (Build.VERSION.SDK_INT >= 23) {
      if (this.checkTpIcon) {
        View w = this.activity.getWindow().getDecorView().getRootView();
        w.setSystemUiVisibility(0);
      } else {
        View v = this.activity.getWindow().getDecorView();
        v.setSystemUiVisibility(1280);
      } 
    } else {
      Log.w("StatusBarTools", "Sorry, set StatusBar Icon to light is not available for API Level < 23");
      return;
    } 
  }
}
/* Location:              /home/fachtna/mobile-data-store/Activities/android-studio-projects/GeneratePOST_ToDeviceUnit_3.0/tmp/com.SalmanDev.StatusBarTools/files/AndroidRuntime.jar!/com/SalmanDev/StatusBarTools/StatusBarTools.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
