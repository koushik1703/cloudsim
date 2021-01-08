package org.cloudbus.cloudsim.examples.thermal.helper;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelNull;
import org.cloudbus.cloudsim.UtilizationModelPlanetLabInMemory;
import org.cloudbus.cloudsim.examples.power.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ThermalCloudLetHelper {
    public static List<Cloudlet> createCloudLet(int numberOfCloudLets, int loadFrequency, int brokerId) throws IOException {
        int pesNumber = 1;
        long fileSize = 300;
        long outputSize = 300;
        List<Cloudlet> cloudLetList = new ArrayList<Cloudlet>();
        String dir = "user.dir";
        String path = "\\modules\\cloudsim-examples\\target\\classes\\workload\\planetlab\\20110306";

        File inputFolder = new File(System.getProperty(dir) + path);
        File[] files = inputFolder.listFiles();
        long[] length = new long[numberOfCloudLets];
        for(int i = 0; i < numberOfCloudLets; i++) {
            if(files != null) {
                BufferedReader input = new BufferedReader(new FileReader(files[i].getAbsolutePath()));
                int utilization = 0;
                for(int j = 0; j < loadFrequency; j++) {
                    utilization = utilization + Integer.parseInt(input.readLine());
                }
                length[i] = utilization * 150;
            }
        }

        UtilizationModel utilizationModelNull = new UtilizationModelNull();
        for(int i = 0; i < numberOfCloudLets; i++) {
            if(files != null) {
                UtilizationModelPlanetLabInMemory utilizationModelPlanetLabInMemory = new UtilizationModelPlanetLabInMemory(files[i].getAbsolutePath(), Constants.SCHEDULING_INTERVAL);
                Cloudlet cloudlet = new Cloudlet(i, length[i], pesNumber, fileSize, outputSize, utilizationModelPlanetLabInMemory, utilizationModelNull, utilizationModelNull);
                cloudlet.setUserId(brokerId);
                cloudlet.setVmId(i);
                cloudLetList.add(cloudlet);
            }
        }

        return cloudLetList;
    }
}
