package org.cloudbus.cloudsim.examples.thermal.helper;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelNull;
import org.cloudbus.cloudsim.UtilizationModelPlanetLabInMemory;
import org.cloudbus.cloudsim.examples.power.Constants;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ThermalCloudLetHelper {
    public static List<Cloudlet> createCloudLet(int numberOfCloudLets, int loadFrequency, int brokerId) throws IOException {
        int pesNumber = 1;
        long fileSize = 300;
        long outputSize = 300;
        List<Cloudlet> cloudLetList = new ArrayList<Cloudlet>();
        String dir = "user.dir";
        String path = "\\modules\\cloudsim-examples\\target\\classes\\workload\\planetlab\\";
        String[] directories = {"20110303", "20110306", "20110309", "20110322", "20110325", "20110403", "20110409", "20110411", "20110412", "20110420"};
        String outputPath = "\\data\\CombinedUtilization\\CombinedUtilization";
        String extension = ".txt";

        long[] length = new long[numberOfCloudLets];
        for(int i = 0; i < numberOfCloudLets; i++) {
            int utilization = 0;
            String fileName = System.getProperty(dir) + outputPath + i + extension;
            File myObj = new File(fileName);
            myObj.createNewFile();
            FileWriter fileWriter = new FileWriter(fileName);
            for(String directory : directories) {
                File inputFolder = new File(System.getProperty(dir) + path + directory);
                File[] files = inputFolder.listFiles();
                if(files != null) {
                    BufferedReader input = new BufferedReader(new FileReader(files[i].getAbsolutePath()));
                    for(int j = 0; j < loadFrequency; j++) {
                        String load = input.readLine();
                        fileWriter.write(load + "\n");
                        utilization = utilization + Integer.parseInt(load);
                    }
                }
            }
            fileWriter.close();
            length[i] = utilization * 150;
        }

        UtilizationModel utilizationModelNull = new UtilizationModelNull();
        for(int i = 0; i < numberOfCloudLets; i++) {
            File inputFolder = new File(System.getProperty(dir) + outputPath + i + extension);
            String inputPaths = inputFolder.getAbsolutePath();
            UtilizationModelPlanetLabInMemory utilizationModelPlanetLabInMemory = new UtilizationModelPlanetLabInMemory(inputPaths, Constants.SCHEDULING_INTERVAL);
            Cloudlet cloudlet = new Cloudlet(i, length[i], pesNumber, fileSize, outputSize, utilizationModelPlanetLabInMemory, utilizationModelNull, utilizationModelNull);
            cloudlet.setUserId(brokerId);
            cloudlet.setVmId(i);
            cloudLetList.add(cloudlet);
        }

        return cloudLetList;
    }
}
