package org.cloudbus.cloudsim.examples.thermal.helper;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.thermal.ThermalDataCenter;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.text.DecimalFormat;
import java.util.List;

public class Printer {
    public static void printCloudLetList(List<Cloudlet> list, ThermalDataCenter thermalDataCenter, int rule, Document document, Element vmElement) {
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

        Element ruleElement = document.createElement("Rule" + rule);
        Attr attr = document.createAttribute("EnergyConsumed");
        double energy = thermalDataCenter.getPower() / (3600 * 1000);
        attr.setValue("" + energy);
        ruleElement.setAttributeNode(attr);
        vmElement.appendChild(ruleElement);
    }
}
