/*
 *     Copyright (C) 2020 rsouth (https://github.com/rsouth)
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.brokn.sequence.cli;

import org.apache.commons.cli.*;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Headless Mode
 */
public class HeadlessCli {

    private final Options options = new Options();

    public HeadlessCli() {
        initOptions();
    }

    private void initOptions() {
        // input file option
        final Option inputOption = Option.builder("i")
                .longOpt("input")
                .desc("input .seq file")
                .hasArg()
                .required()
                .build();

        // output file option
        final Option outputOption = Option.builder("o")
                .longOpt("output")
                .desc("output .png file")
                .hasArg()
                .required()
                .build();

        options.addOption(inputOption);
        options.addOption(outputOption);
    }

    public void run(String[] args) {
        final CliValidationResult cliValidationResult = validateParameters(args);
        if (cliValidationResult.isValid()) {
            // render diagram to the output file
            HeadlessRenderer renderer = new HeadlessRenderer();
            renderer.draw(cliValidationResult);

        } else {
            // invalid args; show usage and exit
            showUsage();
        }

        System.exit(0);
    }

    private void showUsage() {
        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("sequencer", options);
    }

    static class CliValidationResult {
        private final boolean valid;
        private String reason;
        private CommandLine cmd;

        public CliValidationResult(boolean valid, String reason, CommandLine cmd) {
            this.valid = valid;
            this.reason = reason;
            this.cmd = cmd;
        }

        public boolean isValid() {
            return valid;
        }

        public String getReason() {
            return reason;
        }

        public CommandLine getCmd() {
            return cmd;
        }
    }

    private CliValidationResult validateParameters(String[] args) {
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("i") && cmd.hasOption("o")) {
                // check input file exists
                boolean exists = Files.exists(Paths.get(cmd.getOptionValue("i")));
                if (!exists) {
                    System.err.println("file doesn't exist, fool");
                    return new CliValidationResult(false, "Input file does not exist", cmd);

                } else {
                    // proceed, bro.
                    System.out.println("proceed, bro.");
                    return new CliValidationResult(true, "OK", cmd);
                }

            } else {
                return new CliValidationResult(false, "Missing args", null);
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return new CliValidationResult(false, "not sure, just failed", null);
        }

    }
}
