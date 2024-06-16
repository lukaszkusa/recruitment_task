package io.getint.recruitment_task;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Properties;

import static java.util.Objects.requireNonNull;

final class JiraClientProperties {
    static final String BASEAPIPATH;
    private static final String USERNAME;
    private static final String TOKEN;

    private JiraClientProperties(){}

    static {
        String rootPath = requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
        String appConfigPath = rootPath + "app.properties";

        Properties appProps = new Properties();
        try (final var file = new FileInputStream(appConfigPath)) {
            appProps.load(file);
        } catch (IOException ex) {
            throw new PropertyFileNotFoundException();
        }

        USERNAME = appProps.getProperty("user-name");
        BASEAPIPATH = appProps.getProperty("base-api-path");
        TOKEN = appProps.getProperty("token");
    }
    static String getBasicAuthenticationHeader() {
        String valueToEncode = USERNAME + ":" + TOKEN;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }
}
