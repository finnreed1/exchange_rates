package project.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import project.dto.CurrencyDto;

import java.util.HashMap;
import java.util.Map;

public class JsonManager {
    private static final ObjectMapper JsonMapper = new ObjectMapper();

    public static String dtoToJson(Object dto) throws JsonProcessingException {
        String test = JsonMapper.writeValueAsString(dto);
        return test;
    }

    public static String errorToJson(String message, Exception exception) throws JsonProcessingException {
        Map<String, String> map = new HashMap<>();
        map.put("message", message);
        map.put("exception", exception.getClass().getSimpleName());
        return JsonMapper.writeValueAsString(map);
    }

    public static void main(String[] args) throws JsonProcessingException {
        CurrencyDto dto = new CurrencyDto("RUB", "Russian Ruble", "P");
        String test = JsonManager.dtoToJson(dto);
        System.out.println(test);
    }
}
