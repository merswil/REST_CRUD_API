package com.example.restdemo.repository;

import com.example.restdemo.entity.GeologicalClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeologicalClassRepository extends JpaRepository<GeologicalClass, Long> {
}
