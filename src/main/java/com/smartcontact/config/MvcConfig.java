package com.smartcontact.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Expose the './uploads' directory at '/img/uploads/**'
        Path uploadDir = Paths.get("uploads");
        String uploadPath = uploadDir.toFile().getAbsolutePath();
        
        // Ensure folder exists at start
        File uploadFolder = uploadDir.toFile();
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }

        registry.addResourceHandler("/img/uploads/**")
                .addResourceLocations("file:/" + uploadPath + "/");
    }
}
