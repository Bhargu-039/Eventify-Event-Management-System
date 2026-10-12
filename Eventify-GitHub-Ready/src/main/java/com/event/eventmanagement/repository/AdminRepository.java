package com.event.eventmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.event.eventmanagement.model.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    // 🔥 CUSTOM LOGIN METHOD
    Admin findByEmailAndPassword(String email, String password);

    // 🔥 CHECK IF USER EXISTS
    Admin findByEmail(String email);
}