package org.cloudbus.cloudsim.examples.thermal.helper;

import org.cloudbus.cloudsim.CloudletSchedulerDynamicWorkload;
import org.cloudbus.cloudsim.thermal.ThermalVm;

import java.util.ArrayList;
import java.util.List;

public class ThermalVmHelper {
    public static List<ThermalVm> createVm(int numberOfVms, int brokerId) {
        int mips = 100;
        long size = 10000;
        int ram = 512;
        long bw = 1000;
        int pesNumber = 1;
        String vmm = "Xen";
        List<ThermalVm> vmList = new ArrayList<ThermalVm>();

        for(int i = 0; i < numberOfVms; i++) {
            CloudletSchedulerDynamicWorkload cloudletSchedulerDynamicWorkload = new CloudletSchedulerDynamicWorkload(100, 1);
            vmList.add(new ThermalVm(i, brokerId, mips, pesNumber, ram, bw, size, 1, vmm, cloudletSchedulerDynamicWorkload, 300));
        }

        return vmList;
    }
}
