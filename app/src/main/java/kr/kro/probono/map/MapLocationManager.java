package kr.kro.probono.map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Process;

import kr.kro.probono.map.handler.InfCenterPointHandler;
import kr.kro.probono.map.handler.impl.DefaultCenterPointHandler;


public class MapLocationManager extends Activity {

    // this uses for checking my request code
    public static final int REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            requestPermissions(new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_CODE); // request location access permission
        }

    // this method checks permission is granted by user. if permission is denied, application finish itself.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // if my request
        if (requestCode == REQUEST_CODE && grantResults.length > 0) {
            for (int grantResult : grantResults) {
                // if permission isn't granted
                if (PackageManager.PERMISSION_GRANTED != grantResult) {
                    // finish the app
                    moveTaskToBack(true);
                    finishAndRemoveTask();
                    Process.killProcess(Process.myPid());
                    break;
                }
            }
        }
    }




}
