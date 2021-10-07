package org.auto.core.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class ConfigReader {
    ObjectMapper configMapper = new ObjectMapper();
    public Map<String, String> env_details;
    {
        try {
            env_details = (Map<String, String>) configMapper.readValue(Paths.get("parameters.json").toFile(), Map.class).get("environment");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long get_max_response_time(){
        return Long.parseLong(env_details.get("max_response_time"));
    }

    public String get_auth_token(){
        return env_details.get("auth_token");
    }

    public String get_base_uri(){
        return env_details.get("base_uri");
    }

}
