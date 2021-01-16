package org.cloudbus.cloudsim.examples.thermal;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.examples.thermal.helper.*;
import org.cloudbus.cloudsim.thermal.*;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class Example {
    public static void main(String[] args) {
        int numberOfColumns = 10;
        int numberOfRacks = 10;
        int numberOfHosts = 10;
        int loadFrequency = 288;
        boolean trace_flag = false;
        int num_user = 1;

        try {
            String fileName = System.getProperty("user.dir") + "\\data\\Output\\output" + ".xml";
            File myObj = new File(fileName);
            myObj.createNewFile();
            FileWriter fileWriter = new FileWriter(fileName);
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element root = document.createElement("EnergyConsumption");
            document.appendChild(root);
            for(int numberOfVm = 200; numberOfVm < 800; numberOfVm = numberOfVm + 100) {
                Element vmElement = document.createElement("NumberOfVm" + numberOfVm);
                root.appendChild(vmElement);
                for (int rule = 0; rule < 256; rule++) {
                    try {
                        CellularAutomaton.evolve(numberOfColumns, numberOfRacks, numberOfHosts, rule, (loadFrequency / 2));

                        Calendar calendar = Calendar.getInstance();
                        CloudSim.init(num_user, calendar, trace_flag);

                        ThermalDataCenterBroker broker = new ThermalDataCenterBroker("Broker");

                        List<ThermalHostUtilizationHistory> hostList = ThermalHostHelper.createHosts(numberOfColumns, numberOfRacks, numberOfHosts);
                        List<Cloudlet> cloudLetList = ThermalCloudLetHelper.createCloudLet(numberOfVm, loadFrequency, broker.getId());
                        List<ThermalVm> vmList = ThermalVmHelper.createVm(numberOfVm, broker.getId());

                        ThermalDataCenter dataCenter = ThermalDataCenterHelper.createDataCenter(hostList);

                        broker.submitVmList(vmList);
                        broker.submitCloudletList(cloudLetList);

                        CloudSim.startSimulation();

                        List<Cloudlet> newList = broker.getCloudletReceivedList();
                        Log.printLine("Received " + newList.size() + " cloudLets");

                        CloudSim.stopSimulation();

                        Printer.printCloudLetList(newList, dataCenter, rule, document, vmElement);

                        TransformerFactory transformerFactory = TransformerFactory.newInstance();
                        Transformer transformer = transformerFactory.newTransformer();
                        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                        DOMSource domSource = new DOMSource(document);
                        StreamResult streamResult = new StreamResult(new File(fileName));

                        transformer.transform(domSource, streamResult);

                        Log.printLine("CloudSimExample1 finished!");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.printLine("Unwanted errors happen");
                    }
                }
            }
            fileWriter.close();
        } catch (IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
}
