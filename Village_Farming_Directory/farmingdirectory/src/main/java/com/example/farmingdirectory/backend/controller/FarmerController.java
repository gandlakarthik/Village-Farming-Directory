package com.example.farmingdirectory.backend.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.farmingdirectory.backend.model.Farmer;
import com.example.farmingdirectory.backend.repository.FarmerRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Controller
public class FarmerController {

    private final FarmerRepository farmerRepository;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public FarmerController(FarmerRepository farmerRepository) {
        this.farmerRepository = farmerRepository;
    }

    // HOME PAGE

    @GetMapping("/")
    public String showHomePage() {
        return "login";
    }

    // LOGIN PAGE

    @GetMapping("/login")
    public String showLoginPage(HttpSession session) {

        if (session.getAttribute("farmerId") != null) {
            return "redirect:/dashboard";
        }

        return "login";
    }

    // PROCESS LOGIN

    @PostMapping("/login")
    public String processLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        Farmer farmer = farmerRepository.findByUsername(username);

        if (farmer != null
                && passwordEncoder.matches(password, farmer.getPassword())) {

            session.setAttribute("farmerId", farmer.getId());
            session.setAttribute("farmerName", farmer.getFullName());
            session.setAttribute("username", farmer.getUsername());

            return "redirect:/dashboard";
        }

        model.addAttribute("errorMessage",
                "Invalid username or password. Please try again.");

