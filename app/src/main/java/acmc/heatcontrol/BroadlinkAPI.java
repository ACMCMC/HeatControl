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

    public String executeCommand(int api_id, String command, String mac) {
        String out;
        JsonObject params = broadlinkParams(api_id, command);
        params.addProperty(BroadlinkConstants.PARAM_MAC, mac);

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

        String stringResult = executeCommand(BroadlinkConstants.CMD_PROBE_LIST_ID, BroadlinkConstants.CMD_PROBE_LIST);

        JsonObject JsonResult = new JsonParser().parse(stringResult).getAsJsonObject();

        JsonArray deviceJsonArray = JsonResult.get("list").getAsJsonArray();

        //Se puede implementar el deserializer por Gson.
        //Gson deserializer = new Gson();

        for (int i = 0; i < deviceJsonArray.size(); i++) {
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


    public static int Modbus_CRC16(byte[] data, int len) {

        char[] Low_order_byte_table = {0x00, 0xC0, 0xC1, 0x01, 0xC3, 0x03, 0x02, 0xC2, 0xC6, 0x06, 0x07, 0xC7, 0x05, 0xC5, 0xC4,
                0x04, 0xCC, 0x0C, 0x0D, 0xCD, 0x0F, 0xCF, 0xCE, 0x0E, 0x0A, 0xCA, 0xCB, 0x0B, 0xC9, 0x09,
                0x08, 0xC8, 0xD8, 0x18, 0x19, 0xD9, 0x1B, 0xDB, 0xDA, 0x1A, 0x1E, 0xDE, 0xDF, 0x1F, 0xDD,
                0x1D, 0x1C, 0xDC, 0x14, 0xD4, 0xD5, 0x15, 0xD7, 0x17, 0x16, 0xD6, 0xD2, 0x12, 0x13, 0xD3,
                0x11, 0xD1, 0xD0, 0x10, 0xF0, 0x30, 0x31, 0xF1, 0x33, 0xF3, 0xF2, 0x32, 0x36, 0xF6, 0xF7,
                0x37, 0xF5, 0x35, 0x34, 0xF4, 0x3C, 0xFC, 0xFD, 0x3D, 0xFF, 0x3F, 0x3E, 0xFE, 0xFA, 0x3A,
                0x3B, 0xFB, 0x39, 0xF9, 0xF8, 0x38, 0x28, 0xE8, 0xE9, 0x29, 0xEB, 0x2B, 0x2A, 0xEA, 0xEE,
                0x2E, 0x2F, 0xEF, 0x2D, 0xED, 0xEC, 0x2C, 0xE4, 0x24, 0x25, 0xE5, 0x27, 0xE7, 0xE6, 0x26,
                0x22, 0xE2, 0xE3, 0x23, 0xE1, 0x21, 0x20, 0xE0, 0xA0, 0x60, 0x61, 0xA1, 0x63, 0xA3, 0xA2,
                0x62, 0x66, 0xA6, 0xA7, 0x67, 0xA5, 0x65, 0x64, 0xA4, 0x6C, 0xAC, 0xAD, 0x6D, 0xAF, 0x6F,
                0x6E, 0xAE, 0xAA, 0x6A, 0x6B, 0xAB, 0x69, 0xA9, 0xA8, 0x68, 0x78, 0xB8, 0xB9, 0x79, 0xBB,
                0x7B, 0x7A, 0xBA, 0xBE, 0x7E, 0x7F, 0xBF, 0x7D, 0xBD, 0xBC, 0x7C, 0xB4, 0x74, 0x75, 0xB5,
                0x77, 0xB7, 0xB6, 0x76, 0x72, 0xB2, 0xB3, 0x73, 0xB1, 0x71, 0x70, 0xB0, 0x50, 0x90, 0x91,
                0x51, 0x93, 0x53, 0x52, 0x92, 0x96, 0x56, 0x57, 0x97, 0x55, 0x95, 0x94, 0x54, 0x9C, 0x5C,
                0x5D, 0x9D, 0x5F, 0x9F, 0x9E, 0x5E, 0x5A, 0x9A, 0x9B, 0x5B, 0x99, 0x59, 0x58, 0x98, 0x88,
                0x48, 0x49, 0x89, 0x4B, 0x8B, 0x8A, 0x4A, 0x4E, 0x8E, 0x8F, 0x4F, 0x8D, 0x4D, 0x4C, 0x8C,
                0x44, 0x84, 0x85, 0x45, 0x87, 0x47, 0x46, 0x86, 0x82, 0x42, 0x43, 0x83, 0x41, 0x81, 0x80,
                0x40};

        char[] High_order_byte_table = {0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81,
                0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0,
                0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01,
                0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41,
                0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81,
                0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0,
                0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01,
                0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40,
                0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81,
                0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0,
                0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01,
                0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
                0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81,
                0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0,
                0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01,
                0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
                0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81,
                0x40};


        int CRCHi = (int) 255;
        int CRCLo = (int) 255;
        int codigoLookup;
        int i = 0;
        while (len > 0) {
            len--;
            codigoLookup = (CRCLo ^ (data[i] & 255));
            CRCLo = (CRCHi ^ High_order_byte_table[codigoLookup]);
            CRCHi = (Low_order_byte_table[codigoLookup]);

            i++;
        }

        return ((CRCHi << 8) | CRCLo);
    }
}
