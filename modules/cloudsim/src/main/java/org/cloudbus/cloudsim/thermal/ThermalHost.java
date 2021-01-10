package org.cloudbus.cloudsim.thermal;

import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.VmScheduler;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.provisioners.BwProvisioner;
import org.cloudbus.cloudsim.provisioners.RamProvisioner;

import java.util.List;

public class ThermalHost extends PowerHost {
    private ThermalModel thermalModel;
    private double temperature;
    private double thermalFactor;
    private double neighbourTemperature;
    private ThermalHost rightHost;
    private ThermalHost leftHost;

    public ThermalHost(int id, RamProvisioner ramProvisioner, BwProvisioner bwProvisioner, long storage, List<? extends Pe> peList, VmScheduler vmScheduler, PowerModel powerModel, double temperature, double thermalFactor, ThermalModel thermalModel) {
        super(id, ramProvisioner, bwProvisioner, storage, peList, vmScheduler, powerModel);
        setTemperature(temperature);
        setThermalFactor(thermalFactor);
        setThermalModel(thermalModel);
    }

    @Override
    public double getEnergyLinearInterpolation(double fromUtilization, double toUtilization, double time) {
        if (fromUtilization == 0) {
            return 0;
        }
        double fromPower = getPower(fromUtilization);
        double toPower = getPower(toUtilization);
        return ((fromPower + (toPower - fromPower) / 2) + getTemperature()) * time;
    }

    public void getTemperatureFromNeighbour() {
        this.neighbourTemperature = 0;
        if(this.leftHost != null) {
            this.neighbourTemperature = this.neighbourTemperature + getThermalFactor() * this.leftHost.getTemperature();
        }
        if(this.rightHost != null) {
            this.neighbourTemperature = this.neighbourTemperature + getThermalFactor() * this.rightHost.getTemperature();
        }
    }

    public void updateTemperatureFromNeighbour() {
        this.temperature = this.temperature + this.neighbourTemperature;
    }

    public void incrementTemperature(double utilization) {
        this.temperature = getTemperature() + thermalModel.getTemperature(utilization);
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getTemperature() {
        return this.temperature;
    }

    public void setLeftHost(ThermalHost leftHost) {
        this.leftHost = leftHost;
    }

    public void setRightHost(ThermalHost rightHost) {
        this.rightHost = rightHost;
    }

    public void setThermalFactor(double thermalFactor) {
        this.thermalFactor = thermalFactor;
    }

    public double getThermalFactor() {
        return this.thermalFactor;
    }

    public void setThermalModel(ThermalModel thermalModel) {
        this.thermalModel = thermalModel;
    }

    public ThermalModel getThermalModel() {
        return thermalModel;
    }
}
