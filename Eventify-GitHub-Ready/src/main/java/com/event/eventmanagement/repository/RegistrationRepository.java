package com.event.eventmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.event.eventmanagement.model.Registration;

public interface RegistrationRepository extends JpaRepository<Registration, Long>{

}