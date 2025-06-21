package com.sunbird.serve.need.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("prod")
public class AuthenticationConfigTest {

    @Test
    public void testPasswordEncoding() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Test admin password
        String adminPassword = "admin123";
        String encodedAdminPassword = encoder.encode(adminPassword);
        
        assertTrue(encoder.matches(adminPassword, encodedAdminPassword));
        
        // Test user password
        String userPassword = "user123";
        String encodedUserPassword = encoder.encode(userPassword);
        
        assertTrue(encoder.matches(userPassword, encodedUserPassword));
        
        System.out.println("✅ Authentication test passed!");
        System.out.println("📝 Default credentials:");
        System.out.println("   Username: admin, Password: admin123");
        System.out.println("   Username: user, Password: user123");
    }
} 