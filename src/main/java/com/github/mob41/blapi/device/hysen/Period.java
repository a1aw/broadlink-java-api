package com.github.mob41.blapi.device.hysen;

/**
 * Represents a "Period" in what hysen calls "Time and interval programming"
 * 
 * @author alpapad
 */
public class Period {
    // start_hour
    private final short startHour;
    // start_minute
    private final short startMinute;
    private final double temp;

    protected Period(int offset, byte[] payload) {
        this.startHour = payload[2 * offset + 23];
        this.startMinute = payload[2 * offset + 24];
        this.temp = payload[offset + 39] / 2.0;
    }

    public short getStartHour() {
        return startHour;
    }

    public short getStartMinute() {
        return startMinute;
    }

    public double getTemp() {
        return temp;
    }

    @Override
    public String toString() {
        return "\n\tSchedule [startHour=" + startHour + ", startMinute=" + startMinute + ", temp=" + temp + "]";
    }

}
