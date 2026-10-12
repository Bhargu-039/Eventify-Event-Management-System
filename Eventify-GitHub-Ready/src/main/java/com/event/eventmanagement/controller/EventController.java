package com.event.eventmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.event.eventmanagement.model.Event;
import com.event.eventmanagement.model.Registration;
import com.event.eventmanagement.model.Admin;
import com.event.eventmanagement.repository.EventRepository;
import com.event.eventmanagement.repository.RegistrationRepository;
import com.event.eventmanagement.repository.AdminRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class EventController {

    @Autowired
    private EventRepository eventRepo;

    @Autowired
    private RegistrationRepository regRepo;

    @Autowired
    private AdminRepository adminRepo;

    // =========================================================
    // ✅ GLOBAL USER (AVAILABLE IN ALL PAGES AUTOMATICALLY)
    // =========================================================
    @ModelAttribute
    public void addGlobalUser(Model model, HttpSession session) {

        Admin user = (Admin) session.getAttribute("loggedUser");

        model.addAttribute("user", user);
        model.addAttribute("isLoggedIn", user != null);
        model.addAttribute("isAdmin", user != null && "ADMIN".equals(user.getRole()));
    }

    // ================= HOME =================
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // ================= LOGOUT =================
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // ================= AUTH =================
    @GetMapping("/signin")
    public String signin() {
        return "signin";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("admin", new Admin());
        return "signup";
    }

    // ================= SIGNUP =================
    @PostMapping("/signup")
    public String signupUser(@ModelAttribute Admin admin, Model model) {

        admin.setEmail(admin.getEmail().trim());
        admin.setPassword(admin.getPassword().trim());

        Admin existingUser = adminRepo.findByEmail(admin.getEmail());

        if (existingUser != null) {
            model.addAttribute("error", "Email already registered!");
            return "signup";
        }

        if (admin.getRole() == null || admin.getRole().isEmpty()) {
            admin.setRole("USER");
        }

        adminRepo.save(admin);

        return "redirect:/signin";
    }

    // ================= LOGIN =================
    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        Model model,
                        HttpSession session) {

        email = email.trim();
        password = password.trim();

        Admin user = adminRepo.findByEmail(email);

        if (user != null && user.getPassword() != null
                && user.getPassword().equals(password)) {

            session.setAttribute("loggedUser", user);

            String redirectUrl = (String) session.getAttribute("redirectAfterLogin");

            if (redirectUrl != null) {
                session.removeAttribute("redirectAfterLogin");
                return "redirect:" + redirectUrl;
            }

            return "redirect:/events";
        }

        model.addAttribute("error", "Invalid Email or Password");
        return "signin";
    }

    // ================= EVENTS =================
    @GetMapping("/events")
    public String showEvents(Model model) {

        List<Event> list = eventRepo.findAll();
        model.addAttribute("events", list);

        return "events";
    }

    // ================= CREATE EVENT =================
    @GetMapping("/create")
    public String createEvent(Model model, HttpSession session) {

        Admin user = (Admin) session.getAttribute("loggedUser");

        if (user == null) {
            session.setAttribute("redirectAfterLogin", "/create");
            return "redirect:/signin";
        }

        if (!"ADMIN".equals(user.getRole())) {
            return "redirect:/events?error=notauthorized";
        }

        model.addAttribute("event", new Event());
        return "create-event";
    }

    // ================= SAVE EVENT =================
    @PostMapping("/saveEvent")
    public String saveEvent(@ModelAttribute Event event,
                            HttpSession session) {

        Admin user = (Admin) session.getAttribute("loggedUser");

        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/signin";
        }

        if (event.getStatus() == null || event.getStatus().isEmpty()) {
            event.setStatus("Upcoming");
        }

        if (event.getTicketType() == null || event.getTicketType().isEmpty()) {
            event.setTicketType("Free");
        }

        if (event.getPrice() == null) {
            event.setPrice(0.0);
        }

        if (event.getCapacity() == null) {
            event.setCapacity(100);
        }

        if (event.getLocation() == null || event.getLocation().isEmpty()) {
            event.setLocation("Not Specified");
        }

        if (event.getDate() == null || event.getDate().isEmpty()) {
            event.setDate("TBD");
        }

        eventRepo.save(event);

        return "redirect:/events";
    }

    // ================= UPDATE EVENT STATUS =================
    @PostMapping("/updateStatus/{id}")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam String status,
                               HttpSession session) {

        Admin user = (Admin) session.getAttribute("loggedUser");

        if (user == null) {
            return "redirect:/signin";
        }

        if (!"ADMIN".equals(user.getRole())) {
            return "redirect:/events?error=notauthorized";
        }

        Event event = eventRepo.findById(id).orElse(null);

        if (event != null) {
            event.setStatus(status);
            eventRepo.save(event);
        }

        return "redirect:/events";
    }

    // ================= DELETE EVENT =================
    @GetMapping("/deleteEvent/{id}")
    public String deleteEvent(@PathVariable Long id,
                              HttpSession session) {

        Admin user = (Admin) session.getAttribute("loggedUser");

        if (user == null) {
            return "redirect:/signin";
        }

        if (!"ADMIN".equals(user.getRole())) {
            return "redirect:/events?error=notauthorized";
        }

        eventRepo.deleteById(id);

        return "redirect:/events?deleted=true";
    }

    // ================= REGISTER PAGE =================
    @GetMapping("/register/{id}")
    public String registerPage(@PathVariable Long id, Model model) {

        Event event = eventRepo.findById(id).orElse(null);

        if (event == null) {
            return "redirect:/events";
        }

        Registration reg = new Registration();
        reg.setEventName(event.getName());

        model.addAttribute("reg", reg);
        model.addAttribute("event", event);

        return "register";
    }

    // ================= SAVE REGISTRATION =================
    @PostMapping("/saveRegistration")
    public String saveRegistration(@ModelAttribute Registration reg,
                                   Model model) {

        reg.setStatus("Confirmed");
        reg.setTicketId("EVT" + System.currentTimeMillis());

        regRepo.save(reg);

        model.addAttribute("reg", reg);

        return "ticket";
    }

    // ================= ATTENDEES =================
    @GetMapping("/attendees")
    public String attendees(Model model) {

        List<Registration> list = regRepo.findAll();

        model.addAttribute("list", list);

        model.addAttribute("totalTickets",
                list.stream().mapToInt(r -> r.getTickets()).sum());

        model.addAttribute("checkedIn",
                list.stream().filter(r -> "Checked-In".equals(r.getStatus())).count());

        model.addAttribute("pending",
                list.stream().filter(r -> !"Checked-In".equals(r.getStatus())).count());

        return "attendees";
    }

    // ================= CHECK-IN =================
    @PostMapping("/checkin/{id}")
    public String checkIn(@PathVariable Long id) {

        Registration reg = regRepo.findById(id).orElse(null);

        if (reg != null) {

            if ("Checked-In".equals(reg.getStatus())) {
                return "redirect:/attendees?already=true";
            }

            reg.setStatus("Checked-In");
            regRepo.save(reg);

            return "redirect:/attendees?success=true";
        }

        return "redirect:/attendees?error=true";
    }

    // ================= DELETE REGISTRATION =================
    @PostMapping("/deleteRegistration/{id}")
    public String deleteRegistration(@PathVariable Long id) {

        regRepo.deleteById(id);

        return "redirect:/attendees?deleted=true";
    }
}