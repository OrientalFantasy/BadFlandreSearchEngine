package org.example;

import picocli.CommandLine;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            // start server
            Web.main(args);
        } else {
            System.exit(new CommandLine(new ToolCodeCommandLine()).execute(args));
        }
    }
}

