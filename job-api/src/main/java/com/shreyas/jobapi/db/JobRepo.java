package com.shreyas.jobapi.db;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface JobRepo extends JpaRepository<JobEntity, UUID> {}