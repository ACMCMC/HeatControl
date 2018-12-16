package acmc.heatcontrol;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonObject;

import cn.com.broadlink.blnetwork.BLNetwork;

public class MainActivity extends AppCompatActivity {

    BLNetwork mBLNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final BroadlinkAPI mBroadlinkAPI = BroadlinkAPI.getInstance();

        App mApp = (App) getApplicationContext();

        mApp.setDeviceArrayList(mBroadlinkAPI.updateDevices());

        Intent intent = new Intent(this,DeviceListActivity.class);
        startActivity(intent);
    }
}
