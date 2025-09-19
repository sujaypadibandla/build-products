package com.gymplus.gymPlus;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymplus.core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.gymplus.core.JsonSerializer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gymplus.core.HttpUtils.forward;

public class GymPlusPlugin {

    private static String GymPlusUri = "https://gymplus-ie.perfectgym.pl/ClientPortal2";

    private static final Logger LOGGER = LogManager.getLogger(GymPlusPlugin.class);

    private Map<String, String> _getAuthHeaders() {

        Map<String, String> header = new HashMap<String, String>();
        header.put("RememberMe", "false");
        header.put("Login", "psujay.92@gmail.com");
        header.put("Password", "SujayGym@2025");

        return header;
    }

    private Gmap _getCommonRequestHeaders() {

        Gmap header = new Gmap();
        header.put("Accept", "application/json, text/plain, */*");
        header.put("Accept-Language", "en-GB,en;q=0.9");

        header.put("CP-LANG", "en");
        header.put("CP-MODE", "desktop");

        header.put("Content-Type", "application/json;charset=UTF-8");
        header.put("Origin", "https://gymplus-ie.perfectgym.pl");
        header.put("Referer", "https://gymplus-ie.perfectgym.pl/ClientPortal2/");

        header.put("Sec-Fetch-Dest", "empty");
        header.put("Sec-Fetch-Mode", "cors");
        header.put("Sec-Fetch-Site", "same-origin");

        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36");
        header.put("X-Requested-With", "XMLHttpRequest");

        header.put("sec-ch-ua", "Not;A=Brand\";v=\"99\", \"Google Chrome\";v=\"139\", \"Chromium\";v=\"139\"");
        header.put("sec-ch-ua-mobile", "?0");
        header.put("sec-ch-ua-platform", "\"Windows\"");

        return header;
    }


    public void _auth() throws Exception {

        Gmap headers = _getCommonRequestHeaders();
        headers.put("X-Hash", "#/Login");

        Gmap body = new Gmap();
        body.put("RememberMe", "false");
        body.put("Login", "psujay.92@gmail.com");
        body.put("Password", "SujayGym@2025");

        ResponseProcessor responseHandler = (responseBody, status) -> {
            return null;
        };

        Gmap result =  forward("POST", GymPlusUri + "/Auth/Login", JsonSerializer.format(body),
                null, null, headers, responseHandler, true);

        _auth = result.getm("responseHeaders").gets("jwt-token");
    }


    public Gmap getWeeklyClasses() throws Exception {

        Gmap headers = _getCommonRequestHeaders();
        headers.put("X-Hash", "#/Classes/5/Calendar");
        headers.put("Authorization", "Bearer " +  _auth);

       Gmap body = new Gmap();
        body.put("clubId", "5");
        body.put("categoryId", null);
        body.put("daysInWeek", "7");

        // Create a custom response handler
        ResponseProcessor responseHandler = (responseBody, status) -> {
            Gmap result = new Gmap("error", !_isSuccess(status));
            try {
                Gmap payload = responseBody != null ? JsonSerializer.parseMap(responseBody) : null;
                result.append("payload", payload);
            } catch (Exception ignored) {
                result.append("error", true);
            }
            return result;
        };

        return forward("POST", GymPlusUri + "/Classes/ClassCalendar/WeeklyClasses", JsonSerializer.format(body),
                null, null, headers, responseHandler, true);
    }

    public Garray getClassesToBook(Gmap weeklyClasses) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(weeklyClasses.toString());
        List<JsonNode> result = new ArrayList<>();

        for (JsonNode zone : root.path("CalendarData")) {
            for (JsonNode hour : zone.path("ClassesPerHour")) {
                for (JsonNode day : hour.path("ClassesPerDay")) {
                    for (JsonNode gymClass : day) {
                        if ("Indoor Cycle".equals(gymClass.path("Name").asText())) {
                            result.add(gymClass);
                        }
                    }
                }
            }
        }
        return JsonSerializer.parseArray(result.toString());
    }

    public int fetchBookableClassId(Garray classes) throws Exception {
        for (Object class1 : classes) {
            Gmap classMap = JsonSerializer.parseMap(class1.toString());
            if ("Indoor Cycle".equals(classMap.gets("Name")) &&
                    StrUtils.equals(classMap.gets("Status"), "Bookable") &&
                    StrUtils.equalsAny(classMap.gets("StartTime").split("T")[1], "18:00:00", "19:00:00")) {
                return classMap.geti("Id");
            }
        }
        return 0;
    }

    public Gmap bookAclass(int classId) throws Exception {

        Gmap headers = _getCommonRequestHeaders();
        headers.put("X-Hash", "#/Classes/5/Calendar");
        headers.put("Authorization", "Bearer " +  _auth);

        Gmap body = new Gmap();
        body.put("clubId", "5");
        body.put("classId", classId);

        ResponseProcessor responseHandler = (responseBody, status) -> {
            Gmap result = new Gmap("error", !_isSuccess(status));
            int errorCode = 0;
            try {
                Gmap payload = responseBody != null ? JsonSerializer.parseMap(responseBody) : null;
                result.append("payload", payload, "errorCode", errorCode);
            } catch (Exception ignored) {
                result.append("error", true);
            }
            return result;
        };

        Gmap result = forward("POST", GymPlusUri + "/Classes/ClassCalendar/BookClass", JsonSerializer.format(body),
                null, null, headers, responseHandler, false);

        if ( result.getb("error")) { throw new I2AException(result.get("errorCode", result.geti("status"))); }
        return result;

    }

    private String getGymPlusAuthUri() {
        return GymPlusUri + "/Auth/Login";
    }

    public void invokeAuthApi() throws Exception {
        _auth();
    }

    private String _auth = null;

    private boolean _isSuccess(int code) {
        return code >= 200 && code < 300;
    }


}
