package com.event.eventmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.event.eventmanagement.model.Event;

public interface CreateRepository extends JpaRepository<Event, Long> {

}