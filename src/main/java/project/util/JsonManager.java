package project.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class JsonManager {
    private static final ObjectMapper JsonMapper = new ObjectMapper();

    public static String dtoToJson(Object dto) throws JsonProcessingException {
        return JsonMapper.writeValueAsString(dto);
    }

    public static String errorToJson(String message, Exception exception) throws JsonProcessingException {
        Map<String, String> map = new HashMap<>();
        map.put("message", message);
        map.put("exception", exception.getMessage());
        return JsonMapper.writeValueAsString(map);
    }
}
