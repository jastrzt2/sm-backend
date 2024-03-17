package com.example.demo.Images;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dyucisq5v",
                "api_key", "763626873162547",
                "api_secret", "GDypm6KzYFIE8bKmwFrsc4FGRTA",
                "secure", true));
    }
}
