package com.aztgg.scheduler.recruitmentnotice.domain.scraper;

import java.io.IOException;

public interface Scraper<T> {

    T scrap() throws IOException;
}