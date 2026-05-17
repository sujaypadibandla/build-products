package tacoorderapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "tacoorderapp.feign")
public class TacoOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(TacoOrderApplication.class, args);
	}

}
