package com.affidock.api.common.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class MessageSourceConfig {

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setBasenames(
            "classpath:i18n/messages-common",
            "classpath:i18n/messages-users",
            "classpath:i18n/messages-files",
            "classpath:i18n/messages-groups",
            "classpath:i18n/messages-products"
        );
        source.setDefaultEncoding("UTF-8");
        return source;
    }
}
