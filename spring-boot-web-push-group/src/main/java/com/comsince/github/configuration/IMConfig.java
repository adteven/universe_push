package com.comsince.github.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix="im")
public class IMConfig {
    String admin_url;
    String admin_secret;
    String embed_db;

    public String getAdmin_url() {
        return admin_url;
    }

    public void setAdmin_url(String admin_url) {
        this.admin_url = admin_url;
    }

    public String getAdmin_secret() {
        return admin_secret;
    }

    public void setAdmin_secret(String admin_secret) {
        this.admin_secret = admin_secret;
    }

    public String getEmbed_db() {
        return embed_db;
    }

    public void setEmbed_db(String embed_db) {
        this.embed_db = embed_db;
    }
}
