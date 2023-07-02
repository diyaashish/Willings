package jp.loioz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import jp.loioz.common.annotation.FQCNGenerator;

@SpringBootApplication
@ComponentScan(nameGenerator = FQCNGenerator.class)
public class LoiozApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoiozApplication.class, args);
	}
}