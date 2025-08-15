package com.example.consumerservice.repositories;

import com.example.consumerservice.models.Events;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventRepository extends JpaRepository<Events,Long> {
    @Query(value = "SELECT * FROM events ORDER BY created_at DESC LIMIT :limit", nativeQuery = true)
    List<Events> findTopNByOrderByCreatedAtDesc(@Param("limit") int limit);

    @Query(value = "SELECT * FROM events WHERE type = :type ORDER BY created_at DESC LIMIT :limit", nativeQuery = true)
    List<Events> findTopNByTypeOrderByCreatedAtDesc(@Param("type") String type, @Param("limit") int limit);
}
