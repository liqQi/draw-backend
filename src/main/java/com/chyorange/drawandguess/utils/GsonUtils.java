package com.chyorange.drawandguess.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

public class GsonUtils {

    private static Gson gson = new GsonBuilder().create();

    public static String toJson(Object obj){

        return gson.toJson(obj);
    }

    public static <T>T fromJson(String json, Type typeOfT){
        return gson.fromJson(json,typeOfT);
    }

    public static <T>T fromJson(String json, Class<T> typeOfT){
        return gson.fromJson(json,typeOfT);
    }

    public static JsonObject parseJson(String json) {

        return gson.fromJson(json,JsonObject.class);
    }
}
