package acmc.heatcontrol;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by acmc on 11/12/2018.
 */

public class Device {

    private String mac;
    private String type;
    private String name;
    private boolean lock;
    private Integer password;
    private Integer id;
    private Integer subdevice;
    private String key;
    private Float tempSeleccionada;
    private Float tempActual;
    private Boolean enabledState;
    private Boolean isManualTemp;
    private Boolean isAutoTemp;
    private Boolean isRemote;

    /*rawData es la respuesta del dispositivo a Query(). Sabemos que:
    El segundo bit por la izquierda de [4] nos dice si la temperatura está controlada manual o automáticamente.

    [3] es si es un dispositivo remoto
    [4] es un byte que representa:
        (desde la derecha, el bit menos significativo)
        1: si está encendido
        4: si la válvula está abierta
        5: ?
        6: si el control de temperatura es manual
    [5] es la temperatura actual
    [6] es la temperatura seleccionada
    [11] es la temperatura máxima a la que se puede ajustar el dispositivo
    [12] es ¿temperatura mínima a la que se puede ajustar el dispositivo?
     */

    private List<Byte> rawData;

    public Device() {
    }

    public Device(String mac, String type, String name, boolean lock, int password, int id, int subdevice, String key) {
        this.mac = mac;
        this.type = type;
        this.name = name;
        this.lock = lock;
        this.password = password;
        this.id = id;
        this.subdevice = subdevice;
        this.key = key;
        this.rawData = new ArrayList<Byte>();
    }

    public Device(String mac, String type, String name, String lock, String password, String id, String subdevice, String key) throws IllegalArgumentException {
        this.mac = mac;
        this.type = type;
        this.name = name;
        if (lock.equals("1")) {
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
        this.rawData = new ArrayList<Byte>();
    }

    public boolean isManualTemp() {
        return isManualTemp;
    }

    String getMac() {
        return (mac);
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
        String out = BroadlinkAPI.getInstance().executeCommand(BroadlinkConstants.CMD_DEVICE_STATE_ID, BroadlinkConstants.CMD_DEVICE_STATE, mac);
        JsonObject outJson = new JsonParser().parse(out).getAsJsonObject();
        state = outJson.get("status").getAsString();
        return state;
    }

    public boolean getPowerState() {
        BroadlinkAPI api = BroadlinkAPI.getInstance();
        JsonObject request = new JsonObject();
        request.addProperty(BroadlinkConstants.PARAM_MAC, mac);
        request.addProperty("ipaddr", "192.168.1.1");
        String out = api.executeCommand(request, BroadlinkConstants.CMD_PASSTHROUGH_ID, BroadlinkConstants.CMD_PASSTHROUGH);
        System.out.println(out);

        JsonObject outJson = new JsonParser().parse(out).getAsJsonObject();
        if (outJson.get("code").getAsInt() == -3) {
            return true;
        }
        return false;
    }

    private void Query() {
        BroadlinkMessageData messageData = new BroadlinkMessageData();
        messageData.addMessageByte((byte) 1);
        messageData.addMessageByte((byte) 3);
        messageData.addMessageByte((byte) 0);
        messageData.addMessageByte((byte) 0);
        messageData.addMessageByte((byte) 0);
        messageData.addMessageByte((byte) 22);

        JsonObject request = new JsonObject();
        request.addProperty(BroadlinkConstants.PARAM_MAC, mac);
        request.addProperty(BroadlinkConstants.FORMAT, BroadlinkConstants.FORMAT_STRING);
        request.addProperty(BroadlinkConstants.DATA, messageData.getMessageAsHexString());

        String out = BroadlinkAPI.getInstance().executeCommand(request, BroadlinkConstants.CMD_PASSTHROUGH_ID, BroadlinkConstants.CMD_PASSTHROUGH);

        JsonObject outJson = new JsonParser().parse(out).getAsJsonObject();
        String msg = outJson.get("data").getAsString();

        rawData.clear();

        for (int i = 0; i < (msg.length() / 2); i++) {
            rawData.add(i, (Byte) Integer.valueOf(Integer.parseInt(msg.substring(i * 2, (i * 2 + 2)), 16)).byteValue());
        }

        tempActual = new Float(Integer.valueOf(rawData.get(5)).floatValue() / 2);
        tempSeleccionada = new Float(Integer.valueOf(rawData.get(6)).floatValue() / 2);

        if (rawData.get(3).intValue() == 1) {isRemote = true;} else {isRemote = false;}
        if ((rawData.get(4).intValue() & 1) == 1) {enabledState = true;} else {enabledState = false;}
        if (((rawData.get(4).intValue() >> 6) & 1) == 1) {isManualTemp = true;} else {isManualTemp = false;}

    }

    public void update() {
        Query();
    }

    public String getTempSeleccionadaAsString() {
        if (tempSeleccionada == null) {
            Query();
        }

        return String.valueOf(tempSeleccionada);
    }

    public Float getTempSeleccionada() {
        if (tempSeleccionada == null) {
            Query();
        }

        return tempSeleccionada;
    }

    public String getTempActualAsString() {
        if (tempActual == null) {
            Query();
        }

        return String.valueOf(tempActual);
    }

    public Float getTempActual() {
        if (tempActual == null) {
            Query();
        }

        return tempActual;
    }

    public boolean isAutoTemp() {
        if (isAutoTemp == null) {
            Query();
        }

        return enabledState;
    }

    public boolean getEnabledState() {
        /*if (isAutoTemp == null || isManualTemp == null) {
            Query();
        }

        if (isAutoTemp || isManualTemp) {
            enabledState = true;
        } else { enabledState = false;}*/

        if (enabledState == null) {
            Query();
        }

        return enabledState;
    }

    public void setTemp(Float temp) {

        //la temperatura la multiplicamos por 2 para ajustarla, porque por cada +1 = +0.5 (y no +1).
        temp = temp * 2;
        this.tempSeleccionada = temp;

        JsonObject request = new JsonObject();
        request.addProperty(BroadlinkConstants.PARAM_MAC, mac);
        request.addProperty(BroadlinkConstants.FORMAT, BroadlinkConstants.FORMAT_STRING);

        String data = new String();

        byte[] tempMsg = new byte[6];

        tempMsg[0] = (byte) 1;
        tempMsg[1] = (byte) 6;
        tempMsg[2] = (byte) 0;
        tempMsg[3] = (byte) 1;
        tempMsg[4] = (byte) 0;
        tempMsg[5] = (byte) temp.intValue();

        BroadlinkMessageData messageData = new BroadlinkMessageData(tempMsg);

        request.addProperty(BroadlinkConstants.DATA, messageData.getMessageAsHexString());

        String out = BroadlinkAPI.getInstance().executeCommand(request, BroadlinkConstants.CMD_PASSTHROUGH_ID, BroadlinkConstants.CMD_PASSTHROUGH);
    }


}
