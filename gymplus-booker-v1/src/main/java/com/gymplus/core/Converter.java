package com.gymplus.core;

import java.util.ArrayList;

import static com.gymplus.core.Settings.EXTENSION_PREFIX;
import static com.gymplus.core.Settings.TENANT;
import static com.gymplus.core.Settings.EXTENSION_PREFIX;
import static com.gymplus.core.Settings.TENANT;


public class Converter {

    public static Gmap idmUser2azureUser(Gmap idmUser) {
        Gmap params = new Gmap(idmUser);
        // made true in template
//        if (params.containsKey("status")) {
//            params.append("accountEnabled", "active".equals(params.get("status", "").toLowerCase()));
//        } else {
//            params.append("accountEnabled", "true");
//        }

        if (!StrUtils.isEmpty(params.gets("changeUserName"))) {
            params.put("emailAddress", params.get("changeUserName"));
            params.put("displayName", params.get("changeUserName"));
        }

        params.putIfAbsent("extensionPrefix", EXTENSION_PREFIX);

        return params;
    }

    public static Gmap azureUser2idmUser(Gmap azureUser) {
        Gmap params = new Gmap(azureUser);
        new ArrayList<>(params.keySet()).forEach(el -> {
            if (el.startsWith(EXTENSION_PREFIX)) {
                String name = el.replace(EXTENSION_PREFIX , "");
                params.append(name, params.get(el));
            }
        });

        Garray emailArray = azureUser.get("signInNames", new Garray());
        if (!emailArray.isEmpty()) { params.append("emailAddress", ((Gmap) emailArray.get(0)).gets("value")); }

        if (params.containsKey("accountEnabled")) { params.append("status", params.getb("accountEnabled") ? "Active" : "Inactive"); }

        params.append("applications", params.gets(EXTENSION_PREFIX + "application"));
        params.append("AccountNumber", params.gets(EXTENSION_PREFIX + "AccountNumber"));

        return params.append("tenant", TENANT);
    }
}
