package org.cloudbus.cloudsim.thermal;

import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.VmScheduler;
import org.cloudbus.cloudsim.power.PowerVm;
import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.provisioners.BwProvisioner;
import org.cloudbus.cloudsim.provisioners.RamProvisioner;
import org.cloudbus.cloudsim.util.MathUtil;

import java.util.List;

public class ThermalHostUtilizationHistory extends ThermalHost {

    public ThermalHostUtilizationHistory(int id, RamProvisioner ramProvisioner, BwProvisioner bwProvisioner, long storage, List<? extends Pe> peList, VmScheduler vmScheduler, PowerModel powerModel, int temperature, double thermalFactor, ThermalModel thermalModel) {
        super(id, ramProvisioner, bwProvisioner, storage, peList, vmScheduler, powerModel, temperature, thermalFactor, thermalModel);
    }

    protected double[] getUtilizationHistory() {
        double[] utilizationHistory = new double[PowerVm.HISTORY_LENGTH];
        double hostMips = getTotalMips();
        for (ThermalVm vm : this.<ThermalVm> getVmList()) {
            for (int i = 0; i < vm.getUtilizationHistory().size(); i++) {
                utilizationHistory[i] += vm.getUtilizationHistory().get(i) * vm.getMips() / hostMips;
            }
        }
        return MathUtil.trimZeroTail(utilizationHistory);
    }
}
