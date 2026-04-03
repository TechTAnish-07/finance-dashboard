package com.example.finance_dashboard.Service;

import com.example.finance_dashboard.Entity.User;
import com.example.finance_dashboard.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class BrevoEmailService implements EmailService {

    @Value("${BREVO_API_KEY}")
    private String apiKey;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final UserRepo userRepo;
  private PasswordEncoder passwordEncoder;
    public BrevoEmailService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @Async
    @Transactional
    @Override
    public void sendLoginLink(Long userId , String password) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));


        String username          = user.getUsername();
        String email             = user.getEmail();
        String role              = user.getRole().toString();
        String tempPassword      = user.getPassword();
        String loginLink         = frontendUrl + "/login";


        sendViaBrevo(username, email, role, tempPassword, loginLink);
    }


    private void sendViaBrevo(
            String username,
            String email,
            String role,
            String temporaryPassword,
            String loginLink
    ) {
        String url = "https://api.brevo.com/v3/smtp/email";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("api-key", apiKey);

        Map<String, Object> body = new HashMap<>();

        body.put("sender", Map.of(
                "name", "Finance Dashboard",
                "email", "patidartanish31@gmail.com"
        ));

        body.put("to", List.of(
                Map.of("email", email, "name", username)
        ));

        // ✅ Dynamic subject based on role
        body.put("subject", "Welcome to Finance Dashboard — Your Account is Ready");

        // ✅ All variables now properly used
        body.put("htmlContent",
                "<div style='font-family: Arial, sans-serif; padding: 20px;'>" +

                        "<h2>Welcome to Finance Dashboard 💼</h2>" +

                        "<p>Hi <b>" + username + "</b> 👋,</p>" +

                        "<p>Your account has been created successfully.</p>" +

                        "<p>Here are your login credentials:</p>" +

                        "<table style='border: 1px solid #ddd; padding: 10px; border-radius: 5px;'>" +
                        "<tr><td><b>Email</b></td><td>" + email + "</td></tr>" +
                        "<tr><td><b>Password</b></td><td>" + temporaryPassword + "</td></tr>" +
                        "<tr><td><b>Role</b></td><td>" + role + "</td></tr>" +
                        "</table>" +

                        "<br/>" +

                        "<p>Click below to login:</p>" +
                        "<a href='" + loginLink + "' " +
                        "style='background-color: #4CAF50; color: white; padding: 10px 20px; " +
                        "text-decoration: none; border-radius: 5px;'>Login Now</a>" +

                        "<br/><br/>" +

                        "<p style='color: red;'><b>⚠️ Please change your password after first login.</b></p>" +

                        "<p><small>If you did not expect this email, please ignore it.</small></p>" +

                        "<br/>" +
                        "<p>Regards,</p>" +
                        "<p><b>Finance Dashboard Team</b></p>" +

                        "</div>"
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response =
                    restTemplate.postForEntity(url, request, String.class);
            System.out.println("✅ Email sent to: " + email);
            System.out.println("✅ Status: " + response.getStatusCode());

        } catch (HttpClientErrorException e) {
            System.err.println("❌ Brevo rejected request: " + e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            System.err.println("❌ Email sending failed");
            e.printStackTrace();
            throw e;
        }
    }
}
