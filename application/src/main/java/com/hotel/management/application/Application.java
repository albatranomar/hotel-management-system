package com.hotel.management.application;

import com.hotel.management.application.dto.auth.RegisterRequestDto;
import com.hotel.management.application.repository.UserRepository;
import com.hotel.management.application.service.auth.AuthenticationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import static com.hotel.management.application.entity.user.Role.ADMIN;
import static com.hotel.management.application.entity.user.Role.CUSTOMER;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(AuthenticationService service, UserRepository userRepository) {
		return args -> {
			String adminEmail = "admin@mail.com";
			if (userRepository.findByEmail(adminEmail).isEmpty()) {
				var admin = RegisterRequestDto.builder()
						.firstname("Admin")
						.lastname("Admin")
						.email(adminEmail)
						.password("password")
						.role(ADMIN)
						.build();
				System.out.println("Admin token: " + service.register(admin).getAccessToken());
			}

			String customerEmail = "Customer@mail.com";
			if (userRepository.findByEmail(customerEmail).isEmpty()) {
				var customer = RegisterRequestDto.builder()
						.firstname("Customer")
						.lastname("Customer")
						.email(customerEmail)
						.password("password")
						.role(CUSTOMER)
						.build();
				System.out.println("Customer token: " + service.register(customer).getAccessToken());
			}
		};
	}
}
