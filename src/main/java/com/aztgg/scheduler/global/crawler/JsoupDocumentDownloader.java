package com.aztgg.scheduler.global.crawler;

import lombok.Builder;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class JsoupDocumentDownloader implements Downloader<Document> {

    private final String url;
    private final Connection.Method method;
    private final Map<String, String> cookies;

    @Builder
    public JsoupDocumentDownloader(String url, Connection.Method method, Map<String, String> cookies) {
        this.url = url;
        this.method = method;
        this.cookies = cookies;
    }

    @Override
    public Document execute() throws IOException {
        Connection connection = Jsoup.connect(url);

        if (Objects.nonNull(method)) {
            connection.method(method);
        }
        if (Objects.nonNull(cookies)) {
            connection.cookies(cookies);
        }

        return connection.get();
    }
}
