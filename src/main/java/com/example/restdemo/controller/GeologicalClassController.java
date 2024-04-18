package com.example.restdemo.controller;

import com.example.restdemo.entity.GeologicalClass;
import com.example.restdemo.service.GeologicalClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/geologicalClasses")
public class GeologicalClassController {

    @Autowired
    private GeologicalClassService geologicalClassService;

    @GetMapping
    public List<GeologicalClass> getAllGeologicalClasses() {
        return geologicalClassService.getAllGeologicalClasses();
    }

    @GetMapping("/{id}")
    public GeologicalClass getGeologicalClassById(@PathVariable Long id) {
        return geologicalClassService.getGeologicalClassById(id)
                .orElseThrow(() -> new RuntimeException("GeologicalClass not found with id: " + id));
    }

    @PostMapping
    public GeologicalClass createGeologicalClass(@RequestBody GeologicalClass geologicalClass) {
        return geologicalClassService.createGeologicalClass(geologicalClass);
    }

    @PutMapping("/{id}")
    public GeologicalClass updateGeologicalClass(@PathVariable Long id, @RequestBody GeologicalClass geologicalClassDetails) {
        return geologicalClassService.updateGeologicalClass(id, geologicalClassDetails);
    }

    @DeleteMapping("/{id}")
    public void deleteGeologicalClass(@PathVariable Long id) {
        geologicalClassService.deleteGeologicalClass(id);
    }
}