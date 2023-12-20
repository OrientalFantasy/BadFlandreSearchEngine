package org.example;

import com.badflandre.search.util.Query;
import com.badflandre.search.util.QueryData;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
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

public class MainVerticle extends AbstractVerticle {


    public MainVerticle(int port, String indexPath) {
        this.port = port;
        this.indexPath = indexPath;
    }

    private int port;
    private String indexPath;

    Logger logger = LoggerFactory.getLogger(MainVerticle.class);

    @Override
    public void start() throws Exception {

        System.setProperty("file.encoding","UTF-8");
        Field charset = Charset.class.getDeclaredField("defaultCharset");
        charset.setAccessible(true);
        charset.set(null,null);

        vertx = Vertx.vertx();

        Router router = Router.router(vertx);

        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("webroot/index.html");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String index = br.lines().collect(Collectors.joining());

        is.close();
        isr.close();
        br.close();

        router.get("/").respond(ctx -> ctx
                .response()
                .setChunked(true)
                .write(index)
        );


        Query q = new Query(indexPath);

        router.get("/a").handler(ctx -> {
            MultiMap queryParams = ctx.queryParams();
            String key = queryParams.contains("submit") ? queryParams.get("submit") : null;
            if (key == null)
                ctx.json(new JsonObject().put("errorMsg", "`submit` parameter must not be null"));
            else {
                try {
                    String address = ctx.request().connection().remoteAddress().toString();
                    String decodedKey = URLDecoder.decode(key, StandardCharsets.UTF_8.toString());
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

        // router.get("/a").respond(ctx -> ctx.response());


        router.route("/static/*").handler(new AStaticHandler(StaticHandler.create()));

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port)
                .onSuccess(server -> {
                    logger.info("BadFlandre's Search Engine");
                    logger.info("It's working!");
                    logger.info("HTTP server started at http://localhost:" + server.actualPort());
                });
    }

    class AStaticHandler implements Handler<RoutingContext> {

        private StaticHandler delegate;

        public AStaticHandler(StaticHandler delegate) {
            this.delegate = delegate;
        }

        @Override
        public void handle(RoutingContext routingContext) {
//            routingContext.response().putHeader("Content-Type", "html/text; charset=utf-8");
//            routingContext.response().putHeader("Content-Encoding", "UTF-8");
//            delegate.setDefaultContentEncoding("utf-8");

            delegate.handle(routingContext);
        }
    }
}
