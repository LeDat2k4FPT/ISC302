/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vnpay.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author gmt
 */
public class VNPayUtil {

    public static String builQueryString(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder query = new StringBuilder();
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if (value != null && !value.isEmpty()) {
                query.append(URLEncoder.encode(key, "UTF-8"))
                        .append("=")
                        .append(URLEncoder.encode(value, "UTF-8"));
                if (i < keys.size() - 1) {
                    query.append("&");
                }
            }
        }
        return query.toString();
    }
}
