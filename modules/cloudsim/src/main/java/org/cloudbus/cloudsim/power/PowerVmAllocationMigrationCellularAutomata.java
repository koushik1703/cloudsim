/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.power;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.HostDynamicWorkload;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.lists.PowerVmList;
import org.cloudbus.cloudsim.util.ExecutionTimeMeasurer;

public class PowerVmAllocationMigrationCellularAutomata extends PowerVmAllocationPolicyAbstract {

    private final List<Map<String, Object>> savedAllocation = new ArrayList<Map<String, Object>>();

    private final Map<Integer, List<Double>> utilizationHistory = new HashMap<Integer, List<Double>>();

    private final Map<Integer, List<Double>> metricHistory = new HashMap<Integer, List<Double>>();

    private final Map<Integer, List<Double>> timeHistory = new HashMap<Integer, List<Double>>();

    private final List<Double> executionTimeHistoryVmSelection = new LinkedList<Double>();

    private final List<Double> executionTimeHistoryHostSelection = new LinkedList<Double>();

    private final List<Double> executionTimeHistoryVmReallocation = new LinkedList<Double>();

    private final List<Double> executionTimeHistoryTotal = new LinkedList<Double>();

    private int currentAllocation;

    public PowerVmAllocationMigrationCellularAutomata(List<? extends Host> hostList) {
        super(hostList);
        currentAllocation = 0;
    }

