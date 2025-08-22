package org.paysim.paysim;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.paysim.paysim.parameters.*;

import org.paysim.paysim.actors.Bank;
import org.paysim.paysim.actors.SwissClient;
import org.paysim.paysim.actors.Merchant;
import org.paysim.paysim.actors.Client;
import org.paysim.paysim.actors.Fraudster;
import org.paysim.paysim.actors.networkdrugs.NetworkDrug;
import org.paysim.paysim.parameters.TypologiesFiles;

import org.paysim.paysim.base.ClientActionProfile;
import org.paysim.paysim.base.StepActionProfile;

import org.paysim.paysim.output.Output;

public class SwissSpendingSimulator extends PaySim {
    public static final double SWISS_SIM_VERSION = 1.0;
    private static final String[] DEFAULT_ARGS = new String[]{"", "-file", "SwissSpending.properties", "1"};

    public static void main(String[] args) {
        System.out.println("SWISS SPENDING SIMULATOR v" + SWISS_SIM_VERSION);
        if (args.length < 4) {
            args = DEFAULT_ARGS;
        }
        int nbTimesRepeat = Integer.parseInt(args[3]);
        String propertiesFile = "";
        for (int x = 0; x < args.length - 1; x++) {
            if (args[x].equals("-file")) {
                propertiesFile = args[x + 1];
            }
        }
        Parameters.initParameters(propertiesFile);
        
        for (int i = 0; i < nbTimesRepeat; i++) {
            SwissSpendingSimulator p = new SwissSpendingSimulator();
            p.runSimulation();
        }
    }

    public SwissSpendingSimulator() {
        super();
        
        // Override the simulation name for Swiss spending
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            Date currentTime = new Date();
            String swissName = "SWISS_" + dateFormat.format(currentTime) + "_" + seed();
            
            // Use reflection to set the final field
            java.lang.reflect.Field nameField = PaySim.class.getDeclaredField("simulationName");
            nameField.setAccessible(true);
            nameField.set(this, swissName);
            
            File simulationFolder = new File(Parameters.outputPath + swissName);
            simulationFolder.mkdirs();
            
            Output.initOutputFilenames(swissName);
            Output.writeParameters(seed());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected void initActors() {
        System.out.println("Init - Seed " + seed());

        // LARGE OUTPUT VERSION: Use hardcoded large parameters for ~1M lines
        int largeNbMerchants = 10000;
        int largeNbClients = 20000;
        int largeNbBanks = 20;
        int largeMultiplier = 20;

        //Add the merchants
        System.out.println("NbMerchants: " + largeNbMerchants);
        for (int i = 0; i < largeNbMerchants; i++) {
            Merchant m = new Merchant(generateId());
            merchants.add(m);
        }

        //Add the fraudsters
        System.out.println("NbFraudsters: " + (int) (Parameters.nbFraudsters * largeMultiplier));
        for (int i = 0; i < Parameters.nbFraudsters * largeMultiplier; i++) {
            Fraudster f = new Fraudster(generateId());
            fraudsters.add(f);
            schedule.scheduleRepeating(f);
        }

        //Add the banks
        System.out.println("NbBanks: " + largeNbBanks);
        for (int i = 0; i < largeNbBanks; i++) {
            Bank b = new Bank(generateId());
            banks.add(b);
        }

        //Add the Swiss clients instead of regular clients
        System.out.println("NbClients: " + largeNbClients);
        for (int i = 0; i < largeNbClients; i++) {
            SwissClient c = new SwissClient(this);
            clients.add(c);
        }

        // Skip drug network creation for Swiss spending simulator
        // NetworkDrug.createNetwork(this, Parameters.typologiesFolder + TypologiesFiles.drugNetworkOne);

        // Do not write code under this part otherwise clients will not be used in simulation
        // Schedule clients to act at each step of the simulation
        for (Client c : clients) {
            schedule.scheduleRepeating(c);
        }
    }
}
