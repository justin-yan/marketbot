package com.justinyan.marketbot;

import com.justinyan.marketbot.infrastructure.MarketbotConfig;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class MarketbotApp extends Application<MarketbotConfig> {
    public static void main(String[] args) throws Exception {
        new MarketbotApp().run(args);
    }

    @Override
    public String getName() {
        return "marketbot";
    }

    @Override
    public void initialize(Bootstrap<MarketbotConfig> bootstrap) {
    }

    @Override
    public void run(MarketbotConfig configuration, Environment environment) throws Exception {
    }
}
