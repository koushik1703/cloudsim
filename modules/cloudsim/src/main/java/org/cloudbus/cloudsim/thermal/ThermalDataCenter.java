package org.cloudbus.cloudsim.thermal;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerDatacenter;

import java.util.List;

public class ThermalDataCenter extends PowerDatacenter {

    public ThermalDataCenter(String name, DatacenterCharacteristics characteristics, VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList, double schedulingInterval) throws Exception {
        super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval);
    }

    @Override
    protected double updateCloudetProcessingWithoutSchedulingFutureEventsForce() {
        double currentTime = CloudSim.clock();
        double minTime = Double.MAX_VALUE;
        double timeDiff = currentTime - getLastProcessTime();
        double timeFrameDatacenterEnergy = 0.0;

        Log.printLine("\n\n--------------------------------------------------------------\n\n");
        Log.formatLine("New resource usage for the time frame starting at %.2f:", currentTime);

        for (ThermalHost host : this.<ThermalHost> getHostList()) {
            Log.printLine();

            double time = host.updateVmsProcessing(currentTime); // inform VMs to update processing
            if (time < minTime) {
                minTime = time;
            }

            Log.formatLine("%.2f: [Host #%d] utilization is %.2f%%", currentTime, host.getId(), host.getUtilizationOfCpu() * 100);
        }

        for(ThermalHost host : this.<ThermalHost> getHostList()) {
            host.getTemperatureFromNeighbour();
        }

        for(ThermalHost host : this.<ThermalHost> getHostList()) {
            host.updateTemperatureFromNeighbour();
        }

        if (timeDiff > 0) {
            Log.formatLine("\nEnergy consumption for the last time frame from %.2f to %.2f:", getLastProcessTime(), currentTime);

            for (ThermalHost host : this.<ThermalHost> getHostList()) {
                host.incrementTemperature(host.getPreviousUtilizationOfCpu());
                double previousUtilizationOfCpu = host.getPreviousUtilizationOfCpu();
                double utilizationOfCpu = host.getUtilizationOfCpu();
                double timeFrameHostEnergy = host.getEnergyLinearInterpolation(previousUtilizationOfCpu, utilizationOfCpu, timeDiff);
                timeFrameDatacenterEnergy += timeFrameHostEnergy;

                Log.printLine();
                Log.formatLine("%.2f: [Host #%d] utilization at %.2f was %.2f%%, now is %.2f%%", currentTime, host.getId(), getLastProcessTime(), previousUtilizationOfCpu * 100, utilizationOfCpu * 100);
                Log.formatLine("%.2f: [Host #%d] energy is %.2f W*sec", currentTime, host.getId(), timeFrameHostEnergy);
            }

            Log.formatLine("\n%.2f: Data center's energy is %.2f W*sec\n", currentTime, timeFrameDatacenterEnergy);
        }

        setPower(getPower() + timeFrameDatacenterEnergy);

        checkCloudletCompletion();

        /** Remove completed VMs **/
        for (ThermalHost host : this.<ThermalHost> getHostList()) {
            for (Vm vm : host.getCompletedVms()) {
                getVmAllocationPolicy().deallocateHostForVm(vm);
                getVmList().remove(vm);
                Log.printLine("VM #" + vm.getId() + " has been deallocated from host #" + host.getId());
            }
        }

        Log.printLine();

        setLastProcessTime(currentTime);
        return minTime;
    }
}
