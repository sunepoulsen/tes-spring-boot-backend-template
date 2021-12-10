package dk.sunepoulsen.tes.springboot.template.service;

import dk.sunepoulsen.tes.springboot.service.core.utils.SpringBootApplicationUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@EnableAsync
@SpringBootApplication( scanBasePackages = {
    SpringBootApplicationUtils.COMPONENT_SCAN_PACKAGES,
    "dk.sunepoulsen.tes.springboot.template.service"
})
public class Application {
    public static void main( String[] args ) {
        SpringApplication.run( Application.class, args );
    }
}
