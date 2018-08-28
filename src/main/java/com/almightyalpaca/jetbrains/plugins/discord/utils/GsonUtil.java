package com.almightyalpaca.jetbrains.plugins.discord.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class GsonUtil
{
    public static boolean optBoolean(JsonObject object, String key, boolean defaultValue)
    {
        JsonElement matchPartlyElement = object.get("key");

        if (matchPartlyElement != null && matchPartlyElement.isJsonPrimitive())
        {
            JsonPrimitive primitive = matchPartlyElement.getAsJsonPrimitive();

            if (primitive.isBoolean())
                return primitive.getAsBoolean();
        }

        return defaultValue;

    }
}
