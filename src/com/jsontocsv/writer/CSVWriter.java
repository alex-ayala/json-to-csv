package com.jsontocsv.writer;

import com.google.common.base.Joiner;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CSVWriter {

    /**
     * Format the flattened json list map into a comma delimited output and write to a file.
     * @param flatJson List of a map with string key value pairs
     * @param fileName Output file name and location
     * @throws FileNotFoundException
     */
    public static void writeAsCSV(List<Map<String, String>> flatJson, String fileName) throws FileNotFoundException {
        Set<String> headers = collectHeaders(flatJson);
        StringBuilder sb = new StringBuilder();
        sb.append(Joiner.on(",").join(headers.toArray()));
        sb.append(System.lineSeparator());

        for (Map<String, String> map : flatJson) {
            sb.append(getCommaSeperatedRow(headers, map));
            sb.append(System.lineSeparator());
        }
        writeToFile(sb.toString(), fileName);
    }

    /**
     * Format the flattened json list map into a comma delimited output
     * @param flatJson List of a map with string key value pairs
     * @return String output
     */
    public static String delimitCSV(List<Map<String, String>> flatJson) {
        Set<String> headers = collectHeaders(flatJson);
        StringBuilder sb = new StringBuilder();
        sb.append(Joiner.on(",").join(headers.toArray()));
        sb.append(System.lineSeparator());

        for (Map<String, String> map : flatJson) {
            sb.append(getCommaSeperatedRow(headers, map));
            sb.append(System.lineSeparator());
        }

        return sb.toString();
    }

    private static void writeToFile(String output, String fileName) throws FileNotFoundException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(output);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(writer);
        }
    }

    private static void close(BufferedWriter writer) {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getCommaSeperatedRow(Set<String> headers, Map<String, String> map) {
        List<String> items = new ArrayList<String>();
        for (String header : headers) {
            String value = map.get(header) == null ? "" : map.get(header).replace(",", "");
            items.add(value);
        }
        return Joiner.on(",").join(items.toArray());
    }

    private static Set<String> collectHeaders(List<Map<String, String>> flatJson) {
        Set<String> headers = new TreeSet<String>();
        for (Map<String, String> map : flatJson) {
            headers.addAll(map.keySet());
        }
        return headers;
    }
}
