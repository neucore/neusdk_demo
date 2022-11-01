package com.neucore.neulink.extend;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeDeserializer implements JsonDeserializer<LocalTime>,NeulinkConst {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public LocalTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String localDateStr = json.getAsString();
        String dateFormat = ConfigContext.getInstance().getConfig(TimeFormat, HH_MM_SS);
        DateTimeFormatter requestFormatter = DateTimeFormatter.ofPattern(dateFormat);
        LocalTime localTime = LocalTime.parse(localDateStr, requestFormatter);
        return localTime;
    }
}
