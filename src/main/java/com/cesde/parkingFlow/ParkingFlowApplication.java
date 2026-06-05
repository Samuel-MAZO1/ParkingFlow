package com.cesde.parkingFlow;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.cesde.parkingFlow.entity.Parqueadero;
import com.cesde.parkingFlow.entity.User;
import com.cesde.parkingFlow.enums.Rol;
import com.cesde.parkingFlow.repository.ParqueaderoRepository;
import com.cesde.parkingFlow.repository.UserRepository;

@SpringBootApplication
public class ParkingFlowApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParkingFlowApplication.class, args);
	}
	
	@Bean
    public CommandLineRunner initData(UserRepository userRepository,
                                      PasswordEncoder passwordEncoder, 
                                      ParqueaderoRepository parqueaderoRepository
                                      ) {
       
	    return args -> {
           
	    	String emailAdmin = "admin@gmail.com";
        	
        	if(userRepository.findByEmail(emailAdmin).isEmpty()) {
        		
            User admin = new User();
           
            admin.setEmail(emailAdmin);
            admin.setActivo(true);
            admin.setPassword(passwordEncoder.encode("Admin123"));
            
            admin.setName("Mazo");
            admin.setLastName("Bravo");
            admin.setDocument("10339393");
            admin.setRol(Rol.ADMIN);
            admin.setPhone("1234567");
            
            userRepository.save(admin);
            System.out.println("Admin: " + admin.getName() + " está aquí");
        	}
            
            if (parqueaderoRepository.count() == 0) {
                Parqueadero sedePrincipal = new Parqueadero();
                // Seteamos el ID manualmente a 1 para que coincida con nuestro Service
               
                sedePrincipal.setNombre("Parqueadero Central");
                sedePrincipal.setDireccion("Calle 10 #45-20");
                sedePrincipal.setCapacidadFisicaTotal(60);
                sedePrincipal.setOcupacionGlobal(0);

                parqueaderoRepository.save(sedePrincipal);
                System.out.println(">>>> Sede principal del parqueadero inicializada con 60 cupos.");
            } 
        	
    };
	}
}