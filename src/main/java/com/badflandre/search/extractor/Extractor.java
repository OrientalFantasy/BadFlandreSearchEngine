package com.badflandre.search.extractor;

import org.htmlparser.*;
import org.htmlparser.util.*;
import org.htmlparser.visitors.*;
import org.htmlparser.nodes.*;
import org.htmlparser.tags.*;
import com.badflandre.search.page.*;
import com.badflandre.search.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Extractor implements Runnable {
    private String filename;
    private Parser parser;
    private Page page;
    private String encode;

    public void setMirrorPath(String mirrorPath) {
        this.mirrorPath = mirrorPath;
    }

    private String mirrorPath;

    public void setStorePath(String storePath) {
        this.storePath = storePath;
    }

    private String storePath;
    Logger logger = LoggerFactory.getLogger(Extractor.class);


    public void setEncode(String encode) {
        this.encode = encode;
    }

    private String combineNodeText(Node[] nodes) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < nodes.length; i++) {
            Node anode = (Node) nodes[i];
            String line = null;
            if (anode instanceof TextNode) {
                TextNode textnode = (TextNode) anode;
                line = textnode.getText();
            } else if (anode instanceof LinkTag) {
                LinkTag linknode = (LinkTag) anode;
                line = linknode.getLinkText();
            } else if (anode instanceof Div) {
                if (anode.getChildren() != null) {
                    line = combineNodeText(anode.getChildren().toNodeArray());
                }
            } else if (anode instanceof ParagraphTag) {
                if (anode.getChildren() != null) {
                    line = combineNodeText(anode.getChildren().toNodeArray());
                }
            } else if (anode instanceof Span) {
                if (anode.getChildren() != null) {
                    line = combineNodeText(anode.getChildren().toNodeArray());
                }
            } else if (anode instanceof TableTag) {
                if (anode.getChildren() != null) {
                    line = combineNodeText(anode.getChildren().toNodeArray());
                }
            } else if (anode instanceof TableRow) {
                if (anode.getChildren() != null) {
                    line = combineNodeText(anode.getChildren().toNodeArray());
                }
            } else if (anode instanceof TableColumn) {
                if (anode.getChildren() != null) {
                    line = combineNodeText(anode.getChildren().toNodeArray());
                }
            }
            if (line != null) {
                buffer.append(line);
            }
        }
        return buffer.toString();
    }

    private String getUrl(String filename) {

        Path filePath = Paths.get(filename);
        Path mirrorPath = Paths.get(this.mirrorPath);
        if (filePath.startsWith(mirrorPath)) {
            return "http://" + mirrorPath.relativize(filePath).toString().replace("\\", "/");
        } else {
            String url = filename;
            url = url.replace(mirrorPath + "/mirror", "");
            if (url.lastIndexOf("/") == url.length() - 1) {
                url = url.substring(0, url.length() - 1);
            }
            url = url.substring(1);
            return "http://" + url.replace("\\", "/");
        }
    }

    private int getScore(String url, int score) {
        String[] subStr = url.split("/");
        score = score - (subStr.length - 1);
        return score;
    }

    private String getSummary(String context) {
        if (context == null) {
            context = "";
        }
        return MD5.MD5Encode(context);
    }

    public void extract(String filename) {
        logger.info("Message: Now extracting " + filename);
        this.filename = filename.replace("\\", "/");
        run();
        if (this.page != null) {
            PageLib.store(this.page, storePath);
        }
    }

    public void run() {
        try {
            parser = new Parser(this.filename);
            parser.setEncoding(encode);
            HtmlPage visitor = new HtmlPage(parser);
            parser.visitAllNodesWith(visitor);
            page = new Page();
            this.page.setUrl(getUrl(this.filename));
            this.page.setTitle(visitor.getTitle());
            if (visitor.getBody() == null) {
                this.page.SetContext(null);
            } else {
                this.page.SetContext(combineNodeText(visitor.getBody().toNodeArray()));
            }

            this.page.setScore(getScore(this.page.getUrl(), this.page.getScore()));

            this.page.setSummary(getSummary(this.page.getContext()));

        } catch (ParserException pe) {
            this.page = null;
            pe.printStackTrace();
            logger.info("Continue...");
        }
    }
}
