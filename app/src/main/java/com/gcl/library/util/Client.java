package com.gcl.library.util;

import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class Client {

    // cookieStore
    public static BasicCookieStore cookieStore = new BasicCookieStore();

    // 统一客户端
    public static CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
}
