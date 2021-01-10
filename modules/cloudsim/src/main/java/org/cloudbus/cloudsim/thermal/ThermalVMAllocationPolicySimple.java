package org.cloudbus.cloudsim.thermal;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicySimple;

import java.util.List;
import java.util.Map;

public class ThermalVMAllocationPolicySimple extends PowerVmAllocationPolicySimple {

    public ThermalVMAllocationPolicySimple(List<? extends Host> list) {
        super(list);
    }

    @Override
    public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> vmList) {
        return null;
    }
}
