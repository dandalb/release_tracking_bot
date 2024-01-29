package com.example.release_tracking_bot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("local")
@TestPropertySource(locations = "classpath:./application-local.properties")
class ReleaseTrackingBotApplicationTests {
    @Test
    void contextLoads() {
    }
}
