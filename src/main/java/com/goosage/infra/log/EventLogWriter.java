package com.goosage.infra.log;

import java.io.FileWriter;
import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class EventLogWriter {

    private static final ObjectMapper om = new ObjectMapper();

    public static void write(Long userId, String type) {
        try {
        	String fileName = "C:/dev/loosegoose/goosage-scripts/core/artifacts/ui.events.user" + userId + ".json";
        	
            Map<String, Object> log = Map.of(
                    "userId", userId,
                    "type", type,
                    "createdAt", LocalDateTime.now().toString()
            );

            String json = om.writeValueAsString(log);

            FileWriter fw = new FileWriter(fileName, true);
            fw.write(json + "\n");
            fw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}