package config.owner;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "file:local.properties",
        "classpath:local.properties"
})

public interface ApiConfig extends Config {

    @Key("BOOKER_BASE_URL")
    String baseUrl();

    @Key("BOOKER_USERNAME")
    String username();

    @Key("BOOKER_PASSWORD")
    String password();

}