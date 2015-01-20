package com.jsontocsv.parser;

import com.google.common.collect.Lists;
import com.google.gson.*;

import java.util.*;
import java.util.Map;

public class JsonFlattener {
    /**
     * Overloaded method to allow for parsing of json array
     * @param jsonArray Input to be parsed
     * @return List of maps with deconstructed json key value pairs
     */
    public List<Map<String, String>> parse(JsonArray jsonArray) {
        List<Map<String, String>> flatJson = Lists.newArrayList();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonElement jsonElem = jsonArray.get(i) ;
            if (jsonElem.isJsonObject()) {
                Map<String, String> stringMap = parse(jsonElem.getAsJsonObject());
                flatJson.add(stringMap);
            } else if (jsonElem.isJsonArray()){
                parse(jsonElem.getAsJsonArray());
            }
        }
        return flatJson;
    }

    /**
     * Overloaded method to allow for parsing of json string
     * @param json String to be parsed
     * @return List<Map<String, String>>
     * @throws Exception
     */
    public List<Map<String, String>> parseJson(String json) throws Exception {
        List<Map<String, String>> flatJson = Lists.newArrayList();
        try {
            JsonElement jsonElem = new JsonParser().parse(json);
            if (jsonElem.isJsonObject()) {
                flatJson.add(parse(jsonElem.getAsJsonObject()));
            } else {
                flatJson = handleAsArray(json);
            }
        } catch (Exception err) {

            throw new Exception("Json might be malformed");
        }
        return flatJson;
    }

    private Map<String, String> parse(JsonObject jsonObject) {
        Map<String, String> flatJson = new HashMap<String, String>();
        flatten(jsonObject, flatJson, "");
        return flatJson;
    }

    private List<Map<String, String>> handleAsArray(String json) throws Exception {
        List<Map<String, String>> flatJson = Lists.newArrayList();
        try {
            JsonElement jsonElem = new JsonParser().parse(json);
            if (jsonElem.isJsonArray()) {
                flatJson = parse(jsonElem.getAsJsonArray());
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            throw new Exception("Json might be malformed");
        }
        return flatJson;
    }

    private void flatten(JsonArray obj, Map<String, String> flatJson, String prefix) {
        for (int i = 0; i < obj.size(); i++) {
            if (obj.get(i).isJsonArray()) {
                JsonArray jsonArray = obj.get(i).getAsJsonArray();
                if (jsonArray.size() < 1) continue;
                flatten(jsonArray, flatJson, prefix + i);
            } else if (obj.get(i).isJsonObject()) {
                JsonObject jsonObject = obj.get(i).getAsJsonObject();
                flatten(jsonObject, flatJson, prefix + (i + 1));
            } else {
                String value = obj.get(i).getAsString();
                if (!value.isEmpty() && !value.equalsIgnoreCase("null")) {
                    flatJson.put(prefix + (i + 1), value);
                }
            }
        }
    }

    private void flatten(JsonObject obj, Map<String, String> flatJson, String prefix) {
        Set<Map.Entry<String, JsonElement>> objEntries = obj.entrySet();
        for (Map.Entry<String, JsonElement> elem : objEntries) {
            String key = elem.getKey();
            if (obj.get(key).isJsonObject()) {
                JsonObject jsonObject = obj.get(key).getAsJsonObject();
                flatten(jsonObject, flatJson, prefix);
            } else if (obj.get(key).isJsonArray()) {
                JsonArray jsonArray = obj.get(key).getAsJsonArray();
                if (jsonArray.size() < 1) continue;
                flatten(jsonArray, flatJson, key);
            } else {
                String value = obj.get(key).getAsString();
                if (!value.isEmpty() && !value.equalsIgnoreCase("null")) {
                    flatJson.put(prefix + key, value);
                }
            }
        }
    }
}

