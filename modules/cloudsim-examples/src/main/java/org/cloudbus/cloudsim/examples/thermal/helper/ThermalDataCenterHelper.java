package org.cloudbus.cloudsim.examples.thermal.helper;

import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.thermal.ThermalDataCenter;
import org.cloudbus.cloudsim.thermal.ThermalHostUtilizationHistory;
import org.cloudbus.cloudsim.thermal.ThermalVmAllocationMigrationCellularAutomaton;

import java.util.LinkedList;
import java.util.List;

public class ThermalDataCenterHelper {
    public static ThermalDataCenter createDataCenter(List<ThermalHostUtilizationHistory> hostList) throws Exception {
        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";
        double time_zone = 10.0;
        double cost = 3.0;
        double costPerMem = 0.05;
        double costPerStorage = 0.001;
        double costPerBw = 0.0;
        int optimizationInterval = 10000;
        LinkedList<Storage> storageList = new LinkedList<Storage>();

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);
        VmAllocationPolicy thermalVmAllocationPolicy = new ThermalVmAllocationMigrationCellularAutomaton(hostList, optimizationInterval);

        return new ThermalDataCenter("DataCenter1", characteristics, thermalVmAllocationPolicy, storageList, 300);
    }
}
