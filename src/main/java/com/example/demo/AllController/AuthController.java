package com.example.demo.AllController;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.AllPOJO.User;
import com.example.demo.Services.UserService;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

  
    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        if (user.getMembershipMonths() != null && user.getMembershipMonths() > 0 && user.getMembershipStart() == null) {
            user.setMembershipStart(LocalDate.now());
        }

        System.out.println("Password is >>> " + user.getPassword());
        User saved = userService.createUser(user);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            System.out.println("EMAIL: " + req.getEmail());
            System.out.println("PASSWORD: " + req.getPassword());

            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );

            String token = jwtUtil.generateToken(req.getEmail());
            return ResponseEntity.ok(new LoginResponse(token));

        } catch (Exception e) {
            e.printStackTrace(); // ✅ show the real error
            return ResponseEntity.status(401).body("Invalid credentials: " + e.getMessage());
        }
    }


    // ✅ Get all users (secured)
    @GetMapping("/users")
    public ResponseEntity<List<User>> allUsers() {
        List<User> list = userService.getAllUsers();

        list.forEach(user -> {
            System.out.println("ID: " + user.getId());
            System.out.println("Name: " + user.getName());
            System.out.println("Email: " + user.getEmail());
            System.out.println("---------------------------");
        });

        return ResponseEntity.ok(list);
    }

    // ✅ Inner classes for request/response
    public static class LoginRequest {
        private String email;
        private String password;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginResponse {
        private String token;
        public LoginResponse(String token) { this.token = token; }
        public String getToken() { return token; }
    }
}
