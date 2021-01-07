/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.power;

import java.util.List;

import org.cloudbus.cloudsim.HostDynamicWorkload;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.VmScheduler;
import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.provisioners.BwProvisioner;
import org.cloudbus.cloudsim.provisioners.RamProvisioner;

/**
 * PowerHost class enables simulation of power-aware hosts.
 * 
 * <br/>If you are using any algorithms, policies or workload included in the power package please cite
 * the following paper:<br/>
 * 
 * <ul>
 * <li><a href="http://dx.doi.org/10.1002/cpe.1867">Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in
 * Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24,
 * Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012</a>
 * </ul>
 * 
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public class PowerHost extends HostDynamicWorkload {

	/** The power model used by the host. */
	private PowerModel powerModel;
	private double temperature;
	private double neighbourTemperature;
	private double coolingTemperature;
	private PowerHost frontHost;
	private PowerHost backHost;
	private PowerHost rightHost;
	private PowerHost leftHost;
	private PowerHost upHost;
	private PowerHost downHost;

	/**
	 * Instantiates a new PowerHost.
	 * 
	 * @param id the id of the host
	 * @param ramProvisioner the ram provisioner
	 * @param bwProvisioner the bw provisioner
	 * @param storage the storage capacity
	 * @param peList the host's PEs list
	 * @param vmScheduler the VM scheduler
	 */
	public PowerHost(
			int id,
			RamProvisioner ramProvisioner,
			BwProvisioner bwProvisioner,
			long storage,
			List<? extends Pe> peList,
			VmScheduler vmScheduler,
			PowerModel powerModel) {
		super(id, ramProvisioner, bwProvisioner, storage, peList, vmScheduler);
		setPowerModel(powerModel);
		setCoolingTemperature(0.2);
		setTemperature(40);
	}

	/**
	 * Gets the power. For this moment only consumed by all PEs.
	 * 
	 * @return the power
	 */
	public double getPower() {
		return getPower(getUtilizationOfCpu());
	}

	/**
	 * Gets the current power consumption of the host. For this moment only consumed by all PEs.
	 * 
	 * @param utilization the utilization percentage (between [0 and 1]) of a resource that
         * is critical for power consumption
	 * @return the power consumption
	 */
	protected double getPower(double utilization) {
		double power = 0;
		try {
			power = getPowerModel().getPower(utilization);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		return power;
	}

	/**
	 * Gets the max power that can be consumed by the host.
	 * 
	 * @return the max power
	 */
	public double getMaxPower() {
		double power = 0;
		try {
			power = getPowerModel().getPower(1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		return power;
	}

	/**
	 * Gets the energy consumption using linear interpolation of the utilization change.
	 * 
	 * @param fromUtilization the initial utilization percentage
	 * @param toUtilization the final utilization percentage
	 * @param time the time
	 * @return the energy
	 */
	public double getEnergyLinearInterpolation(double fromUtilization, double toUtilization, double time) {
		if (fromUtilization == 0) {
			return 0;
		}
		double fromPower = getPower(fromUtilization);
		double toPower = getPower(toUtilization);
		return ((fromPower + (toPower - fromPower) / 2) + (0.2 * temperature)) * time;
	}

	/**
	 * Sets the power model.
	 * 
	 * @param powerModel the new power model
	 */
	protected void setPowerModel(PowerModel powerModel) {
		this.powerModel = powerModel;
	}

	/**
	 * Gets the power model.
	 * 
	 * @return the power model
	 */
	public PowerModel getPowerModel() {
		return powerModel;
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

	public void setFrontHost(PowerHost frontHost) {
		this.frontHost = frontHost;
	}

	public void setBackHost(PowerHost backHost) {
		this.backHost = backHost;
	}

	public void setLeftHost(PowerHost leftHost) {
		this.leftHost = leftHost;
	}

	public void setRightHost(PowerHost rightHost) {
		this.rightHost = rightHost;
	}

	public void setUpHost(PowerHost upHost) {
		this.upHost = upHost;
	}

	public void setDownHost(PowerHost downHost) {
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
