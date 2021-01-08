package org.cloudbus.cloudsim.examples.thermal;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.examples.thermal.helper.*;
import org.cloudbus.cloudsim.thermal.*;

import java.util.Calendar;
import java.util.List;

public class Example {
    public static void main(String[] args) {
        int numberOfColumns = 10;
        int numberOfRacks = 10;
        int numberOfHosts = 10;
        int loadFrequency = 288;
        int numberOfCloudLet = 500;
        boolean trace_flag = false;
        int num_user = 1;

        try {
            CellularAutomaton.evolve(numberOfColumns, numberOfRacks, numberOfHosts, (loadFrequency/20 + 1));

            Calendar calendar = Calendar.getInstance();
            CloudSim.init(num_user, calendar, trace_flag);

            ThermalDataCenterBroker broker = new ThermalDataCenterBroker("Broker");

            List<ThermalHostUtilizationHistory> hostList = ThermalHostHelper.createHosts(numberOfColumns, numberOfRacks, numberOfHosts);
            List<Cloudlet> cloudLetList = ThermalCloudLetHelper.createCloudLet(numberOfCloudLet, loadFrequency, broker.getId());
            List<ThermalVm> vmList = ThermalVmHelper.createVm(numberOfCloudLet, broker.getId());

            ThermalDataCenter dataCenter = ThermalDataCenterHelper.createDataCenter(hostList);

            broker.submitVmList(vmList);
            broker.submitCloudletList(cloudLetList);

            CloudSim.startSimulation();

            List<Cloudlet> newList = broker.getCloudletReceivedList();
            Log.printLine("Received " + newList.size() + " cloudLets");

            CloudSim.stopSimulation();

            Printer.printCloudLetList(newList, dataCenter);
            Log.printLine("CloudSimExample1 finished!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Unwanted errors happen");
        }
    }
}
