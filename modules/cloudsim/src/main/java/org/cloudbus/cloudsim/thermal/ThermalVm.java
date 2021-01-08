package org.cloudbus.cloudsim.thermal;

import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.power.PowerVm;

public class ThermalVm extends PowerVm {

    public ThermalVm(int id, int userId, double mips, int pesNumber, int ram, long bw, long size, int priority, String vmm, CloudletScheduler cloudletScheduler, double schedulingInterval) {
        super(id, userId, mips, pesNumber, ram, bw, size, priority, vmm, cloudletScheduler, schedulingInterval);
    }
}
