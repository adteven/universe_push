package com.comsince.github.configuration;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class TomcatConfig extends WebMvcConfigurerAdapter {
    @Value("${server.http.port}")
    private int httpPort;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "POST", "OPTIONS", "PUT")
                .allowedHeaders("Content-Type", "X-Requested-With", "accept", "Origin", "Access-Control-Request-Method",
                        "Access-Control-Request-Headers","accessToken")
                .exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials")
                .allowCredentials(true).maxAge(3600);
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableTomcatWebServerFactory> containerCustomizer() {
        return new WebServerFactoryCustomizer<ConfigurableTomcatWebServerFactory>() {
            @Override
            public void customize(ConfigurableTomcatWebServerFactory factory) {
                if (factory instanceof TomcatServletWebServerFactory) {
                    Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
                    connector.setPort(httpPort);
                    ((TomcatServletWebServerFactory) factory).addAdditionalTomcatConnectors(connector);
                }
            }
        };
    }

}
