package com.alfredbase.utils;

import com.alfredbase.javabean.ItemDetailPrice;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by Arif S. on 2019-09-20
 */
public class ItemDetailPriceDeserializer implements JsonDeserializer<ItemDetailPrice> {
    @Override
    public ItemDetailPrice deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement type = jsonObject.get("paraValue1");
        if (type != null) {
            switch (type.getAsString()) {

            }
        }
        return null;
    }
}
