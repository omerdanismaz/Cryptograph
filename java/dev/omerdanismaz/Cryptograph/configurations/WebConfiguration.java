package dev.omerdanismaz.Cryptograph.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer
{
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry resourceHandlerRegistry)
    {
        resourceHandlerRegistry
                .addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}
