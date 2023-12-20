package com.badflandre.search.util;

import java.util.Arrays;

public class TestQuery {
    public static void main(String[] args) throws Exception {
        Query query = new Query("indexes");
        QueryData[] res = query.getQueryResult(new String[]{ "list" });
        String str = Arrays.toString(res);
        System.out.println(str);
//        System.out.println("啊啊啊啊");
    }
}
