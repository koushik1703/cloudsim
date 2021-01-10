package org.cloudbus.cloudsim.examples.thermal.helper;

import java.io.File;
import java.io.FileWriter;
import java.util.Random;

public class CellularAutomaton {
    static int[] ruleArray;

    public static void evolve(int numberOfColumns, int numberOfRacks, int numberOfHosts, int rule, int steps) {
        int [][][]cellularAutomata = new int[numberOfColumns][numberOfRacks][numberOfHosts];
        String dir = "user.dir";
        String path = "\\data\\ActiveHost\\ActiveHostAt";
        String extension = ".txt";
        int firstHost = 0;
        int lastHost = numberOfHosts - 1;
        convertToRuleArray(rule);

        for(int column = 0; column < numberOfColumns; column++) {
            for (int rack = 0; rack < numberOfRacks; rack++) {
                for (int host = 0; host < numberOfHosts; host++) {
                    cellularAutomata[column][rack][host] = new Random().nextInt(2);
                }
            }
        }

        try {
            for(int step = 0; step < steps; step++) {
                String fileName = System.getProperty(dir) + path + step + extension;
                File myObj = new File(fileName);
                myObj.createNewFile();
                FileWriter fileWriter = new FileWriter(fileName);
                for (int column = 0; column < numberOfColumns; column++) {
                    for (int rack = 0; rack < numberOfRacks; rack++) {
                        for (int host = 0; host < numberOfHosts; host++) {
                            int left = 0;
                            int right = 0;
                            if (host != lastHost) {
                                right = cellularAutomata[column][rack][host + 1];
                            }
                            if (host != firstHost) {
                                left = cellularAutomata[column][rack][host - 1];
                            }
                            int cell = cellularAutomata[column][rack][host];
                            cellularAutomata[column][rack][host] = rules(cell, right, left);
                            fileWriter.write(cellularAutomata[column][rack][host] + "\n");
                        }
                    }
                }
                fileWriter.close();
            }
        } catch (Exception exception) {
            System.out.println("Error");
        }
    }

    public static int rules(int cell, int right, int left) {
        if(cell == 0 && right == 0 && left == 0) {
            return ruleArray[0];
        }
        if(cell == 0 && right == 0 && left == 1) {
            return ruleArray[1];
        }
        if(cell == 0 && right == 1 && left == 0) {
            return ruleArray[2];
        }
        if(cell == 0 && right == 1 && left == 1) {
            return ruleArray[3];
        }
        if(cell == 1 && right == 0 && left == 0) {
            return ruleArray[4];
        }
        if(cell == 1 && right == 0 && left == 1) {
            return ruleArray[5];
        }
        if(cell == 1 && right == 1 && left == 0) {
            return ruleArray[6];
        }
        if(cell == 1 && right == 1 && left == 1) {
            return ruleArray[7];
        }
        return new Random().nextInt(2);
    }

    public static void convertToRuleArray(int rule) {
        ruleArray = new int[8];
        for(int i = 0; i < 8; i++) {
            ruleArray[i] = 0;
        }
        String binary = Integer.toBinaryString(rule);
        int diff = 8 - binary.length();
        for(int i = 0; i < binary.length(); i++) {
            ruleArray[diff + i] = binary.charAt(i) - 48;
        }
    }
}
