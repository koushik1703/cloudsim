package org.cloudbus.cloudsim.examples;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.*;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.examples.power.Constants;
import org.cloudbus.cloudsim.power.*;
import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.power.models.PowerModelLinear;
import org.cloudbus.cloudsim.provisioners.*;

public class CloudSimExample9 {

    public static void main(String[] args) {

        try {
            cellularAutomataEvolve();
            int num_user = 1;
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false;
            CloudSim.init(num_user, calendar, trace_flag);

            List<PowerHostUtilizationHistory> hostList = new ArrayList<PowerHostUtilizationHistory>();
            List<PowerVm> vmlist = new ArrayList<PowerVm>();
            List<Pe> peList = new ArrayList<Pe>();
            LinkedList<Storage> storageList = new LinkedList<Storage>();
            List<Cloudlet> cloudletList = new ArrayList<Cloudlet>();
            PowerHost powerHosts[][][] = new PowerHost[6][6][6];

            int peMips = 100;
            for(int i = 0; i < 5; i++) {
                PeProvisionerSimple peProvisionerSimple = new PeProvisionerSimple(peMips);
                Pe pe = new Pe(i, peProvisionerSimple);
                peList.add(pe);
            }

            int numberOfColumns = 2;
            int numberOfRacks = 2;
            int numberOfHosts = 2;
            int loadFrequency = 288;
            int firstColumn = 0;
            int lastColumn = 5;
            int firstRack = 0;
            int lastRack = 5;
            int firstHost = 0;
            int lastHost = 5;

            int hostId = 0;
            int hostRam = 2560;
            long hostStorage = 1000000;
            int hostBw = 10000;
            for(int column = 0; column < numberOfColumns; column++) {
                for(int rack = 0; rack < numberOfRacks; rack++) {
                    for(int host = 0; host < numberOfHosts; host++) {
                        RamProvisioner ramProvisioner = new RamProvisionerSimple(hostRam);
                        BwProvisionerSimple bwProvisionerSimple = new BwProvisionerSimple(hostBw);
                        VmSchedulerTimeShared vmSchedulerTimeShared = new VmSchedulerTimeShared(peList);
                        PowerModel powerModel = new PowerModelLinear(200, 0.3);
                        PowerHostUtilizationHistory powerHost = new PowerHostUtilizationHistory(hostId, ramProvisioner, bwProvisionerSimple, hostStorage, peList, vmSchedulerTimeShared, powerModel);
                        powerHosts[column][rack][host] = powerHost;
                        hostList.add(powerHost);
                        hostId++;
                    }
                }
            }
            for(int column = 0; column < numberOfColumns; column++) {
                for(int rack = 0; rack < numberOfRacks; rack++) {
                    for(int host = 0; host < numberOfHosts; host++) {
                        if(column != firstColumn) {
                            powerHosts[column][rack][host].setBackHost(powerHosts[column - 1][rack][host]);
                        }
                        if(column != lastColumn) {
                            powerHosts[column][rack][host].setFrontHost(powerHosts[column + 1][rack][host]);
                        }
                        if(rack != firstRack) {
                            powerHosts[column][rack][host].setUpHost(powerHosts[column][rack - 1][host]);
                        }
                        if(rack != lastRack) {
                            powerHosts[column][rack][host].setDownHost(powerHosts[column][rack + 1][host]);
                        }
                        if(host != firstHost) {
                            powerHosts[column][rack][host].setRightHost(powerHosts[column][rack][host - 1]);
                        }
                        if(host != lastHost) {
                            powerHosts[column][rack][host].setLeftHost(powerHosts[column][rack][host + 1]);
                        }
                    }
                }
            }

            RamProvisioner ramProvisioner = new RamProvisionerSimple(hostRam);
            BwProvisionerSimple bwProvisionerSimple = new BwProvisionerSimple(hostBw);
            VmSchedulerTimeShared vmSchedulerTimeShared = new VmSchedulerTimeShared(peList);
            PowerModel powerModel = new PowerModelLinear(200, 0.3);
            PowerHostUtilizationHistory powerHost = new PowerHostUtilizationHistory(hostId, ramProvisioner, bwProvisionerSimple, hostStorage, peList, vmSchedulerTimeShared, powerModel);
            hostList.add(powerHost);

            String arch = "x86";
            String os = "Linux";
            String vmm = "Xen";
            double time_zone = 10.0;
            double cost = 3.0;
            double costPerMem = 0.05;
            double costPerStorage = 0.001;
            double costPerBw = 0.0;
            DatacenterCharacteristics characteristics = new DatacenterCharacteristics(arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);
            VmAllocationPolicy powerVmAllocationPolicy = new PowerVmAllocationMigrationCellularAutomata(hostList);
            PowerDatacenter datacenter = new PowerDatacenter("DataCenter1", characteristics, powerVmAllocationPolicy, storageList, 300);

            PowerDatacenterBroker broker = new PowerDatacenterBroker("Broker");
            int brokerId = broker.getId();

            int mips = 100;
            long size = 10000;
            int ram = 512;
            long bw = 1000;
            int pesNumber = 1;
            File inputFolder = new java.io.File("C:\\Users\\User\\Documents\\Study_Project\\cloudsim"
                    + "\\modules\\cloudsim-examples\\target\\classes\\workload\\planetlab\\20110306");
            File[] files = inputFolder.listFiles();
            long length[] = new long[10];
            for(int i = 0; i < 10; i++) {
                BufferedReader input = new BufferedReader(new FileReader(files[i].getAbsolutePath()));
                int utilization = 0;
                for(int j = 0; j < loadFrequency; j++) {
                    utilization = utilization + Integer.valueOf(input.readLine());
                }
                length[i] = utilization * 150;
            }
            long fileSize = 300;
            long outputSize = 300;
            UtilizationModel utilizationModelNull = new UtilizationModelNull();
            for(int i = 0; i < 10; i++) {
                UtilizationModelPlanetLabInMemory utilizationModelPlanetLabInMemory = new UtilizationModelPlanetLabInMemory(files[i].getAbsolutePath(), Constants.SCHEDULING_INTERVAL);
                Cloudlet cloudlet = new Cloudlet(i, length[i], pesNumber, fileSize, outputSize, utilizationModelPlanetLabInMemory, utilizationModelNull, utilizationModelNull);
                cloudlet.setUserId(brokerId);
                cloudlet.setVmId(i);
                cloudletList.add(cloudlet);
            }
            for(int i = 0; i < 10; i++) {
                CloudletSchedulerDynamicWorkload cloudletSchedulerDynamicWorkload = new CloudletSchedulerDynamicWorkload(100, 1);
                vmlist.add(new PowerVm(i, brokerId, mips, pesNumber, ram, bw, size, 1, vmm, cloudletSchedulerDynamicWorkload, 300));
            }

            Log.printLine(hostList.size());
            Log.printLine(cloudletList.size());
            Log.printLine(vmlist.size());

            broker.submitVmList(vmlist);
            broker.submitCloudletList(cloudletList);

            CloudSim.startSimulation();

            List<Cloudlet> newList = broker.getCloudletReceivedList();
            Log.printLine("Received " + newList.size() + " cloudlets");

            CloudSim.stopSimulation();

            printCloudletList(newList, hostList, datacenter);
            Log.printLine("CloudSimExample1 finished!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Unwanted errors happen");
        }
    }

