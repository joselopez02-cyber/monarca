package com.cinemamonarca.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // JS/CSS/imágenes: 1 hora con revalidación obligatoria
        registry.addResourceHandler("/static/**", "/*.js", "/*.css",
                                    "/*.ico", "/*.png", "/*.jpg", "/*.woff2")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(
                    CacheControl.maxAge(1, TimeUnit.HOURS)
                                .mustRevalidate()
                                .cachePublic()
                );

        // index.html: siempre verifica si cambió
        registry.addResourceHandler("/", "/index.html")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.noCache().mustRevalidate());
    }
}