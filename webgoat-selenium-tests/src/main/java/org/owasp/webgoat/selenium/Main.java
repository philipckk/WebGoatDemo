package org.owasp.webgoat.selenium;

import org.apache.commons.cli.*;



public class Main {

    static private String driver = "/opt/google/chrome/chromedriver"; // TODO: For now we rely on Chrome Driver
    static private String url = "http://localhost:8080/WebGoat"; // Default URL
    static private String user = "webgoat"; // Default username
    static private String password = "webgoat"; // Default password
    static private boolean register = false;
    static private boolean verbose = false;


    public static void main(String[] args) {


        Options options = new Options();
        options.addOption("d", "driver", true, "Chrome driver for selenium")
                .addOption("U", "url", true, "URL for WebGoat")
                .addOption("u", "user", true, "User name to login")
                .addOption("p", "password", true,  "Password to login")
                .addOption("r", "register", false, "Register new user")
                .addOption("v", "verbose", false, "Verbose output");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("d")) {
                driver = cmd.getOptionValue("d");
                System.out.println("Using Chrome driver: " + driver);
            }

            if (cmd.hasOption("U")) {
                url = cmd.getOptionValue("U");
                System.out.println("Accessing WebGoat on URL: " + url);
            }

            if (cmd.hasOption("u")) {
                user = cmd.getOptionValue("u");
                System.out.println("Using user name: " + user);
            }

            if (cmd.hasOption("p")) {
                password = cmd.getOptionValue("p");
                System.out.println("Using provided password");
            }

            if (cmd.hasOption("r")) {
                register = true;
            }

            if (cmd.hasOption("v")) {
                verbose = true;
            }

            // Invoke the selenium driver.
            Driver selenium = Driver.getOnlyInstance(driver);

            selenium.invoke(url, user, password, register, verbose);

        } catch (ParseException pe) {
            System.out.println("Error parsing command-line arguments!");
            System.out.println("");

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("webgoat-selenium-tests", options);

            System.exit(1);
        }

    }
}
