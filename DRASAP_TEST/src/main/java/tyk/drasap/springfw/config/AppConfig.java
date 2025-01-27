package tyk.drasap.springfw.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;

import tyk.drasap.springfw.bean.ActionMappings;
import tyk.drasap.springfw.bean.GlobalForwards;

@Configuration
public class AppConfig {
	@Bean
	public GlobalForwards globalForwards() throws IOException {
		System.out.println("Parse global-forwards.json ......");

		Resource resource = new ClassPathResource("forwardJson/global-forwards.json");
		ObjectMapper objectMapper = new ObjectMapper();
		GlobalForwards globalForwards = objectMapper.readValue(resource.getInputStream(), GlobalForwards.class);
		return globalForwards;
	}

	@Bean
	public ActionMappings actionMappings() throws IOException {
		System.out.println("Parse action-mappings.json ......");

		Resource resource = new ClassPathResource("forwardJson/action-mappings.json");
		ObjectMapper objectMapper = new ObjectMapper();
		ActionMappings actionMappings = objectMapper.readValue(resource.getInputStream(), ActionMappings.class);
		return actionMappings;
	}
}
