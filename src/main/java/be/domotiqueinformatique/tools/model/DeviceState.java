package be.domotiqueinformatique.tools.model;

public enum DeviceState {
    INIT ("init"),
    READY ("ready"),
    DISCONNECTED ("disconnected"),
    SLEEPING ("sleeping"),
    LOST ("lost"),
    ALERT ("alert");

    private final String value;

    DeviceState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
