package acmc.heatcontrol;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by acmc on 11/12/2018.
 */

public class Device {

    private String mac;
    private String type;
    private String name;
    private boolean lock;
    private int password;
    private int id;
    private int subdevice;
    private String key;
    private int temp;

public Device(){}

    public Device(String mac, String type, String name, boolean lock, int password, int id, int subdevice, String key) {
        this.mac = mac;
        this.type = type;
        this.name = name;
        this.lock = lock;
        this.password = password;
        this.id = id;
        this.subdevice = subdevice;
        this.key = key;
    }

    public Device(String mac, String type, String name, String lock, String password, String id, String subdevice, String key) throws IllegalArgumentException {
        this.mac = mac;
        this.type = type;
        this.name = name;
        if(lock.equals("1")){
            this.lock = true;
        } else if (lock.equals("0")) {
            this.lock = false;
        } else {
            throw new IllegalArgumentException("No se reconoce el estado de bloqueo");
        }
        this.password = Integer.parseInt(password);
        this.id = Integer.parseInt(id);
        this.subdevice = Integer.parseInt(subdevice);
        this.key = key;
    }

    String getMac() {
        return(mac);
    }

    int getLockInt() {
        if (lock == true) {
            return (1);
        } else if (lock == false) {
            return (0);
        } else {
            return (0);
        }
    }

    boolean getLock() {
        return lock;
    }

    String getName() {
        return name;
    }


    public String getState() {
        String state;
        String out = BroadlinkAPI.getInstance().executeCommand(BroadlinkConstants.CMD_DEVICE_STATE_ID,BroadlinkConstants.CMD_DEVICE_STATE,mac);
        JsonObject outJson = new JsonParser().parse(out).getAsJsonObject();
        state = outJson.get("status").getAsString();
        return state;
    }

    private boolean getPowerState() {
        BroadlinkAPI api = BroadlinkAPI.getInstance();
        JsonObject request = new JsonObject();
        request.addProperty(BroadlinkConstants.PARAM_MAC,mac);
        request.addProperty("ipaddr","192.168.1.1");
        String out = api.executeCommand(request,BroadlinkConstants.CMD_WIFI_INFO_ID,BroadlinkConstants.CMD_WIFI_INFO);
        System.out.println(out);

        JsonObject outJson = new JsonParser().parse(out).getAsJsonObject();
        if (outJson.get("code").getAsInt() == -3) {
            return true;
        }
        return false;
    }

    public int setTemp(int temp) {

        //la temperatura la multiplicamos por 2 para ajustarla, porque por cada +1 = +0.5 (y no +1).
        temp = temp * 2;
        this.temp = temp;

        JsonObject request = new JsonObject();
        request.addProperty(BroadlinkConstants.PARAM_MAC,mac);
        request.addProperty("format","string");

        String data = new String();

        byte[] test = new byte[8];
        /*test[0] = (byte) 1;
        test[1] = (byte) 3;
        test[2] = (byte) 0;
        test[3] = (byte) 1;
        test[4] = (byte) 0;
        test[5] = (byte) temp;
        test[6] = (byte) (BroadlinkAPI.Modbus_CRC16(test, 6) & 255);
        test[7] = (byte) ((BroadlinkAPI.Modbus_CRC16(test, 6) >> 8) & 255);*/

        test[0] = (byte) 1;
        test[1] = (byte) 6;
        test[2] = (byte) 0;
        test[3] = (byte) 1;
        test[4] = (byte) 0;
        test[5] = (byte) temp;
        test[6] = (byte) (BroadlinkAPI.Modbus_CRC16(test, 6) & 255);
        test[7] = (byte) ((BroadlinkAPI.Modbus_CRC16(test, 6) >> 8) & 255);

        for (int i = 0; i < 8; i++) {
            String temps = Integer.toHexString(test[i] & 255);
            if (temps.length() == 1) {
                data = new StringBuilder(String.valueOf(data)).append("0").toString();
            }
            data = new StringBuilder(String.valueOf(data)).append(temps).toString();
        }

        request.addProperty("data",data);

        String out = BroadlinkAPI.getInstance().executeCommand(request,BroadlinkConstants.CMD_WIFI_INFO_ID,"passthrough");
        return temp;
    }


}
