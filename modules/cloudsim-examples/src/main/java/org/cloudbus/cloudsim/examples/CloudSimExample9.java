package org.cloudbus.cloudsim.examples;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

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
            int num_user = 1;
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false;
            CloudSim.init(num_user, calendar, trace_flag);

            List<PowerHost> hostList = new ArrayList<PowerHost>();
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

            int numberOfColumns = 6;
            int numberOfRacks = 6;
            int numberOfHosts = 6;
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
                        PowerHost powerHost = new PowerHost(hostId, ramProvisioner, bwProvisionerSimple, hostStorage, peList, vmSchedulerTimeShared, powerModel);
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
            PowerHost powerHost = new PowerHost(hostId, ramProvisioner, bwProvisionerSimple, hostStorage, peList, vmSchedulerTimeShared, powerModel);
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
            PowerDatacenter datacenter = new PowerDatacenter("DataCenter1", characteristics, new PowerVmAllocationPolicySimple(hostList), storageList, 300);

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
            long length[] = new long[files.length];
            for(int i = 0; i < files.length; i++) {
                BufferedReader input = new BufferedReader(new FileReader(files[i].getAbsolutePath()));
                int utilization = 0;
                for(int j = 0; j < loadFrequency; j++) {
                    utilization = utilization + Integer.valueOf(input.readLine());
                }
                length[i] = utilization * 300;
            }
            long fileSize = 300;
            long outputSize = 300;
            UtilizationModel utilizationModelNull = new UtilizationModelNull();
            for(int i = 0; i < files.length; i++) {
                UtilizationModelPlanetLabInMemory utilizationModelPlanetLabInMemory = new UtilizationModelPlanetLabInMemory(files[i].getAbsolutePath(), Constants.SCHEDULING_INTERVAL);
                Cloudlet cloudlet = new Cloudlet(i, length[i], pesNumber, fileSize, outputSize, utilizationModelPlanetLabInMemory, utilizationModelNull, utilizationModelNull);
                cloudlet.setUserId(brokerId);
                cloudlet.setVmId(i);
                cloudletList.add(cloudlet);
            }
            for(int i = 0; i < files.length; i++) {
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

            printCloudletList(newList, hostList);
            Log.printLine("CloudSimExample1 finished!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Unwanted errors happen");
        }
    }

    private static void printCloudletList(List<Cloudlet> list, List<PowerHost> hosts) {
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
        Log.printLine("host ID" + indent + "Temperature");

        for(int j = 0; j < hosts.size(); j++) {
            Log.printLine(indent + indent + hosts.get(j).getId() + indent + indent + indent + hosts.get(j).getTemperature());
        }
    }
}