
package com.Djwid.KInvTweaks.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class JsonUtils {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static <T> T readJson(File file, Class<T> clazz) {
        try (Reader reader = new FileReader(file)) {
            return GSON.fromJson(reader, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeJson(File file, Object data) {
        try (Writer writer = new FileWriter(file)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
