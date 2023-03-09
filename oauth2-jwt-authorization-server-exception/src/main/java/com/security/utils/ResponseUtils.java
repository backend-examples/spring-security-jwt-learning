package com.security.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.MediaType;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class ResponseUtils {

    @SuppressWarnings({"deprecation"})
    public static void response(HttpServletResponse response, Map map) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_UTF8.toString());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String json = objectMapper.writeValueAsString(map);

        response.getWriter().println(json);
        response.setStatus(HttpServletResponse.SC_OK);
        response.flushBuffer();
    }
}
