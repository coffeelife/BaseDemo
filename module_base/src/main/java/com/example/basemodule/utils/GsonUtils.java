package com.example.basemodule.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lzl
 * @version 1.0.0
 * @功能 Gson解析工具类
 */
public class GsonUtils {

    // 是否json数据
    public static boolean isGoodJson(String json) {
//        if (json == null || json.equals("{}") || json.equals("401")) {
        if (json == null || json.equals("401")) {
            return false;
        }
        try {
            if (json.contains("code400")) {
                return true;
            }
            new JsonParser().parse(json);
            return true;
        } catch (JsonParseException e) {
            return false;
        }
    }

    public static boolean isBadJson(String json) {
        return !isGoodJson(json);
    }

    // 是否存在指定key
    public static boolean fieldIsNull(JsonObject command, String methodName) {

        return GsonUtils.getKeyValue(command, methodName) == null;
    }

    // Gson解析器
    public static Gson gson = new GsonBuilder().enableComplexMapKeySerialization()
            .excludeFieldsWithoutExposeAnnotation()// 不导出实体中没有用@Expose注解的属性
            .serializeNulls()// 支持Map的key为复杂对象的形式
            .setDateFormat("yyyy-MM-dd HH:mm:ss") // 时间转化为特定格式
            .setPrettyPrinting().create();

    /****************** json拆分 ***************************************/

    /**
     * 从父JsonObject对象中获取子JsonObject对象
     *
     * @param obj
     * @param key
     * @return
     */
    public static JsonObject getJsonObject(JsonObject obj, String key) {
        JsonElement element = obj.get(key);
        return element.getAsJsonObject();
    }

    /**
     * 从父JsonObject对象中获取子JsonArray对象
     *
     * @param obj
     * @param key
     * @return
     */
    public static JsonArray getJsonArray(JsonObject obj, String key) {
        JsonElement element = obj.get(key);
        return element.getAsJsonArray();
    }

    /**
     * 得到根JsonObject
     *
     * @param jsonStr
     * @return
     */
    public static JsonObject getRootJsonObject(String jsonStr) {
        JsonObject obj = new JsonParser().parse(jsonStr).getAsJsonObject();
        return obj;
    }

    /**
     * 得到根JsonArray
     *
     * @param jsonStr
     * @return
     */
    public static JsonArray getRootJsonArray(String jsonStr) {
        JsonArray obj = new JsonParser().parse(jsonStr).getAsJsonArray();
        return obj;
    }

    /**
     * 从JsonObject对象中获取键值
     *
     * @param obj
     * @param key
     * @return
     */
    public static JsonElement getKeyValue(JsonObject obj, String key) {
        JsonElement element = obj.get(key);
        return element;
    }

    /**
     * JsonArrary---->>>List<JsonObject>
     *
     * @param jsonArray
     * @return
     */
    public static List<JsonObject> JsonArrayToList(JsonArray jsonArray) {
        List<JsonObject> obj = new ArrayList<JsonObject>();
        for (int i = 0; i < jsonArray.size(); i++) {
            obj.add(jsonArray.get(i).getAsJsonObject());
        }
        return obj;
    }

    public Gson getGson() {
        return gson;
    }

    /****************** bean-jsonString配合builder使用 ***************************************/

    // TODO 简单bean,带泛型的List------>>>>>String
    public static <T> String toJsonString(List<T> json) {
        return gson.toJson(json);
    }

    // TODO 简单bean,带泛型的bean------>>>>>String
    public static <T> String toJsonString(T json) {
        return gson.toJson(json);
    }

    /****************** bean-jsonobject配合builder使用 ***************************************/
    // TODO bean-------->>>>jsonObject
    public static <T> JsonObject beanToJsonObject(T json) {
        return getRootJsonObject(toJsonString(json));
    }

    // TODO List<bean>------>>>>>jsonArrary
    public static <T> JsonArray ListToJsonArrary(List<T> json) {
        return getRootJsonArray(toJsonString(json));
    }

    /********************* json--bean配合Gson解析 *************************************/
    // TODO 转换JSONObject对象中的JSONObject为Bean
    public static <T> T toBean(JsonObject obj, String key, Class<T> type) {
        return gson.fromJson(obj.get(key).toString(), type);
    }

    // TODO 转换JSONObject对象中的JsonArray为List<Bean>
    public static <T> List<T> toList(JsonObject obj, String key, Class<T> type) {
        return JsonArrayToListBean(obj.get(key).getAsJsonArray(), type);
    }

    // TODO 直接把JsonArrary对象转化为List<Bean>
    public static <T> List<T> JsonArrayToListBean(JsonArray jsonArray, Class<T> type) {
        List<T> obj = new ArrayList<T>();
        for (int i = 0; i < jsonArray.size(); i++) {
            obj.add(gson.fromJson(jsonArray.get(i).toString(), type));
        }
        return obj;
    }

    // TODO 直接把json对象转化为bean
    public static <T> T JsonObjectToBean(JsonObject jsonObject, Class<T> type) {
        return gson.fromJson(jsonObject.toString(), type);
    }

    // 检查对象是否存在
    public static boolean checkJsonObejct(JsonObject root, String key) {
        return (!getKeyValue(root, key).isJsonNull()) && getKeyValue(root, key).isJsonObject();

    }

    // 检查数组是否存在
    public static boolean checkJsonArray(JsonObject root, String key) {
        return (!getKeyValue(root, key).isJsonNull()) && getKeyValue(root, key).isJsonArray();
    }

    public static JsonArray getRootJsonArrayByInputStream(Reader jsonStr) {
        JsonArray obj = new JsonParser().parse(jsonStr).getAsJsonArray();
        return obj;
    }
}