    private static void printCloudletList(List<Cloudlet> list, List<PowerHostUtilizationHistory> hosts, PowerDatacenter powerDatacenter) {
        int size = list.size();
        Cloudlet cloudlet;

        String indent = "    ";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Cloudlet ID" + indent + "STATUS" + indent + "Data center ID" + indent + "VM ID" + indent + "Start Time" + indent + "Finish Time");

        DecimalFormat dft = new DecimalFormat("###.##");
        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            Log.print(indent + cloudlet.getCloudletId() + indent + indent);

            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                Log.print("SUCCESS");

                Log.printLine(indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId()
                        + indent + indent + dft.format(cloudlet.getExecStartTime()) + indent + indent + dft.format(cloudlet.getFinishTime()));
            }
        }

        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Datacenter ID" + indent + "Energy Consumed");
        double energy = powerDatacenter.getPower() / (3600 * 1000);
        Log.printLine(powerDatacenter.getId() + indent + indent + indent + energy);

    }
    public static void cellularAutomataEvolve() {
        int columns = 10;
        int racks = 10;
        int hosts = 10;
        int [][][]cellularAutomata = new int[columns][racks][hosts];

        for(int column = 0; column < columns; column++) {
            for (int rack = 0; rack < racks; rack++) {
                for (int host = 0; host < hosts; host++) {
                    cellularAutomata[column][rack][host] = new Random().nextInt(2);
                }
            }
        }
        try {
            for(int step = 0; step < 288; step++) {
                String fileName = System.getProperty("user.dir") + "\\Data\\ActiveHostAt" + step + ".txt";
                System.out.println(fileName);
                File myObj = new File(fileName);
                if (myObj.createNewFile()) {
                    System.out.println("File created: " + myObj.getName());
                } else {
                    System.out.println("File already exists.");
                }
                FileWriter fileWriter = new FileWriter(fileName);
                for (int column = 0; column < columns; column++) {
                    for (int rack = 0; rack < racks; rack++) {
                        for (int host = 0; host < hosts; host++) {
                            int cell = 2;
                            int left = 2;
                            int right = 2;
                            int top = 2;
                            int bottom = 2;
                            int front = 2;
                            int back = 2;
                            if (host == hosts - 1) {
                                right = 0;
                            }
                            if (host == 0) {
                                left = 0;
                            }
                            if (rack == racks - 1) {
                                bottom = 0;
                            }
                            if (rack == 0) {
                                top = 0;
                            }
                            if (column == columns - 1) {
                                back = 0;
                            }
                            if (column == 0) {
                                front = 0;
                            }
                            cell = cellularAutomata[column][rack][host];
                            if (right == 2) {
                                right = cellularAutomata[column][rack][host + 1];
                            }
                            if (left == 2) {
                                left = cellularAutomata[column][rack][host - 1];
                            }
                            if (bottom == 2) {
                                bottom = cellularAutomata[column][rack + 1][host];
                            }
                            if (top == 2) {
                                top = cellularAutomata[column][rack - 1][host];
                            }
                            if (back == 2) {
                                back = cellularAutomata[column + 1][rack][host];
                            }
                            if (front == 2) {
                                front = cellularAutomata[column - 1][rack][host];
                            }
                            cellularAutomata[column][rack][host] = rules(cell, right, left, bottom, top, back, front);
                            fileWriter.write(cellularAutomata[column][rack][host] + "\n");
                        }
                    }
                }
                fileWriter.close();
            }
        } catch (Exception exception) {
            System.out.println(exception);
        }
    }

    public static int rules(int cell, int right, int left, int bottom, int top, int back, int front) {
        if(cell + right + left + bottom + top + back + front < 3) {
            return 1;
        }
        return 0;
    }
}