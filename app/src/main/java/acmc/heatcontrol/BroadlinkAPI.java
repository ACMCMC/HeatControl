package acmc.heatcontrol;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by acmc on 10/12/2018.
 */

public class BroadlinkAPI {

    public static String mLicenseValue = "nADnSUtobAEYLWxa7WIf/ew+nmvTfFWY5rFYTb0upuz/mfVgXAjM1azI5W7SxPYpZyH/0T3Bqqz1YqA0OQZLs5LxUNNSbg9qY9L0iQaqF7s073HJKbc=";
    public static cn.com.broadlink.blnetwork.BLNetwork BLNetwork;
    public static com.broadlink.blcloudac.BLCloudAC BLCloudAC;

    private static BroadlinkAPI instance = null;

    protected BroadlinkAPI() {
        Context context = App.getAppContext();

        try {
            BLNetwork = BLNetwork.getInstanceBLNetwork(context);
            BLCloudAC = BLCloudAC.getInstance();
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "Check app permissions");
            Log.e(this.getClass().getSimpleName(), "" + e);
        }
    }

    public static BroadlinkAPI getInstance() {
        if (instance == null) {
            instance = new BroadlinkAPI();
        }
        return instance;
    }

    public String executeCommand(int api_id, String command) {
        String out;
        JsonObject params = broadlinkParams(api_id, command);

        if (BLNetwork == null) {
            Log.e(getClass().getSimpleName(), "mBlNetwork is uninitialized, check app permissions");
            return null;
        }

        out = BLNetwork.requestDispatch(params.toString());
        System.out.println(out);

        return out;
    }

    public String executeCommand(JsonObject Json) {
        String out;

        if (BLNetwork == null) {
            Log.e(getClass().getSimpleName(), "BLNetwork is uninitialized");
            throw new java.lang.IllegalStateException("BLNetwork is uninitialized");
        }

        if (!Json.has(BroadlinkConstants.LICENSE)) {
            Json.addProperty(BroadlinkConstants.LICENSE, mLicenseValue);
        }

        out = BLNetwork.requestDispatch(Json.toString());
        System.out.println(out);

        return out;
    }

    public String executeCommand(JsonObject Json, int api_id, String command) {
        String out;

        if (BLNetwork == null) {
            Log.e(getClass().getSimpleName(), "BLNetwork is uninitialized");
            throw new java.lang.IllegalStateException("BLNetwork is uninitialized");
        }

        if (!Json.has(BroadlinkConstants.LICENSE)) {
            Json.addProperty(BroadlinkConstants.LICENSE, mLicenseValue);
        }

        Json.addProperty(BroadlinkConstants.API_ID, api_id);
        Json.addProperty(BroadlinkConstants.COMMAND, command);

        out = BLNetwork.requestDispatch(Json.toString());
        System.out.println(out);

        return out;
    }

    private JsonObject broadlinkParams(int api_id, String command) {
        JsonObject JsonObjectParams = new JsonObject();
        JsonObjectParams.addProperty(BroadlinkConstants.API_ID, api_id);
        JsonObjectParams.addProperty(BroadlinkConstants.COMMAND, command);
        JsonObjectParams.addProperty(BroadlinkConstants.LICENSE, mLicenseValue);
        return JsonObjectParams;
    }

    String initNetwork() throws IllegalStateException {
        JsonObject JsonObjectParams = new JsonObject();
        JsonObjectParams.addProperty(BroadlinkConstants.API_ID, BroadlinkConstants.CMD_NETWORK_INIT_ID);
        JsonObjectParams.addProperty(BroadlinkConstants.COMMAND, BroadlinkConstants.CMD_NETWORK_INIT);
        JsonObjectParams.addProperty(BroadlinkConstants.LICENSE, mLicenseValue);
        JsonObjectParams.addProperty("type_license", "a3X4Zc6aJ5bgFvRn78ky7aY7/jOIXdSl5U2gIWdvfPMXy7gqfqxX0+oL8u8gbwv0");
        JsonObjectParams.addProperty("main_udp_ser", "141main.broadlink.com.cn");
        JsonObjectParams.addProperty("backup_udp_ser", "141backup.broadlink.com.cn");
        JsonObjectParams.addProperty("main_tcp_ser", "141tcp.broadlink.com.cn");
        JsonObjectParams.addProperty("main_udp_port", 16384);
        JsonObjectParams.addProperty("backup_udp_port", 1812);
        JsonObjectParams.addProperty("main_tcp_port", 80);

        if (BLNetwork == null) {
            Log.e(getClass().getSimpleName(), "BLNetwork is uninitialized");
            throw new java.lang.IllegalStateException("BLNetwork is uninitialized");
        }

        String out;
        out = BLNetwork.requestDispatch(JsonObjectParams.toString());
        System.out.println(out);

        return out;
    }

    List<Device> updateDevices() throws IllegalStateException {
        List<Device> deviceArray = new ArrayList<>();

        if (BLNetwork == null) {
            Log.e(getClass().getSimpleName(), "BLNetwork is uninitialized");
            throw new java.lang.IllegalStateException("BLNetwork is uninitialized");
        }

        String stringResult = executeCommand(BroadlinkConstants.CMD_PROBE_LIST_ID,BroadlinkConstants.CMD_PROBE_LIST);

        JsonObject JsonResult = new JsonParser().parse(stringResult).getAsJsonObject();

        JsonArray deviceJsonArray = JsonResult.get("list").getAsJsonArray();

        //Se puede implementar el deserializer por Gson.
        //Gson deserializer = new Gson();

        for (int i = 0; i < deviceJsonArray.size() ; i++) {
            JsonObject dev = new JsonParser().parse(deviceJsonArray.get(i).toString()).getAsJsonObject();
            deviceArray.add(new Device(
                    dev.get(BroadlinkConstants.PARAM_MAC).getAsString(),
                    dev.get(BroadlinkConstants.PARAM_TYPE).getAsString(),
                    dev.get(BroadlinkConstants.PARAM_NAME).getAsString(),
                    dev.get(BroadlinkConstants.PARAM_LOCK).getAsString(),
                    dev.get(BroadlinkConstants.PARAM_PASSWORD).getAsString(),
                    dev.get(BroadlinkConstants.PARAM_ID).getAsString(),
                    dev.get(BroadlinkConstants.PARAM_SUBDEVICE).getAsString(),
                    dev.get(BroadlinkConstants.PARAM_KEY).getAsString()));
        }

        return deviceArray;
    }
}
