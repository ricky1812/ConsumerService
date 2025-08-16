package com.example.consumerservice.repositories;

import com.example.consumerservice.models.Events;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventRepository extends JpaRepository<Events,Long> {

    @Query("SELECT e FROM events e ORDER BY e.createdAt DESC")
    List<Events> findTopNByOrderByCreatedAtDesc(Pageable pageable);
    @Query("SELECT e FROM events e WHERE e.type IN :types ORDER BY e.createdAt DESC")
    List<Events> findTopNByTypeInOrderByCreatedAtDesc(@Param("types") List<String> types, Pageable pageable);

    default List<Events> findTopNByOrderByCreatedAtDesc(int limit) {
        return findTopNByOrderByCreatedAtDesc(PageRequest.of(0, limit));
    }

    default List<Events> findTopNByTypeInOrderByCreatedAtDesc(List<String> types, int limit) {
        return findTopNByTypeInOrderByCreatedAtDesc(types, PageRequest.of(0, limit));
    }
    boolean existsByEventID(String eventID);
}
