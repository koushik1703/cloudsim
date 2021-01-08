package org.cloudbus.cloudsim.thermal;

import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.VmScheduler;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.provisioners.BwProvisioner;
import org.cloudbus.cloudsim.provisioners.RamProvisioner;

import java.util.List;

public class ThermalHost extends PowerHost {
    private double temperature;
    private double neighbourTemperature;
    private double coolingTemperature;
    private ThermalHost frontHost;
    private ThermalHost backHost;
    private ThermalHost rightHost;
    private ThermalHost leftHost;
    private ThermalHost upHost;
    private ThermalHost downHost;

    public ThermalHost(int id, RamProvisioner ramProvisioner, BwProvisioner bwProvisioner, long storage, List<? extends Pe> peList, VmScheduler vmScheduler, PowerModel powerModel) {
        super(id, ramProvisioner, bwProvisioner, storage, peList, vmScheduler, powerModel);
        setCoolingTemperature(0.2);
        setTemperature(40);
    }

    public void incrementTemperature(double utilization) {
        this.temperature = getTemperature() - getCoolingTemperature() + (0.03 * utilization);
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getTemperature() {
        return this.temperature;
    }

    public void setCoolingTemperature(double coolingTemperature) {
        this.coolingTemperature = coolingTemperature;
    }

    public double getCoolingTemperature() {
        return this.coolingTemperature;
    }

    public void setFrontHost(ThermalHost frontHost) {
        this.frontHost = frontHost;
    }

    public void setBackHost(ThermalHost backHost) {
        this.backHost = backHost;
    }

    public void setLeftHost(ThermalHost leftHost) {
        this.leftHost = leftHost;
    }

    public void setRightHost(ThermalHost rightHost) {
        this.rightHost = rightHost;
    }

    public void setUpHost(ThermalHost upHost) {
        this.upHost = upHost;
    }

    public void setDownHost(ThermalHost downHost) {
        this.downHost = downHost;
    }

    public void getTemperatureFromNeighbour() {
        this.neighbourTemperature = 0;
        if(this.backHost != null) {
            this.neighbourTemperature = this.neighbourTemperature + 0.001 * this.backHost.getTemperature();
        }
        if(this.frontHost != null) {
            this.neighbourTemperature = this.neighbourTemperature + 0.001 * this.frontHost.getTemperature();
        }
        if(this.upHost != null) {
            this.neighbourTemperature = this.neighbourTemperature + 0.001 * this.upHost.getTemperature();
        }
        if(this.downHost != null) {
            this.neighbourTemperature = this.neighbourTemperature + 0.001 * this.downHost.getTemperature();
        }
        if(this.leftHost != null) {
            this.neighbourTemperature = this.neighbourTemperature + 0.001 * this.leftHost.getTemperature();
        }
        if(this.rightHost != null) {
            this.neighbourTemperature = this.neighbourTemperature + 0.001 * this.rightHost.getTemperature();
        }
    }

    public void updateTemperatureFromNeighbour() {
        this.temperature = this.temperature + this.neighbourTemperature;
    }
}
