package org.example;

import com.badflandre.search.index.IndexBuilder;
import com.badflandre.search.util.HtmlExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.Callable;

public class Main {
    public static void main(String[] args) throws Exception {


        if (args.length == 0) {
            // start server
            Web.main(args);
        } else {
            System.exit(new CommandLine(new ToolCodeCommandLine()).execute(args));
        }
//
//        String mp = ProperConfig.getValue("mirror.path");
//        String fp = ProperConfig.getValue("files.path");
//        String ip = ProperConfig.getValue("index.path");
//
//        if (!exists(mp)) throw new RuntimeException("mirror.path does not exist.");
//        if (!exists(fp)) Files.createDirectories(Paths.get(fp));
//        if (!exists(ip)) Files.createDirectories(Paths.get(ip));
//
//        if (args.length != 0) {
//            String firstArg = args[0];
//            if (firstArg.equals("extract")) TestExtractor.main(args);
//            else if (firstArg.equals("index")) TestIndexer.main(args);
//            else throw new RuntimeException("unknown cmd: " + firstArg);
//        } else {
//
//        }
    }
}

@CommandLine.Command(
        name = "bfse",
        mixinStandardHelpOptions = true,
        version = {"0.0.1"},
        description = {"bfse command line tools"})
class ToolCodeCommandLine implements Callable<Integer> {

    Logger logger = LoggerFactory.getLogger(ToolCodeCommandLine.class);

    @CommandLine.Parameters(index = "0", defaultValue = "server", description = {
            "tool action, can be extract, index or server"})
    String action;

    @CommandLine.Option(names = {"-port"}, defaultValue = "25565", description = {"server start port"})
    String port;

    @CommandLine.Option(names = {"-encoding"}, description = {"encoding for extract, default is utf-8"})
    String encoding;

    @CommandLine.Option(names = {"-config"},description = {"configuration file path"})
    File configFile;


    private Properties loadConfig() throws IOException {
        if (configFile == null) {
            // using default path: ./config.properties
            configFile = new File("./config.properties");
            if (!configFile.exists()) throw new RuntimeException("please provide config.properties");
        }

        Properties config = new Properties();
        config.load(new FileReader(configFile));

        return config;
    }

    @Override
    public Integer call() throws Exception {

        if (action.equals("extract")) {
            Properties config = loadConfig();

            Path mp = Paths.get(config.getProperty("mirror.path"));
            Path fp = Paths.get(config.getProperty("files.path"));

            if (!Files.exists(mp)) {
                throw new RuntimeException("mirror.path not exists!");
            }

            if (!Files.exists(fp)) {
                Files.createDirectories(fp);
            }

            HtmlExtractor extractor = new HtmlExtractor();
            extractor.setMirrorPath(mp.toFile().getAbsolutePath());
            extractor.setOutputPath(fp.toFile().getAbsolutePath());
            extractor.setDefaultEncoding(encoding);

            extractor.doFinal();

        } else if (action.equals("index")) {
            Properties config = loadConfig();

            Path ip = Paths.get(config.getProperty("index.path"));
            Path fp = Paths.get(config.getProperty("files.path"));

            if (!Files.exists(fp)) {
                throw new RuntimeException("files.path not exists!");
            }

            if (!Files.exists(ip)) {
                Files.createDirectories(ip);
            }

            try {
                IndexBuilder index = new IndexBuilder(ip.toFile().getAbsolutePath());
                index.build(fp.toFile().getAbsolutePath());
            } catch (IOException ioe) {
                logger.error("Index creating failed!");
                ioe.printStackTrace();
            }

            logger.info("Index creating finished!");

        } else if (action.equals("server")) {
            Properties config = loadConfig();

            Path ip = Paths.get(config.getProperty("index.path"));

            if (!Files.exists(ip)) {
                throw new RuntimeException("index.path not exists!");
            }

            new MainVerticle(Integer.parseInt(port), ip.toFile().getAbsolutePath()).start();

            Scanner scanner = new Scanner(System.in);
            while (true) {
                String line = scanner.nextLine();
                if (line.equals("stop")) System.exit(0);
                else {
                    System.out.println("unknown command: " + line);
                }
            }

        } else throw new RuntimeException("unknown action: " + action);

        return 0;
    }
}