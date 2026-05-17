//package org.example;
//
//import org.example.models.VehicleValidator;
//import org.example.repositories.RentalRepository;
//import org.example.repositories.UserRepository;
//import org.example.repositories.VehicleCategoryConfigRepository;
//import org.example.repositories.VehicleRepository;
//import org.example.repositories.impl.jdbc.RentalJdbcRepository;
//import org.example.repositories.impl.jdbc.UserJdbcRepository;
//import org.example.repositories.impl.jdbc.VehicleJdbcRepository;
//import org.example.repositories.impl.json.RentalJsonRepository;
//import org.example.repositories.impl.json.UserJsonRepository;
//import org.example.repositories.impl.json.VehicleCategoryConfigJsonRepository;
//import org.example.repositories.impl.json.VehicleJsonRepository;
//import org.example.services.*;
//
//public class Main {
//    public static void main(String[] args) {
//        if(args[0].equals("jdbc")){
//            VehicleJdbcRepository jdbcRepository = new VehicleJdbcRepository();
//            System.out.println(jdbcRepository.findAll());
//        }
//
//        UserRepository userRepository;
//        VehicleRepository vehicleRepository;
//        RentalRepository rentalRepository;
//        VehicleCategoryConfigRepository categoryConfigRepository = new VehicleCategoryConfigJsonRepository();
//
//        if (args[0].equals("jdbc")) {
//            System.out.println(">>> Uruchamianie w trybie Bazy Danych (JDBC Neon.tech) <<<");
//            userRepository = new UserJdbcRepository();
//            vehicleRepository = new VehicleJdbcRepository();
//            rentalRepository = new RentalJdbcRepository(vehicleRepository, userRepository);
//        } else {
//            System.out.println(">>> Uruchamianie w trybie Plików (JSON) <<<");
//            userRepository = new UserJsonRepository();
//            vehicleRepository = new VehicleJsonRepository();
//            rentalRepository = new RentalJsonRepository();
//        }
//
//        AuthService authService = new AuthService(userRepository);
//        VehicleCategoryConfigService categoryConfigService = new VehicleCategoryConfigService(categoryConfigRepository);
//        VehicleValidator vehicleValidator = new VehicleValidator(categoryConfigService);
//
//        RentalService rentalService = new RentalService(rentalRepository, vehicleRepository, userRepository);
//        VehicleService vehicleService = new VehicleService(vehicleRepository, rentalService, vehicleValidator);
//        UserService userService = new UserService(userRepository, rentalService);
//
//        UI ui = new UI(authService, vehicleService, rentalService, userService, categoryConfigService);
//        ui.start();
//    }
//}