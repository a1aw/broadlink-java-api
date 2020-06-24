package com.github.mob41.blapi.dev.hysen;

import java.util.Arrays;

/**
 * Advanced status info retrieved from a hysen type thermostat
 * 
 * Adapted from https://github.com/mjg59/python-broadlink
 * 
 * @author alpapad
 *
 */
public class AdvancedStatusInfo extends BaseStatusInfo {

    private final short hour;
    private final short min;
    private final short sec;
    private final short dayofweek;

    // there are 8 periods available for configuration:
    // 6 for weekdays and 2 for weekends.
    // Loop mode controls which periods get to be applied during Sat and Sun.
    private final Period[] periods = new Period[8];

    private final Period[] weekday = new Period[6];
    private final Period[] weekend = new Period[2];

    protected AdvancedStatusInfo(byte[] payload) {
        super(payload);
        if ((payload[19]>=0)&&(payload[19]<24))
        	this.hour = payload[19];
        else
        	this.hour = 0;
        if ((payload[20]>=0)&&(payload[20]<60))
        	this.min = payload[20];
        else
        	this.min = 0;
        if ((payload[21]>=0)&&(payload[21]<60))
        	this.sec = payload[21];
        else
        	this.sec = 0;
        if ((payload[22]>0)&&(payload[22]<=7))
        	this.dayofweek = payload[22];
        else
        	this.dayofweek = 1;        
        for (int i = 0; i < 6; i++) {
            this.periods[i] = new Period(i, payload);
            this.weekday[i] = this.periods[i];

        }

        for (int i = 6; i <= 7; i++) {
            this.periods[i] = new Period(i, payload);
            this.weekend[i - 6] = this.periods[i];
        }

    }

    public short getHour() {
        return hour;
    }

    public short getMin() {
        return min;
    }

    public short getSec() {
        return sec;
    }

    public short getDayofweek() {
        return dayofweek;
    }

    public Period[] getWeekday() {
        return weekday;
    }

    public Period[] getWeekend() {
        return weekend;
    }

    public Period[] getPeriods() {
        return periods;
    }

    @Override
    public String toString() {
        return "StatusInfo [remote lock=" + remoteLock + ",\n power=" + power + ",\n active=" + active + ",\n rtd_open=" + rtdOpen
                + ",\n manual temperature=" + manualTemp + ",\n room temp=" + roomTemp + ",\n thermostat temp="
                + thermostatTemp + ",\n auto_mode=" + autoMode + ",\n loop_mode=" + loopMode + ",\n SensorControl="
                + sensorControl + ",\n osv=" + osv + ",\n dif=" + dif + ",\n svh=" + svh + ",\n svl=" + svl
                + ",\n room temp adj=" + roomTempAdjustment + ",\n anti freeze=" + antiFreezing + ",\n powerOnMemory="
                + powerOnMemory + ",\n fac?=" + fac + ",\n external temp=" + externalTemp + ",\n hour=" + hour
                + ",\n min=" + min + ",\n sec=" + sec + ",\n dayofweek=" + dayofweek + ",\n\n weekday="
                + Arrays.toString(weekday) + ",\n\n weekend=" + Arrays.toString(weekend) + "]";
    }

}
