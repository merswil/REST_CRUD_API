package com.example.restdemo.controller;


import com.example.restdemo.entity.Section;
import com.example.restdemo.service.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sections")
public class SectionController {

    @Autowired
    private SectionService sectionService;

    @GetMapping
    public List<Section> getAllSections() {
        return sectionService.getAllSections();
    }

    @GetMapping("/{id}")
    public Section getSectionById(@PathVariable Long id) {
        return sectionService.getSectionById(id)
                .orElseThrow(() -> new RuntimeException("Section not found with id: " + id));
    }

    @PostMapping
    public Section createSection(@RequestBody Section section) {
        return sectionService.createSection(section);
    }

    @PutMapping("/{id}")
    public Section updateSection(@PathVariable Long id, @RequestBody Section sectionDetails) {
        return sectionService.updateSection(id, sectionDetails);
    }

    @DeleteMapping("/{id}")
    public void deleteSection(@PathVariable Long id) {
        sectionService.deleteSection(id);
    }

    @GetMapping("/by-code")
    public List<Section> getSectionsByGeologicalClassCode(@RequestParam("code") String code) {
        return sectionService.findSectionsByGeologicalClassCode(code);
    }
}