package tyk.drasap.springfw.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class AopConfig {
	public AopConfig() {
		System.out.println("AopConfig start, Enbale AOP ......");
	}
}
