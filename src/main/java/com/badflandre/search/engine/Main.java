package com.badflandre.search.engine;

import picocli.CommandLine;

public class Main {
    public static void main(String[] args) throws Exception {
        System.exit(new CommandLine(new SearchEngineCodeCommandLine()).execute(args));
    }
}

