package com.gcl.library;

import com.gcl.library.service.NetService;

import org.junit.BeforeClass;
import org.junit.Test;

public class NetServiceTest {
    private static NetService netService = null;

    @BeforeClass
    public static void before() {
        netService = new NetService();
    }

    @Test
    public void testLogin() throws Exception {
        String result = netService.login("14110501053", "454110");
        System.out.println(result);
    }
}