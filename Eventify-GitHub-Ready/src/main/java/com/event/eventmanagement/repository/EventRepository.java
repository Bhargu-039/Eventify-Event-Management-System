package com.event.eventmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.event.eventmanagement.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {

    // 🔥 SEARCH USING ALL FIELDS (FOR SEARCH BAR)
    Event findByNameAndLocationAndDate(String name, String location, String date);

    // 🔥 FETCH EVENT FOR REGISTER PAGE (VERY IMPORTANT)
    Event findByName(String name);
}