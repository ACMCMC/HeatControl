package acmc.heatcontrol;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.broadlink.blcloudac.BLCloudAC;
import com.google.gson.JsonObject;

import java.util.List;

import cn.com.broadlink.blnetwork.BLNetwork;

/**
 * Created by acmc on 06/12/2018.
 */

public class App extends Application {

    private static Context mAppContext;

    private List<Device> deviceArrayList;

    @Override
    public void onCreate() {
        super.onCreate();

        mAppContext = getApplicationContext();

        try {
            BroadlinkAPI.getInstance().initNetwork();
        } catch (IllegalStateException e) {
            Log.e(getClass().getSimpleName(),e.getMessage());
        }
    }

    public static Context getAppContext() {
        return mAppContext;
    }

    public List<Device> getDeviceArrayList() {
        return deviceArrayList;
    }

    public void setDeviceArrayList(List<Device> deviceArrayList) {
        this.deviceArrayList = deviceArrayList;
    }
}
