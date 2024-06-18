package com.hotel.management.application;

import com.hotel.management.application.dto.auth.RegisterRequestDto;
import com.hotel.management.application.entity.*;
import com.hotel.management.application.repository.*;
import com.hotel.management.application.service.HouseKeepingService;
import com.hotel.management.application.service.auth.AuthenticationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Random;

import static com.hotel.management.application.entity.user.Role.ADMIN;
import static com.hotel.management.application.entity.user.Role.CUSTOMER;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(AuthenticationService service, UserRepository userRepository, FeatureRepository featureRepository, FacilityRepository facilityRepository, RoomRepository roomRepository, EmployeeRepository employeeRepository, HouseKeepingRepository houseKeepingRepository) {
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

			if (featureRepository.findAll().isEmpty()) {
				for (int i = 1; i <= 10; i++) {
					Feature feature = new Feature();
					feature.setFname("Feature " + i);

					featureRepository.save(feature);
				}
			}

			if (facilityRepository.findAll().isEmpty()) {
				for (int i = 1; i <= 10; i++) {
					Facility facility = new Facility();
					facility.setFname("Feature " + i);

					facilityRepository.save(facility);
				}
			}

			if (roomRepository.findAll().isEmpty()) {
				for (int i = 1; i <= 10; i++) {
					Room room = new Room();
					Random random = new Random();

					room.setNo(i);
					room.setType("NORMAL");
					room.setCost(random.nextDouble(999));
					room.setCapacity(random.nextInt(10));
					room.setStatus(Room.Status.AVAILABLE);
					if (i == 1 || i == 2) {
						Feature f1 = new Feature();
						f1.setFname("Room " + i + " feature 1");

						Feature f2 = new Feature();
						f2.setFname("Room " + i + " feature 2");

						featureRepository.save(f1);
						featureRepository.save(f2);

						room.getFeatures().add(f1);
						room.getFeatures().add(f2);

						Facility fa1 = new Facility();
						f1.setFname("Room " + i + " facility 1");

						Facility fa2 = new Facility();
						f2.setFname("Room " + i + " facility 2");

						facilityRepository.save(fa1);
						facilityRepository.save(fa2);

						room.getFacilities().add(fa1);
						room.getFacilities().add(fa2);
					}

					roomRepository.save(room);
				}
			}

			if (houseKeepingRepository.findAll().isEmpty()) {
				for (int i = 1; i <= 10; i++) {
					HouseKeeping houseKeeping = new HouseKeeping();
					houseKeeping.setDate(new Date(System.currentTimeMillis()));
					houseKeeping.setStatus("IN_PROGRESS");
					houseKeeping.setTask("Doing stuff " + i);

					houseKeepingRepository.save(houseKeeping);
				}
			}

			if (employeeRepository.findAll().isEmpty()) {
				for (int i = 1; i <= 10; i++) {
					Employee employee = new Employee();
					Random random = new Random();

					employee.setFname("Employee");
					employee.setLname("No " + i);
					employee.setEmail("employee" + i + "@email.com");
					employee.setPhoneNo("0" + (543215431 + random.nextInt(543215431)));
					employee.setSalary(random.nextDouble(999));
					employee.setTasks(new ArrayList<>());

					employee = employeeRepository.save(employee);

					if (i == 1 || i == 2) {
						HouseKeeping houseKeeping = new HouseKeeping();
						houseKeeping.setDate(new Date(System.currentTimeMillis()));
						houseKeeping.setStatus("IN_PROGRESS");
						houseKeeping.setTask("Doing stuff for employee " + i);
						houseKeeping.setRoom(roomRepository.findAll().get(0));
						houseKeeping.setEmployee(employee);

						houseKeeping = houseKeepingRepository.save(houseKeeping);
						employee.getTasks().add(houseKeeping);
					}
				}
			}
		};
	}
}