    @Override
    public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> vmList) {
        ExecutionTimeMeasurer.start("optimizeAllocationTotal");

        ExecutionTimeMeasurer.start("optimizeAllocationHostSelection");
        List<PowerHostUtilizationHistory> currentShutDownHosts = getCurrentShutDownHosts();
        getExecutionTimeHistoryHostSelection().add(ExecutionTimeMeasurer.end("optimizeAllocationHostSelection"));

        printCurrentShutDownHosts(currentShutDownHosts);

        saveAllocation();

        ExecutionTimeMeasurer.start("optimizeAllocationVmSelection");
        List<? extends Vm> vmsToMigrate = getVmsToMigrateFromHosts(currentShutDownHosts);
        getExecutionTimeHistoryVmSelection().add(ExecutionTimeMeasurer.end("optimizeAllocationVmSelection"));

        Log.printLine("Reallocation of VMs from the Shutdown hosts:");
        ExecutionTimeMeasurer.start("optimizeAllocationVmReallocation");
        List<Map<String, Object>> migrationMap = getNewVmPlacement(vmsToMigrate, new HashSet<Host>(currentShutDownHosts));
        getExecutionTimeHistoryVmReallocation().add(ExecutionTimeMeasurer.end("optimizeAllocationVmReallocation"));
        Log.printLine();

        restoreAllocation();

        getExecutionTimeHistoryTotal().add(ExecutionTimeMeasurer.end("optimizeAllocationTotal"));

        return migrationMap;
    }

    protected void printCurrentShutDownHosts(List<PowerHostUtilizationHistory> currentShutDownHosts) {
        if (!Log.isDisabled()) {
            Log.printLine("Shut down hosts:");
            for (PowerHostUtilizationHistory host : currentShutDownHosts) {
                Log.printConcatLine("Host #", host.getId());
            }
            Log.printLine();
        }
    }

    public PowerHost findHostForVm(Vm vm, Set<? extends Host> excludedHosts) {
        double minPower = Double.MAX_VALUE;
        PowerHost allocatedHost = null;

        for (PowerHost host : this.<PowerHost> getHostList()) {
            if (excludedHosts.contains(host)) {
                continue;
            }
            if (host.isSuitableForVm(vm)) {
                if (getUtilizationOfCpuMips(host) != 0 && isHostOverUtilizedAfterAllocation(host, vm)) {
                    continue;
                }

                try {
                    double powerAfterAllocation = getPowerAfterAllocation(host, vm);
                    if (powerAfterAllocation != -1) {
                        double powerDiff = powerAfterAllocation - host.getPower();
                        if (powerDiff < minPower) {
                            minPower = powerDiff;
                            allocatedHost = host;
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
        return allocatedHost;
    }

    protected boolean isHostOverUtilizedAfterAllocation(PowerHost host, Vm vm) {
        boolean isHostOverUtilizedAfterAllocation = true;
        isHostOverUtilizedAfterAllocation = isHostOverUtilized(host, vm);
        host.vmDestroy(vm);
        return isHostOverUtilizedAfterAllocation;
    }

    @Override
    public PowerHost findHostForVm(Vm vm) {
        Set<Host> excludedHosts = new HashSet<Host>();
        if (vm.getHost() != null) {
            excludedHosts.add(vm.getHost());
        }
        return findHostForVm(vm, excludedHosts);
    }

    protected List<Map<String, Object>> getNewVmPlacement(List<? extends Vm> vmsToMigrate, Set<? extends Host> excludedHosts) {
        List<Map<String, Object>> migrationMap = new LinkedList<Map<String, Object>>();
        PowerVmList.sortByCpuUtilization(vmsToMigrate);
        for (Vm vm : vmsToMigrate) {
            PowerHost allocatedHost = findHostForVm(vm, excludedHosts);
            if (allocatedHost != null) {
                allocatedHost.vmCreate(vm);
                Log.printConcatLine("VM #", vm.getId(), " allocated to host #", allocatedHost.getId());

                Map<String, Object> migrate = new HashMap<String, Object>();
                migrate.put("vm", vm);
                migrate.put("host", allocatedHost);
                migrationMap.add(migrate);
            }
        }
        return migrationMap;
    }

    protected List<? extends Vm> getVmsToMigrateFromHosts(List<PowerHostUtilizationHistory> overUtilizedHosts) {
        List<Vm> vmsToMigrate = new LinkedList<Vm>();
        for (PowerHostUtilizationHistory host : overUtilizedHosts) {
            List<Vm> vmsToRemoveFromHost = new LinkedList<Vm>();
            for(Vm vm : host.getVmList()) {
                vmsToMigrate.add(vm);
                vmsToRemoveFromHost.add(vm);
            }
            for(Vm vm : vmsToRemoveFromHost) {
                host.vmDestroy(vm);
            }
        }
        return vmsToMigrate;
    }

    protected List<PowerHostUtilizationHistory> getCurrentShutDownHosts() {
        List<PowerHostUtilizationHistory> currentShutDownHosts = new LinkedList<PowerHostUtilizationHistory>();
        String fileName = System.getProperty("user.dir") + "\\Data\\ActiveHostAt" + currentAllocation + ".txt";
        List<String> allocations = null;
        try {
            allocations = Files.readAllLines(Paths.get(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (PowerHostUtilizationHistory host : this.<PowerHostUtilizationHistory> getHostList()) {
            if (allocations.get(host.getId()).equals("0")) {
                currentShutDownHosts.add(host);
            }
        }

        return currentShutDownHosts;
    }

    protected boolean isHostOverUtilized(PowerHost host, Vm vm) {
        double requestedTotalMips = vm.getCurrentRequestedTotalMips();
        double hostUtilizationMips = getUtilizationOfCpuMips(host);
        double hostPotentialUtilizationMips = hostUtilizationMips + requestedTotalMips;
        double pePotentialUtilization = hostPotentialUtilizationMips / host.getTotalMips();
        return pePotentialUtilization > 1;
    }

    protected void addHistoryEntry(HostDynamicWorkload host, double metric) {
        int hostId = host.getId();
        if (!getTimeHistory().containsKey(hostId)) {
            getTimeHistory().put(hostId, new LinkedList<Double>());
        }
        if (!getUtilizationHistory().containsKey(hostId)) {
            getUtilizationHistory().put(hostId, new LinkedList<Double>());
        }
        if (!getMetricHistory().containsKey(hostId)) {
            getMetricHistory().put(hostId, new LinkedList<Double>());
        }
        if (!getTimeHistory().get(hostId).contains(CloudSim.clock())) {
            getTimeHistory().get(hostId).add(CloudSim.clock());
            getUtilizationHistory().get(hostId).add(host.getUtilizationOfCpu());
            getMetricHistory().get(hostId).add(metric);
        }
    }

    protected void saveAllocation() {
        getSavedAllocation().clear();
        for (Host host : getHostList()) {
            for (Vm vm : host.getVmList()) {
                if (host.getVmsMigratingIn().contains(vm)) {
                    continue;
                }
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("host", host);
                map.put("vm", vm);
                getSavedAllocation().add(map);
            }
        }
    }

    protected void restoreAllocation() {
        for (Host host : getHostList()) {
            host.vmDestroyAll();
            host.reallocateMigratingInVms();
        }
        for (Map<String, Object> map : getSavedAllocation()) {
            Vm vm = (Vm) map.get("vm");
            PowerHost host = (PowerHost) map.get("host");
            if (!host.vmCreate(vm)) {
                Log.printConcatLine("Couldn't restore VM #", vm.getId(), " on host #", host.getId());
                System.exit(0);
            }
            getVmTable().put(vm.getUid(), host);
        }
    }

    protected double getPowerAfterAllocation(PowerHost host, Vm vm) {
        double power = 0;
        try {
            power = host.getPowerModel().getPower(getMaxUtilizationAfterAllocation(host, vm));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        return power;
    }

    protected double getMaxUtilizationAfterAllocation(PowerHost host, Vm vm) {
        double requestedTotalMips = vm.getCurrentRequestedTotalMips();
        double hostUtilizationMips = getUtilizationOfCpuMips(host);
        double hostPotentialUtilizationMips = hostUtilizationMips + requestedTotalMips;
        double pePotentialUtilization = hostPotentialUtilizationMips / host.getTotalMips();
        return pePotentialUtilization;
    }

    protected double getUtilizationOfCpuMips(PowerHost host) {
        double hostUtilizationMips = 0;
        for (Vm vm2 : host.getVmList()) {
            if (host.getVmsMigratingIn().contains(vm2)) {
                // calculate additional potential CPU usage of a migrating in VM
                hostUtilizationMips += host.getTotalAllocatedMipsForVm(vm2) * 0.9 / 0.1;
            }
            hostUtilizationMips += host.getTotalAllocatedMipsForVm(vm2);
        }
        return hostUtilizationMips;
    }

    protected List<Map<String, Object>> getSavedAllocation() {
        return savedAllocation;
    }

    public Map<Integer, List<Double>> getUtilizationHistory() {
        return utilizationHistory;
    }

    public Map<Integer, List<Double>> getMetricHistory() {
        return metricHistory;
    }

    public Map<Integer, List<Double>> getTimeHistory() {
        return timeHistory;
    }

    public List<Double> getExecutionTimeHistoryVmSelection() {
        return executionTimeHistoryVmSelection;
    }

    public List<Double> getExecutionTimeHistoryHostSelection() {
        return executionTimeHistoryHostSelection;
    }

    public List<Double> getExecutionTimeHistoryVmReallocation() {
        return executionTimeHistoryVmReallocation;
    }

    public List<Double> getExecutionTimeHistoryTotal() {
        return executionTimeHistoryTotal;
    }
}
