package com.aaroen.spring.integration.persistence.repository;

import com.aaroen.spring.integration.persistence.entity.EmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<EmailEntity, Long> { }