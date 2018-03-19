/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2016, 2017 Anthony Law
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * Contributors:
 *      - Anthony Law (mob41) - Initial API Implementation
 *      - bwssytems
 *      - Christian Fischer (computerlyrik)
 *******************************************************************************/

package com.github.mob41.blapi.device.hysen;

import java.io.IOException;

import javax.xml.bind.DatatypeConverter;

import com.github.mob41.blapi.BLDevice;
import com.github.mob41.blapi.device.hysen.cmd.GetBasicInfoCommand;
import com.github.mob41.blapi.device.hysen.cmd.GetStatusCommand;
import com.github.mob41.blapi.device.hysen.cmd.SetModeCommand;
import com.github.mob41.blapi.device.hysen.cmd.SetPoweCommand;
import com.github.mob41.blapi.device.hysen.cmd.SetPeriodsCommand;
import com.github.mob41.blapi.device.hysen.cmd.SetTempCommand;
import com.github.mob41.blapi.mac.Mac;

/**
 * Base hysen "class" thermostats
 * 
 * Adapted from https://github.com/mjg59/python-broadlink
 * 
 * @author alpapad
 *
 */
public class BaseHysenDevice extends BLDevice {

    /**
     * Generic way to create a BaseHysenDevice
     * 
     * @param deviceType Device Type
     * @param deviceDesc Friendly device description
     * @param host       The target Broadlink hostname
     * @param mac        The target Broadlink MAC address
     * @throws IOException Problems on constructing socket
     */
    protected BaseHysenDevice(short deviceType, String deviceDesc, String host, Mac mac) throws IOException {
        super(deviceType, deviceDesc, host, mac);
    }

    public double getThermostatTemp() throws Exception {
        BaseStatusInfo info = getBasicStatus();
        return info.getThermostatTemp();
    }

    public double getExternalTemp() throws Exception {
        BaseStatusInfo info = getBasicStatus();
        return info.getExternalTemp();
    }

    public double getRoomTemp() throws Exception {
        BaseStatusInfo info = getBasicStatus();
        return info.getRoomTemp();
    }

    public BaseStatusInfo getBasicStatus() throws Exception {
        byte[] pl = new GetBasicInfoCommand().execute(this);
        if (pl != null) {
            log.debug("getBasicStatus - received bytes: {}", DatatypeConverter.printHexBinary(pl));
            return new BaseStatusInfo(pl);
        }
        return null;
    }

    public AdvancedStatusInfo getAdvancedStatus() throws Exception {
        byte[] pl = new GetStatusCommand().execute(this);
        if (pl != null) {
            log.debug("getAdvancedStatus - received bytes: {}", DatatypeConverter.printHexBinary(pl));
            return new AdvancedStatusInfo(pl);
        }
        return null;
    }

    /**
     * Change controller mode auto_mode = 1 for auto (scheduled/timed) mode, 0 for
     * manual mode. Manual mode will activate last used temperature. In typical
     * usage call set_temp to activate manual control and set temp. loop_mode refers
     * to index in [ "12345,67", "123456,7", "1234567" ] E.g. loop_mode = 0
     * ("12345,67") means Saturday and Sunday follow the "weekend" schedule
     * loop_mode = 2 ("1234567") means every day (including Saturday and Sunday)
     * follows the "weekday" schedule
     * 
     * @throws Exception
     */
    public void setMode(boolean autoMode, LoopMode loopMode, SensorControl sensorControl) throws Exception {
        new SetModeCommand(tob(autoMode), loopMode.getValue(), sensorControl.getValue()).execute(this);
    }

    /**
     * Change controller mode auto_mode = 1 for auto (scheduled/timed) mode, 0 for
     * manual mode. Manual mode will activate last used temperature. In typical
     * usage call set_temp to activate manual control and set temp. loop_mode refers
     * to index in [ "12345,67", "123456,7", "1234567" ] E.g. loop_mode = 0
     * ("12345,67") means Saturday and Sunday follow the "weekend" schedule
     * loop_mode = 2 ("1234567") means every day (including Saturday and Sunday)
     * follows the "weekday" schedule
     * 
     * @throws Exception
     */
    public void setMode(boolean autoMode, LoopMode loopMode) throws Exception {
        BaseStatusInfo status = this.getBasicStatus();
        new SetModeCommand(tob(autoMode), loopMode.getValue(), status.getSensorControl().getValue()).execute(this);
    }

    public void setPower(boolean powerOn, boolean remoteLock) throws Exception {
        new SetPoweCommand(tob(powerOn), tob(remoteLock)).execute(this);
    }

    public void setPower(boolean powerOn) throws Exception {
        BaseStatusInfo status = this.getBasicStatus();
        new SetPoweCommand(tob(powerOn), tob(status.getRemoteLock())).execute(this);
    }

    public void setLock(boolean remoteLock) throws Exception {
        BaseStatusInfo status = this.getBasicStatus();
        new SetPoweCommand(tob(status.getPower()), tob(remoteLock)).execute(this);
    }

    public void setThermostatTemp(double temp) throws Exception {
        new SetTempCommand(temp).execute(this);
    }

    public void switchToAuto() throws Exception {
        BaseStatusInfo status = this.getBasicStatus();
        this.setMode(true, status.getLoopMode(), status.getSensorControl());
    }

    public void switchToManual() throws Exception {
        BaseStatusInfo status = this.getBasicStatus();
        this.setMode(false, status.getLoopMode(), status.getSensorControl());
    }

    public void setAdvancedOptions(LoopMode loopMode, SensorControl sensor, short osv, short dif, short svh, short svl,
            double adj, AntiFreezing antiFreeze, PowerOnMemory poweron) throws Exception {
        new SetModeCommand(loopMode.getValue(), sensor.getValue(), tob(osv), tob(dif), tob(svh), tob(svl), adj,
                antiFreeze.getValue(), poweron.getValue()).execute(this);
    }

    public void setPeriods(Period[] schedule) throws Exception {
        new SetPeriodsCommand(schedule).execute(this);
    }

    private static byte tob(boolean v) {
        return (byte) (v ? 1 : 0);
    }

    private static byte tob(short in) {
        return (byte) (in & 0xff);
    }
}
