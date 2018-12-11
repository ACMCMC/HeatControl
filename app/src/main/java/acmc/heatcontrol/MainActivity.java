package acmc.heatcontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import cn.com.broadlink.blnetwork.BLNetwork;

public class MainActivity extends AppCompatActivity {

    BLNetwork mBLNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final BroadlinkAPI mBroadlinkAPI = BroadlinkAPI.getInstance();

        App mApp = (App) getApplicationContext();

        mApp.setDeviceArrayList(mBroadlinkAPI.updateDevices());

        Button alternarBoton = (Button) findViewById(R.id.alternarBoton);

        TextView nombreTextView = (TextView) findViewById(R.id.nombreTextView);
        nombreTextView.setText(mApp.getDeviceArrayList().get(0).getName());

        alternarBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonObject request = new JsonObject();
                request.addProperty(BroadlinkConstants.PARAM_MAC,((App) App.getAppContext()).getDeviceArrayList().get(0).getMac());
                if (((App) App.getAppContext()).getDeviceArrayList().get(0).getLock()) {
                    request.addProperty("lock","0");
                } else {
                    request.addProperty("lock","1");
                }
                mBroadlinkAPI.executeCommand(request,BroadlinkConstants.CMD_DEVICE_UPDATE_ID,BroadlinkConstants.CMD_DEVICE_UPDATE);
            }
        });
    }
}
