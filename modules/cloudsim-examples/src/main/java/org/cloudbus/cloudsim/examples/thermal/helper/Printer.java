package org.cloudbus.cloudsim.examples.thermal.helper;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.thermal.ThermalDataCenter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class Printer {
    public static void printCloudLetList(List<Cloudlet> list, ThermalDataCenter thermalDataCenter, int rule, FileWriter fileWriter) {
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


        try {
            fileWriter.write("========= RUlE : "+ rule +" =========" + "\n");
            fileWriter.write("========== OUTPUT ==========" + "\n");
            fileWriter.write("DataCenter ID" + indent + "Energy Consumed" + "\n");
            double energy = thermalDataCenter.getPower() / (3600 * 1000);
            fileWriter.write(thermalDataCenter.getId() + indent + indent + indent + indent + energy + unit + "\n");
            fileWriter.write("\n\n\n\n\n\n\n\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
