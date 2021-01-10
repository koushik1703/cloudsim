package org.cloudbus.cloudsim.thermal;

public class ThermalModelCubic implements ThermalModel {
    private double constant;

    public ThermalModelCubic(double constant) {
        setConstant(constant);
    }

    @Override
    public double getTemperature(double utilization) throws IllegalArgumentException {
        if (utilization < 0 || utilization > 1) {
            throw new IllegalArgumentException("Utilization value must be between 0 and 1");
        }
        if (utilization == 0) {
            return 0;
        }
        return getConstant() * Math.pow(utilization, 3);
    }

    private void setConstant(double constant) {
        this.constant = constant;
    }

    private double getConstant() {
        return constant;
    }
}
