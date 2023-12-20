package com.badflandre.search.util;

import com.badflandre.search.index.IndexBuilder;

import java.io.*;


public class TestIndexer {
    public static void main(String[] args) {
        try {
            IndexBuilder index = new IndexBuilder(ProperConfig.getValue("index.path"));
            index.build(ProperConfig.getValue("files.path"));
        } catch (IOException ioe) {
            System.out.println("Index creating failed!");
            ioe.printStackTrace();
        }
        System.out.println("Index creating finished!");
    }
}
