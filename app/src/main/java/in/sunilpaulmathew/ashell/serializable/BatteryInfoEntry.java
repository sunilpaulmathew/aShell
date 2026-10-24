package in.sunilpaulmathew.ashell.serializable;

import java.io.Serializable;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 18, 2026
 */
public class BatteryInfoEntry implements Serializable {

    private final int level, status, health, temperature, voltage;
    private final int chargeCounter, maximumCapacity, designCapacity;
    private final boolean acPowered, usbPowered, wirelessPowered, dockPowered;
    private final String technology;

    public BatteryInfoEntry(int level, int status, int health, int temperature, int voltage,
                            int chargeCounter, int maximumCapacity, int designCapacity,
                            boolean acPowered, boolean usbPowered, boolean wirelessPowered, boolean dockPowered,
                            String technology) {
        this.level = level;
        this.status = status;
        this.health = health;
        this.temperature = temperature;
        this.voltage = voltage;
        this.chargeCounter = chargeCounter;
        this.maximumCapacity = maximumCapacity;
        this.designCapacity = designCapacity;
        this.acPowered = acPowered;
        this.usbPowered = usbPowered;
        this.wirelessPowered = wirelessPowered;
        this.dockPowered = dockPowered;
        this.technology = technology;
    }

    public int getLevel() { return level; }

    public String getChargingString() {
        if (acPowered) return "Charging: AC";
        if (usbPowered) return "Charging: USB";
        if (wirelessPowered) return "Charging: Wireless";
        if (dockPowered) return "Charging: Dock";
        return "Charging";
    }

    public String getStatusString() {
        switch (status) {
            case 2:
                return getChargingString();
            case 3:
                return "Discharging";
            case 4:
                return "Not charging";
            case 5:
                return "Full";
            default:
                return "Unknown";
        }
    }

    public String getHealthString() {
        switch (health) {
            case 2: return "Good";
            case 3: return "Overheat";
            case 4: return "Dead";
            case 5: return "Over voltage";
            case 6: return "Unspecified failure";
            case 7: return "Cold";
            default: return "Unknown";
        }
    }

    public float getTemperatureC() {
        return temperature / 10.0f;
    }

    public float getVoltageV() {
        return voltage / 1000.0f;
    }

    public int getEstimatedMaximumCapacityMah() {
        if (maximumCapacity > 0) {
            return maximumCapacity / 1000;
        }

        if (level > 0 && chargeCounter > 0) {
            float currentMah = chargeCounter / 1000.0f;
            float fraction = level / 100.0f;
            return Math.round(currentMah / fraction);
        }

        return 0;
    }

    public float getBatteryHealthPercentage() {
        if (designCapacity > 0) {
            float healthPct = ((float) maximumCapacity / (float) designCapacity) * 100.0f;
            return Math.min(healthPct, 100.0f);
        }
        return 0.0f;
    }

    public String getTechnology() {
        return technology;
    }

}