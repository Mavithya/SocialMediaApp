package com.example.social_media_app.controller;

import com.example.social_media_app.model.User;
import com.example.social_media_app.security.CustomUserDetails;
import com.example.social_media_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @GetMapping("/profile/settings")
    public String showSettings(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("user", user);
        return "profileSetings"; // Make sure name matches your HTML file
    }

    @GetMapping("/profile/current")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findByEmail(authentication.getName());

            Map<String, Object> response = new HashMap<>();
            response.put("id", currentUser.getId());
            response.put("firstName", currentUser.getFirstName());
            response.put("lastName", currentUser.getLastName());
            response.put("email", currentUser.getEmail());
            response.put("username", currentUser.getUsername());
            response.put("profilePicture", currentUser.getProfilePicture());
            response.put("city", currentUser.getCity());
            response.put("country", currentUser.getCountry());
            response.put("education", currentUser.getEducation());
            response.put("workplace", currentUser.getWorkplace());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("user") User updatedUser,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        try {
            User existingUser = userService.findByEmail(userDetails.getUsername());

            // Check if email is being changed and if new email already exists
            if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
                if (userService.existsByEmail(updatedUser.getEmail())) {
                    redirectAttributes.addFlashAttribute("error",
                            "Email address is already in use ,Choose a different one");
                    return "redirect:/home";
                }
            }

            // Only allow editing certain fields
            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            existingUser.setEmail(updatedUser.getEmail());

            // Update username based on email if it's being changed
            if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
                String newUsername = updatedUser.getEmail().split("@")[0];
                existingUser.setUsername(newUsername);
            }

            // Only update password if provided and not empty
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().trim().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

            userService.save(existingUser);
            
            // Update the SecurityContext with the updated user information

            CustomUserDetails updatedUserDetails = new CustomUserDetails(existingUser);
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    updatedUserDetails, 
                    null, // credentials not needed for authenticated user
                    updatedUserDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(newAuth);
            
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update profile. Please try again.");
        }

        return "redirect:/home";
    }

    @PostMapping("/profile/update-ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateProfileAjax(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "country", required = false) String country,
            @RequestParam(value = "education", required = false) String education,
            @RequestParam(value = "workplace", required = false) String workplace,
            @RequestParam(value = "profilePictureFile", required = false) MultipartFile profilePictureFile) {
        
        Map<String, Object> response = new HashMap<>();

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User existingUser = userService.findByEmail(authentication.getName());


            if (email != null && !existingUser.getEmail().equals(email)) {
                if (userService.existsByEmail(email)) {
                    response.put("success", false);
                    response.put("message", "Email address is already in use by another account.");
                    return ResponseEntity.badRequest().body(response);
                }
            }

            // Update user fields
            if (firstName != null && !firstName.trim().isEmpty()) {
                existingUser.setFirstName(firstName);
            }
            if (lastName != null && !lastName.trim().isEmpty()) {
                existingUser.setLastName(lastName);
            }
            if (email != null && !email.trim().isEmpty()) {
                existingUser.setEmail(email);
                // Update username based on new email
                String newUsername = email.split("@")[0];
                existingUser.setUsername(newUsername);
            }

            // Handle profile picture upload
            if (profilePictureFile != null && !profilePictureFile.isEmpty()) {
                try {
                    String profilePictureUrl = saveProfilePicture(profilePictureFile);
                    existingUser.setProfilePicture(profilePictureUrl);
                } catch (IOException e) {
                    response.put("success", false);
                    response.put("message", "Failed to upload profile picture. Please try again.");
                    return ResponseEntity.badRequest().body(response);
                }
            }

            // Update other profile fields
            if (city != null) {
                existingUser.setCity(city.trim().isEmpty() ? null : city);
            }
            if (country != null) {
                existingUser.setCountry(country.trim().isEmpty() ? null : country);
            }
            if (education != null) {
                existingUser.setEducation(education.trim().isEmpty() ? null : education);
            }
            if (workplace != null) {
                existingUser.setWorkplace(workplace.trim().isEmpty() ? null : workplace);
            }

            // Update password only if provided
            if (password != null && !password.trim().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(password));
            }

            userService.save(existingUser);


            // This ensures that the authentication principal has the latest user data
            CustomUserDetails updatedUserDetails = new CustomUserDetails(existingUser);
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    updatedUserDetails, 
                    authentication.getCredentials(), 
                    updatedUserDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            // Return updated user data
            response.put("success", true);
            response.put("message", "Profile updated successfully!");

            Map<String, Object> userData = Map.of(
                    "id", existingUser.getId(),
                    "firstName", existingUser.getFirstName(),
                    "lastName", existingUser.getLastName(),
                    "email", existingUser.getEmail(),
                    "username", existingUser.getUsername(),
                    "profilePicture", existingUser.getProfilePicture() != null ? existingUser.getProfilePicture() : "",
                    "city", existingUser.getCity() != null ? existingUser.getCity() : "",
                    "country", existingUser.getCountry() != null ? existingUser.getCountry() : "",
                    "education", existingUser.getEducation() != null ? existingUser.getEducation() : "",
                    "workplace", existingUser.getWorkplace() != null ? existingUser.getWorkplace() : "");

            response.put("user", userData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update profile. Please try again.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    private String saveProfilePicture(MultipartFile file) throws IOException {
        // Create upload directory if it doesn't exist
        File uploadDirFile = new File(uploadDir + "/profiles");
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + fileExtension;

        // Save file
        Path filePath = Paths.get(uploadDir + "/profiles/" + filename);
        Files.write(filePath, file.getBytes());

        // Return URL path
        return "/uploads/profiles/" + filename;
    }
}
