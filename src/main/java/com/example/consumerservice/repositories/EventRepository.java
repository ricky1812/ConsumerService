package com.example.consumerservice.repositories;

import com.example.consumerservice.models.Events;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Events,Long> {
}
