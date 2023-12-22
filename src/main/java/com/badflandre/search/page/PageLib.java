package com.badflandre.search.page;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageLib {

    static Logger logger = LoggerFactory.getLogger(PageLib.class);


    public static void store(Page page, String storePath) {
        String storepath = storePath + "/" + page.getSummary();
        if (new File(storepath).exists()) {
            logger.info("Message: " + storepath + " is existed, now overwrite it");
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(storepath, false));
            writer.append(page.getUrl());
            writer.newLine();
            writer.append(page.getTitle());
            writer.newLine();
            writer.append(String.valueOf(page.getScore()));
            writer.newLine();
            writer.append(page.getContext());
            writer.close();
        } catch (IOException ioe) {
            logger.error("Error: Processing " + page.getUrl() + " accurs error");
            ioe.printStackTrace();
        }
    }

    public static void _store(Page page, String storePath) {
        String storepath = storePath + "/" + page.getSummary();
        if (new File(storepath).exists()) {
            logger.info("Message: " + storepath + " is existed!");
            return;
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(storepath));
            writer.append(page.getUrl());
            writer.newLine();
            writer.append(page.getTitle());
            writer.newLine();
            writer.append(String.valueOf(page.getScore()));
            writer.newLine();
            writer.append(page.getContext());
            writer.close();
        } catch (IOException ioe) {
            logger.error("Error: Processing " + page.getUrl() + " accurs error");
            ioe.printStackTrace();
        }
    }
}
