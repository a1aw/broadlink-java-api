package com.github.mob41.blapi.dev.hysen;

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
        int iStartHour = 2 * offset + 23;
        int iStartMin = 2 * offset + 24;
        int iTemp = offset + 39;
        if ((iStartHour<payload.length)&&(iStartMin<payload.length)&&(iTemp<payload.length)){
			this.startHour = payload[iStartHour];
			this.startMinute = payload[iStartMin];
			this.temp = payload[iTemp] / 2.0;
        }
        else {
			this.startHour = 0;
			this.startMinute = 0;
			this.temp = 0.0;        	
        }
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
