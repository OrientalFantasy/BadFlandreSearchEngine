package com.badflandre.search.util;

import com.badflandre.search.extractor.Extractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class HtmlExtractor {
    Logger logger = LoggerFactory.getLogger(HtmlExtractor.class);

    public void setMirrorPath(String mirrorPath) {
        this.mirrorPath = mirrorPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }


    public void setDefaultEncoding(String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }

    private String defaultEncoding;
    private String mirrorPath;
    private String outputPath;

    public void doFinal() {
        logger.info("Start extracting pages...");
        if (mirrorPath == null) throw new RuntimeException("mirror path can not be null!");
        processExtract(mirrorPath);
        logger.info("Extraction is end.");
    }

    private void processExtract(String path) {
        File[] files = new File(path).listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                processExtract(files[i].getAbsolutePath());
            } else {
                String encode = "GB2312";
                try {
                    if (fileFilter(files[i])) continue;
                    BufferedReader reader = new BufferedReader(new FileReader(files[i].getAbsoluteFile()));
                    String line = reader.readLine();
                    while (line != null) {
                        if (line.contains("charset=")) {
                            int start = line.indexOf("charset=");
                            start = start + 8;
                            String tmp = line.substring(start, start + 3);
                            if ("ISO".equals(tmp) || "iso".equals(tmp)) {
                                encode = "ISO-8859-1";
                            } else if ("UTF".equals(tmp) || "utf".equals(tmp)) {
                                encode = "UTF-8";
                            } else if ("GBK".equals(tmp) || "gbk".equals(tmp)) {
                                encode = "GBK";
                            } else {
                                encode = "GB2312";
                            }
                            reader.close();
                            break;
                        } else {
                            line = reader.readLine();
                        }
                    }
                } catch (IOException ioe) {
                    logger.error(ioe.toString());
                }
                Extractor extractor = new Extractor();
                encode = defaultEncoding != null ? defaultEncoding : "UTF-8";
                extractor.setMirrorPath(mirrorPath);
                extractor.setStorePath(outputPath);
                extractor.setEncode(encode);
                extractor.extract(files[i].getAbsolutePath());
            }
        }
    }


    //        过滤指定类型文件
    public static Boolean fileFilter(File f) {
        String fileName = f.getName().toLowerCase();
        return !fileName.endsWith(".html");

//        String fileName = f.getName().toLowerCase();
//        List<String> ends = Arrays.asList(
//            ".gz", ".zip", ".tar", ".7z", ".rar",
//                ".jpg", ".jpeg", ".png", ".gif", ".bmp",
//                ".mp3", ".mp4", ".ogg", ".mov", ".avi", ".flv",
//                ".psd", ".doc", ".ppt", ".xls", ".wmv"
//        );
//        int flag = 0;
//
//        for (int i = 0; i < ends.size(); i++) {
//            if (fileName.endsWith(ends.get(i))) {
//                flag ++;
//            }
//        }
//
//        return flag != 0;


    }
}
