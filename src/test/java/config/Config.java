package config;

import config.owner.ApiConfig;
import org.aeonbits.owner.ConfigFactory;

public class Config {

    private static final ApiConfig CFG = ConfigFactory.create(ApiConfig.class);

    public static final String BASE_URL = require("BOOKER_BASE_URL", CFG.baseUrl());
    public static final String USERNAME = require("BOOKER_USERNAME", CFG.username());
    public static final String PASSWORD = require("BOOKER_PASSWORD", CFG.password());

    private Config() {}

    private static String require(String key, String value) {

        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Variable" + key + " is null");
        }

        return value;

    }

}
