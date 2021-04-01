package be.domotiqueinformatique.tools.model;

public enum Units {
    DEGREE_CELSIUS ("°C"),
    DEGREE_FAHRENHEIT ("°F"),
    DEGREE ("°"),
    LITER ("L"),
    GALLON("gal"),
    VOLTS ("V"),
    WATT ("W"),
    AMPERE ("A"),
    PERCENT ("%"),
    METER ("m"),
    FEET ("ft"),
    PASCAL ("Pa"),
    PSI ("psi"),
    COUNT_OR_AMOUNT ("#");

    private final String value;

    Units(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
