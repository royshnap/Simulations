package dto;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class HistogramSimulationDTOSerializer implements JsonSerializer<HistogramSimulationDTO> {
    @Override
    public JsonElement serialize(HistogramSimulationDTO src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("consistency", src.getConsistency());
        jsonObject.addProperty("average", src.getAverage());
        jsonObject.add("histogram", context.serialize(src.getHistogram()));
        return jsonObject;
    }
}

