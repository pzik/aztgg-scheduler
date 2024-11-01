package com.aztgg.scheduler.global.crawler;

import java.io.IOException;

public interface Downloader<T> {

    T execute() throws IOException;
}
