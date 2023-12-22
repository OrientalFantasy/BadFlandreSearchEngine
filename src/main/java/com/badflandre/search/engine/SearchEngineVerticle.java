package com.badflandre.search.engine;

import com.badflandre.search.util.Query;
import com.badflandre.search.util.QueryData;
import io.vertx.core.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SearchEngineVerticle extends AbstractVerticle {

    public SearchEngineVerticle() {
        this(25565, "indexes");
    }

    public SearchEngineVerticle(int port, String indexPath, String consoleEncoding, String decodeEncoding) {
        this.port = port;
        this.indexPath = indexPath;
        this.consoleEncoding = consoleEncoding;
        this.decodeEncoding = decodeEncoding;
    }

    public SearchEngineVerticle(int port, String indexPath) {
        this(port, indexPath, "UTF-8", "UTF-8");
    }

    private final int port;
    private final String indexPath;
    private final String consoleEncoding;
    private final String decodeEncoding;

    Logger logger = LoggerFactory.getLogger(SearchEngineVerticle.class);

    @Override
    public void start(Promise<Void> promise) throws Exception {
        setSystemEncoding();
        Query q = new Query(indexPath);
        String indexPage = getIndexPage();

        Router router = Router.router(vertx);

        router.get("/").respond(ctx -> ctx
                .response()
                .setChunked(true)
                .write(indexPage)
        );

        router.get("/a").handler(ctx -> {
            MultiMap queryParams = ctx.queryParams();
            String key = queryParams.contains("submit") ? queryParams.get("submit") : null;
            if (key == null)
                ctx.json(new JsonObject().put("errorMsg", "`submit` parameter must not be null"));
            else {
                try {
                    String address = ctx.request().connection().remoteAddress().toString();
                    String decodedKey = URLDecoder.decode(key, decodeEncoding == null ? StandardCharsets.UTF_8 : Charset.forName(decodeEncoding));
                    String[] keys = decodedKey.split(" ");
                    logger.info("<" + address + "> : " + decodedKey);
                    if (keys.length == 0 || Arrays.stream(keys).anyMatch(p -> p.trim().isEmpty())) {
                        throw new RuntimeException("illegal keys form");
                    }
                    QueryData[] res = q.getQueryResult(Arrays.stream(keys).map(String::trim).collect(Collectors.toList()).toArray(new String[]{}));
                    JsonObject json = new JsonObject();

                    JsonArray array = new JsonArray();

                    for (QueryData i : res) {
                        JsonObject data = new JsonObject();
                        data.put("title", i.getTitle());
                        data.put("url", i.getUrl());
                        data.put("content", i.getContent());
                        array.add(data);
                    }

                    json.put("data", array);
                    ctx.json(json);
                } catch (IOException e) {
                    ctx.json(new JsonObject().put("errorMsg", e.getMessage()));
                    logger.error("error occurred: " + e.getMessage());
                }
            }
        });

        router.route("/*").handler(StaticHandler.create());

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port)
                .onSuccess(server -> {
                    logger.info("BadFlandre's Search Engine");
                    logger.info("It's working!");
                    logger.info("HTTP server started at http://localhost:" + server.actualPort());
                });
    }

    private void setSystemEncoding() throws NoSuchFieldException, IllegalAccessException {
        System.setProperty("file.encoding", consoleEncoding);
        Field charset = Charset.class.getDeclaredField("defaultCharset");
        charset.setAccessible(true);
        charset.set(null, null);
    }

    private String getIndexPage() throws IOException {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("webroot/index.html");
        assert is != null;
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String indexPage = br.lines().collect(Collectors.joining("\n"));

        is.close();
        isr.close();
        br.close();

        return indexPage;
    }
}