        return "login";
    }

    // REGISTRATION PAGE

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {

        model.addAttribute("farmer", new Farmer());

        return "register";
    }

    // PROCESS REGISTRATION

    @PostMapping("/register")
    public String registerFarmer(
            @ModelAttribute Farmer farmer,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {

        if (farmer.getFullName() == null
                || farmer.getFullName().trim().isEmpty()
                || farmer.getMobile() == null
                || farmer.getMobile().trim().isEmpty()
                || farmer.getVillage() == null
                || farmer.getVillage().trim().isEmpty()
                || farmer.getUsername() == null
                || farmer.getUsername().trim().isEmpty()
                || farmer.getPassword() == null
                || farmer.getPassword().trim().isEmpty()
                || farmer.getCrops() == null
                || farmer.getCrops().trim().isEmpty()) {

            model.addAttribute("errorMessage",
                    "Please fill in all required fields.");

            model.addAttribute("farmer", farmer);

            return "register";
        }

        if (!farmer.getMobile().matches("[0-9]{10}")) {

            model.addAttribute("errorMessage",
                    "Mobile number must contain exactly 10 digits.");

            model.addAttribute("farmer", farmer);

            return "register";
        }

        if (farmerRepository.existsByUsername(farmer.getUsername())) {

            model.addAttribute("errorMessage",
                    "Username already exists. Please choose another.");

            model.addAttribute("farmer", farmer);

            return "register";
        }

        if (farmerRepository.existsByMobile(farmer.getMobile())) {

            model.addAttribute("errorMessage",
                    "Mobile number is already registered.");

            model.addAttribute("farmer", farmer);

            return "register";
        }

        if (farmer.getPassword().length() < 6) {

            model.addAttribute("errorMessage",
                    "Password must contain at least 6 characters.");

            model.addAttribute("farmer", farmer);

            return "register";
        }

        if (!farmer.getPassword().equals(confirmPassword)) {

            model.addAttribute("errorMessage",
                    "Password and confirm password do not match.");

            model.addAttribute("farmer", farmer);

            return "register";
        }

        if (farmer.getLandArea() == null
                || farmer.getLandArea().compareTo(BigDecimal.ZERO) <= 0) {

            model.addAttribute("errorMessage",
                    "Land area must be greater than zero.");

            model.addAttribute("farmer", farmer);

            return "register";
        }

        if (farmer.getLandUnit() == null
                || farmer.getLandUnit().trim().isEmpty()) {

            model.addAttribute("errorMessage",
                    "Please select a land measurement unit.");

            model.addAttribute("farmer", farmer);

            return "register";
        }

        if (farmer.getCropSeason() == null
                || farmer.getCropSeason().trim().isEmpty()) {

            model.addAttribute("errorMessage",
                    "Please select a crop season.");

            model.addAttribute("farmer", farmer);

            return "register";
        }

        farmer.setPassword(
                passwordEncoder.encode(farmer.getPassword()));

        farmerRepository.save(farmer);

        return "redirect:/login?registered";
    }

    // DASHBOARD

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {

        if (session.getAttribute("farmerId") == null) {
            return "redirect:/login";
        }

        String farmerName =
                (String) session.getAttribute("farmerName");

        model.addAttribute("farmerName", farmerName);

        List<Farmer> farmers = farmerRepository.findAll();

        long totalFarmers = farmers.size();

        BigDecimal totalLandAcres = BigDecimal.ZERO;

        for (Farmer farmer : farmers) {

            if (farmer.getLandArea() != null
                    && farmer.getLandUnit() != null) {

                BigDecimal landArea = farmer.getLandArea();

                String landUnit = farmer.getLandUnit();

                if (landUnit.equalsIgnoreCase("Hectares")
                        || landUnit.equalsIgnoreCase("Hectare")) {

                    landArea = landArea.multiply(
                            new BigDecimal("2.47105"));
                }

                totalLandAcres =
                        totalLandAcres.add(landArea);
            }
        }

        totalLandAcres = totalLandAcres.setScale(
                2, RoundingMode.HALF_UP);

        model.addAttribute("totalFarmers", totalFarmers);

        model.addAttribute("totalLandAcres", totalLandAcres);

        return "dashboard";
    }

    // FARMER DIRECTORY WITH SEARCH

    @GetMapping("/farmers")
    public String showFarmers(
            @RequestParam(value = "keyword", required = false)
            String keyword,
            HttpSession session,
            Model model) {

        if (session.getAttribute("farmerId") == null) {
            return "redirect:/login";
        }

        List<Farmer> farmers;

        if (keyword == null || keyword.trim().isEmpty()) {

            farmers = farmerRepository.findAll();

            keyword = "";

        } else {

            farmers = farmerRepository.searchFarmers(keyword.trim());
        }

        model.addAttribute("farmers", farmers);

        model.addAttribute("searchKeyword", keyword);

        return "farmers";
    }

    // ADD FARMER PAGE

    @GetMapping("/add-farmer")
    public String showAddFarmerPage(
            HttpSession session,
            Model model) {

        if (session.getAttribute("farmerId") == null) {
            return "redirect:/login";
        }

        model.addAttribute("farmer", new Farmer());

        model.addAttribute("isEdit", false);

        return "farmer-form";
    }

    // SAVE NEW FARMER

    @PostMapping("/add-farmer")
    public String addFarmer(
            @ModelAttribute Farmer farmer,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpSession session,
            Model model) {

        if (session.getAttribute("farmerId") == null) {
            return "redirect:/login";
        }

        if (farmer.getFullName() == null
                || farmer.getFullName().trim().isEmpty()
                || farmer.getMobile() == null
                || farmer.getMobile().trim().isEmpty()
                || farmer.getVillage() == null
                || farmer.getVillage().trim().isEmpty()
                || farmer.getUsername() == null
                || farmer.getUsername().trim().isEmpty()
                || farmer.getPassword() == null
                || farmer.getPassword().trim().isEmpty()
                || farmer.getCrops() == null
                || farmer.getCrops().trim().isEmpty()) {

            model.addAttribute("errorMessage",
                    "Please fill in all required fields.");

            model.addAttribute("farmer", farmer);

            model.addAttribute("isEdit", false);

            return "farmer-form";
        }

        if (!farmer.getMobile().matches("[0-9]{10}")) {

            model.addAttribute("errorMessage",
                    "Mobile number must contain exactly 10 digits.");

            model.addAttribute("farmer", farmer);

            model.addAttribute("isEdit", false);

            return "farmer-form";
        }

        if (farmerRepository.existsByUsername(farmer.getUsername())) {

            model.addAttribute("errorMessage",
                    "Username already exists. Please choose another.");

            model.addAttribute("farmer", farmer);

            model.addAttribute("isEdit", false);

            return "farmer-form";
        }

        if (farmerRepository.existsByMobile(farmer.getMobile())) {

            model.addAttribute("errorMessage",
                    "Mobile number is already registered.");

            model.addAttribute("farmer", farmer);

            model.addAttribute("isEdit", false);

            return "farmer-form";
        }

        if (farmer.getPassword().length() < 6) {

            model.addAttribute("errorMessage",
                    "Password must contain at least 6 characters.");

            model.addAttribute("farmer", farmer);

            model.addAttribute("isEdit", false);

            return "farmer-form";
        }

        if (!farmer.getPassword().equals(confirmPassword)) {

            model.addAttribute("errorMessage",
                    "Password and confirm password do not match.");

            model.addAttribute("farmer", farmer);

            model.addAttribute("isEdit", false);

            return "farmer-form";
        }

        if (farmer.getLandArea() == null
                || farmer.getLandArea().compareTo(BigDecimal.ZERO) <= 0) {

            model.addAttribute("errorMessage",
                    "Land area must be greater than zero.");

            model.addAttribute("farmer", farmer);

            model.addAttribute("isEdit", false);

            return "farmer-form";
        }

        if (farmer.getLandUnit() == null
                || farmer.getLandUnit().trim().isEmpty()) {

            model.addAttribute("errorMessage",
                    "Please select a land measurement unit.");

            model.addAttribute("farmer", farmer);

            model.addAttribute("isEdit", false);

            return "farmer-form";
        }

        if (farmer.getCropSeason() == null
                || farmer.getCropSeason().trim().isEmpty()) {

            model.addAttribute("errorMessage",
                    "Please select a crop season.");

            model.addAttribute("farmer", farmer);

            model.addAttribute("isEdit", false);

            return "farmer-form";
        }

        farmer.setPassword(
                passwordEncoder.encode(farmer.getPassword()));

        farmerRepository.save(farmer);

        return "redirect:/farmers?added";
    }

    // EDIT FARMER PAGE

    @GetMapping("/edit-farmer/{id}")
    public String showEditFarmerPage(
            @PathVariable Long id,
            HttpSession session,
            Model model) {

        if (session.getAttribute("farmerId") == null) {
            return "redirect:/login";
        }

        Farmer farmer = farmerRepository.findById(id).orElse(null);

        if (farmer == null) {
            return "redirect:/farmers";
        }

        model.addAttribute("farmer", farmer);

        model.addAttribute("isEdit", true);

        return "farmer-form";
    }

    // UPDATE FARMER

    @PostMapping("/edit-farmer/{id}")
    public String updateFarmer(
            @PathVariable Long id,
            @ModelAttribute Farmer formFarmer,
            HttpSession session,
            Model model) {

        if (session.getAttribute("farmerId") == null) {
            return "redirect:/login";
        }

        Farmer existingFarmer =
                farmerRepository.findById(id).orElse(null);

        if (existingFarmer == null) {
            return "redirect:/farmers";
        }

        if (formFarmer.getFullName() == null
                || formFarmer.getFullName().trim().isEmpty()
                || formFarmer.getMobile() == null
                || formFarmer.getMobile().trim().isEmpty()
                || formFarmer.getVillage() == null
                || formFarmer.getVillage().trim().isEmpty()
                || formFarmer.getCrops() == null
                || formFarmer.getCrops().trim().isEmpty()) {

            model.addAttribute("errorMessage",
                    "Please fill in all required fields.");

            model.addAttribute("farmer", formFarmer);

            model.addAttribute("isEdit", true);

            return "farmer-form";
        }

        if (!formFarmer.getMobile().matches("[0-9]{10}")) {

            model.addAttribute("errorMessage",
                    "Mobile number must contain exactly 10 digits.");

            model.addAttribute("farmer", formFarmer);

            model.addAttribute("isEdit", true);

            return "farmer-form";
        }

        boolean mobileExists =
                farmerRepository.existsByMobile(formFarmer.getMobile());

        if (mobileExists
                && !existingFarmer.getMobile().equals(formFarmer.getMobile())) {

            model.addAttribute("errorMessage",
                    "This mobile number belongs to another farmer.");

            model.addAttribute("farmer", formFarmer);

            model.addAttribute("isEdit", true);

            return "farmer-form";
        }

        if (formFarmer.getLandArea() == null
                || formFarmer.getLandArea().compareTo(BigDecimal.ZERO) <= 0) {

            model.addAttribute("errorMessage",
                    "Land area must be greater than zero.");

            model.addAttribute("farmer", formFarmer);

            model.addAttribute("isEdit", true);

            return "farmer-form";
        }

        if (formFarmer.getLandUnit() == null
                || formFarmer.getLandUnit().trim().isEmpty()) {

            model.addAttribute("errorMessage",
                    "Please select a land measurement unit.");

            model.addAttribute("farmer", formFarmer);

            model.addAttribute("isEdit", true);

            return "farmer-form";
        }

        if (formFarmer.getCropSeason() == null
                || formFarmer.getCropSeason().trim().isEmpty()) {

            model.addAttribute("errorMessage",
                    "Please select a crop season.");

            model.addAttribute("farmer", formFarmer);

            model.addAttribute("isEdit", true);

            return "farmer-form";
        }

        // Update existing record

        existingFarmer.setFullName(formFarmer.getFullName());
        existingFarmer.setMobile(formFarmer.getMobile());
        existingFarmer.setVillage(formFarmer.getVillage());
        existingFarmer.setLandArea(formFarmer.getLandArea());
        existingFarmer.setLandUnit(formFarmer.getLandUnit());
        existingFarmer.setCrops(formFarmer.getCrops());
        existingFarmer.setCropSeason(formFarmer.getCropSeason());

        // Keep existing username and password unchanged

        farmerRepository.save(existingFarmer);

        return "redirect:/farmers?updated";
    }

    // DELETE FARMER

    @PostMapping("/delete-farmer/{id}")
    public String deleteFarmer(
            @PathVariable Long id,
            HttpSession session) {

        if (session.getAttribute("farmerId") == null) {
            return "redirect:/login";
        }

        if (farmerRepository.existsById(id)) {
            farmerRepository.deleteById(id);
        }

        return "redirect:/farmers?deleted";
    }

    // LOGOUT

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }
}
