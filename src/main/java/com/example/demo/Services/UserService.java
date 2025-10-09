package com.example.demo.Services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.AllPOJO.User;
import com.example.demo.AllPOJO.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public User createUser(User user){
        // hash password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if(user.getMembershipStart()==null && user.getMembershipMonths()!=null && user.getMembershipMonths()>0){
            user.setMembershipStart(LocalDate.now());
        }
        return userRepository.save(user);
    }

    public boolean validateLogin(String email, String rawPassword){
        return userRepository.findByEmail(email)
                .map(u -> passwordEncoder.matches(rawPassword, u.getPassword()))
                .orElse(false);
    }

    public boolean isMembershipValid(User user){
        if(user.getMembershipStart()==null || user.getMembershipMonths()==null) return false;
        LocalDate expiry = user.getMembershipStart().plusMonths(user.getMembershipMonths());
        return !LocalDate.now().isAfter(expiry);
    }

    public User updateMembership(Long userId, LocalDate start, Integer months){
        User u = userRepository.findById(userId).orElseThrow();
        u.setMembershipStart(start);
        u.setMembershipMonths(months);
        return userRepository.save(u);
    }

    public Optional<User> getById(Long id){
        return userRepository.findById(id);
    }
}

