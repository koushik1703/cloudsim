package org.cloudbus.cloudsim.examples.thermal.helper;

import java.io.File;
import java.io.FileWriter;
import java.util.Random;

public class CellularAutomaton {
    public static void evolve(int numberOfColumns, int numberOfRacks, int numberOfHosts, int steps) {
        int [][][]cellularAutomata = new int[numberOfColumns][numberOfRacks][numberOfHosts];
        String dir = "user.dir";
        String path = "\\Data\\ActiveHostAt";
        String extension = ".txt";
        int firstColumn = 0;
        int firstRack = 0;
        int firstHost = 0;
        int lastColumn = numberOfColumns - 1;
        int lastRack = numberOfRacks - 1;
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
                if(myObj.createNewFile()) {
                    FileWriter fileWriter = new FileWriter(fileName);
                    for (int column = 0; column < numberOfColumns; column++) {
                        for (int rack = 0; rack < numberOfRacks; rack++) {
                            for (int host = 0; host < numberOfRacks; host++) {
                                int left = 0;
                                int right = 0;
                                int top = 0;
                                int bottom = 0;
                                int front = 0;
                                int back = 0;
                                if (host != lastHost) {
                                    right = cellularAutomata[column][rack][host + 1];
                                }
                                if (host != firstHost) {
                                    left = cellularAutomata[column][rack][host - 1];
                                }
                                if (rack != lastRack) {
                                    bottom = cellularAutomata[column][rack + 1][host];
                                }
                                if (rack != firstRack) {
                                    top = cellularAutomata[column][rack - 1][host];
                                }
                                if (column != lastColumn) {
                                    back = cellularAutomata[column + 1][rack][host];
                                }
                                if (column != firstColumn) {
                                    front = cellularAutomata[column - 1][rack][host];
                                }
                                int cell = cellularAutomata[column][rack][host];
                                cellularAutomata[column][rack][host] = rules(cell, right, left, bottom, top, back, front);
                                fileWriter.write(cellularAutomata[column][rack][host] + "\n");
                            }
                        }
                    }
                    fileWriter.close();
                }
            }
        } catch (Exception exception) {
            System.out.println("Error");
        }
    }

    public static int rules(int cell, int right, int left, int bottom, int top, int back, int front) {
        if(cell + right + left + bottom + top + back + front < 3) {
            return 1;
        }
        return 0;
    }
}
