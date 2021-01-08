package org.cloudbus.cloudsim.examples.thermal.helper;

import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.power.models.PowerModelLinear;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisioner;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.thermal.ThermalHost;
import org.cloudbus.cloudsim.thermal.ThermalHostUtilizationHistory;

import java.util.ArrayList;
import java.util.List;

public class ThermalHostHelper {
    public static List<ThermalHostUtilizationHistory> createHosts(int numberOfColumns, int numberOfRacks, int numberOfHosts) {
        int peMips = 100;
        int hostId = 0;
        int hostRam = 2560;
        long hostStorage = 1000000;
        int hostBw = 10000;
        int firstHost = 0;
        int lastHost = numberOfHosts - 1;

        List<ThermalHostUtilizationHistory> hostList = new ArrayList<ThermalHostUtilizationHistory>();
        List<Pe> peList = new ArrayList<Pe>();
        ThermalHost[][][] thermalHosts = new ThermalHost[numberOfColumns][numberOfRacks][numberOfHosts];

        for(int i = 0; i < 5; i++) {
            PeProvisionerSimple peProvisionerSimple = new PeProvisionerSimple(peMips);
            Pe pe = new Pe(i, peProvisionerSimple);
            peList.add(pe);
        }

        for(int column = 0; column < numberOfColumns; column++) {
            for(int rack = 0; rack < numberOfRacks; rack++) {
                for(int host = 0; host < numberOfHosts; host++) {
                    RamProvisioner ramProvisioner = new RamProvisionerSimple(hostRam);
                    BwProvisionerSimple bwProvisionerSimple = new BwProvisionerSimple(hostBw);
                    VmSchedulerTimeShared vmSchedulerTimeShared = new VmSchedulerTimeShared(peList);
                    PowerModel powerModel = new PowerModelLinear(200, 0.3);
                    ThermalHostUtilizationHistory thermalHost = new ThermalHostUtilizationHistory(hostId, ramProvisioner, bwProvisionerSimple, hostStorage, peList, vmSchedulerTimeShared, powerModel);
                    thermalHosts[column][rack][host] = thermalHost;
                    hostList.add(thermalHost);
                    hostId++;
                }
            }
        }

        for(int column = 0; column < numberOfColumns; column++) {
            for(int rack = 0; rack < numberOfRacks; rack++) {
                for(int host = 0; host < numberOfRacks; host++) {
                    if(host != firstHost) {
                        thermalHosts[column][rack][host].setRightHost(thermalHosts[column][rack][host - 1]);
                    }
                    if(host != lastHost) {
                        thermalHosts[column][rack][host].setLeftHost(thermalHosts[column][rack][host + 1]);
                    }
                }
            }
        }

        return hostList;
    }
}
