package com.example.restdemo.repository;

import com.example.restdemo.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    @Query("SELECT s FROM Section s JOIN s.geologicalClasses gc WHERE gc.code = :code AND gc.section.id = s.id")
    List<Section> findSectionsByGeologicalClassesCode(@Param("code") String code);
}
