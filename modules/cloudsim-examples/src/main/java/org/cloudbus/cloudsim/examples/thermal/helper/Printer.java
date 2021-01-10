package org.cloudbus.cloudsim.examples.thermal.helper;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.thermal.ThermalDataCenter;

import java.text.DecimalFormat;
import java.util.List;

public class Printer {
    public static void printCloudLetList(List<Cloudlet> list, ThermalDataCenter thermalDataCenter) {
        Cloudlet cloudlet;
        String unit = " Kwh";

        String indent = "    ";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("CloudLet ID" + indent + "STATUS" + indent + "Data center ID" + indent + "VM ID" + indent + "Start Time" + indent + "Finish Time");

        DecimalFormat dft = new DecimalFormat("###.##");
        for (Cloudlet value : list) {
            cloudlet = value;
            Log.print(indent + cloudlet.getCloudletId() + indent + indent);

            if (cloudlet.getStatus() == Cloudlet.SUCCESS) {
                Log.print("SUCCESS");

                Log.printLine(indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId()
                        + indent + indent + dft.format(cloudlet.getExecStartTime()) + indent + indent + dft.format(cloudlet.getFinishTime()));
            }
        }

        Log.printLine("========== OUTPUT ==========");
        Log.printLine("DataCenter ID" + indent + "Energy Consumed");
        double energy = thermalDataCenter.getPower() / (3600 * 1000);
        Log.printLine(thermalDataCenter.getId() + indent + indent + indent + indent + energy + unit);
    }
}
