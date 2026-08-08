package com.careercompass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * CareerCompass - Exam Rank & College Predictor Platform
 *
 * Main application class for the CareerCompass Spring Boot application.
 * Helps students predict colleges based on exam rank, category, and quotas.
 *
 * @author CareerCompass Team
 * @version 1.0.0
 */
@SpringBootApplication
public class CareerCompassApplication {

    public static void main(String[] args) {
        SpringApplication.run(CareerCompassApplication.class, args);
        System.out.println("=" .repeat(60));
        System.out.println("CareerCompass Application Started Successfully!");
        System.out.println("Access at: http://localhost:8080");
        System.out.println("=" .repeat(60));
    }
}
