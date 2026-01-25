package com.shreyas.jobapi.outbox;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OutboxRepo extends JpaRepository<OutboxEventEntity, UUID> {

  @Query("select e from OutboxEventEntity e where e.processedAt is null order by e.createdAt asc")
  List<OutboxEventEntity> findUnprocessed(Pageable pageable);
}