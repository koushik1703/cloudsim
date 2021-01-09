package org.cloudbus.cloudsim.examples.thermal.helper;

import java.io.File;
import java.io.FileWriter;
import java.util.Random;

public class CellularAutomaton {
    public static void evolve(int numberOfColumns, int numberOfRacks, int numberOfHosts, int steps) {
        int [][][]cellularAutomata = new int[numberOfColumns][numberOfRacks][numberOfHosts];
        String dir = "user.dir";
        String path = "\\Data\\ActiveHost\\ActiveHostAt";
        String extension = ".txt";
        int firstHost = 0;
        int lastHost = numberOfHosts - 1;

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
        if(cell + right + left < 2) {
            return 1;
        }
        return 0;
    }
}
