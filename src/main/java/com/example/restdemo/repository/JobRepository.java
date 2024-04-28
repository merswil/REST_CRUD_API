package com.example.restdemo.repository;

import com.example.restdemo.entity.AsyncJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<AsyncJob, Long> {
}

