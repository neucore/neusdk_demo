package com.neucore.neulink.extend;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime>,NeulinkConst {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        String dateFormat = ConfigContext.getInstance().getConfig(DateTimeFormat, YYYY_MM_DD_HH_MM_SS);
        DateTimeFormatter requestFormatter = DateTimeFormatter.ofPattern(dateFormat);
        String localDateStr = src.format(requestFormatter);
        return new JsonPrimitive(localDateStr);
    }
}
