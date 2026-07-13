package com.subscript.subscription.service.service.impl;

import com.subscript.subscription.api.model.Customer;
import com.subscript.subscription.api.model.User;
import com.subscript.subscription.api.wrapper.request.LoginRequest;
import com.subscript.subscription.api.wrapper.request.RegisterRequest;
import com.subscript.subscription.api.wrapper.response.LoginResponse;
import com.subscript.subscription.api.wrapper.response.RegisterResponse;
import com.subscript.subscription.service.repository.CustomerRepository;
import com.subscript.subscription.service.repository.UserRepository;
import com.subscript.subscription.service.service.interfaces.AuthService;
import com.subscript.subscription.service.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Registration flow:
     * - role=customer → creates User + Customer in one transaction
     * - employee roles → creates only a User (no customer record)
     *
     * If Customer creation throws, @Transactional rolls back the User insert too.
     */
    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        // 1. Guard duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // 2. Build & persist the User
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(User.Role.customer); // self-registration always creates a customer
        user.setStatus(User.Status.active);

        User savedUser = userRepository.save(user);

        // 3. Also create a linked Customer record (same transaction)
        Customer customer = new Customer();
        customer.setUser(savedUser);
        customer.setEmail(savedUser.getEmail());
        customer.setPhone(savedUser.getPhone());
        // Use companyName from request if provided, otherwise fall back to full name
        String company = (request.getCompanyName() != null && !request.getCompanyName().isBlank())
                ? request.getCompanyName()
                : savedUser.getFirstName() + " " + savedUser.getLastName();
        customer.setCompanyName(company);
        customer.setContactPerson(savedUser.getFirstName() + " " + savedUser.getLastName());
        customer.setStatus(Customer.Status.active);

        Customer savedCustomer = customerRepository.save(customer);

        return new RegisterResponse(
                "Registration successful",
                savedUser.getUserId(),
                savedCustomer.getCustomerId(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getRole().name());
    }

    /**
     * Login: Spring Security handles credential verification.
     * On success a signed JWT is returned.
     */
    @Override
    public LoginResponse login(LoginRequest request) {

        // BadCredentialsException thrown automatically on wrong password/email
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);

        return new LoginResponse(
                token,
                user.getEmail(),
                user.getFirstName(),
                user.getRole().name());
    }
}