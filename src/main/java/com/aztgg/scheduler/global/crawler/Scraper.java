package com.aztgg.scheduler.global.crawler;

import java.io.IOException;

public interface Scraper<T> {

    T scrap() throws IOException;
}