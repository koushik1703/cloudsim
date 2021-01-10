package org.cloudbus.cloudsim.thermal;

public interface ThermalModel {
    double getTemperature(double utilization) throws IllegalArgumentException;
}
