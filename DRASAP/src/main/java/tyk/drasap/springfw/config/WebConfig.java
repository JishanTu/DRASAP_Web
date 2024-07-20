package tyk.drasap.springfw.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "tyk.drasap")
public class WebConfig implements WebMvcConfigurer {
	public WebConfig() {
		System.out.println("WebConfig start, Enbale Spring MVC ......");
	}
}
