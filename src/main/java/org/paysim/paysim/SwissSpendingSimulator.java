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
            p.start();
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
}
