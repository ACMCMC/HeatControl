package acmc.heatcontrol;

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
}
