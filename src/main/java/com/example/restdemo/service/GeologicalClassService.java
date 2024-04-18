package com.example.restdemo.service;

import com.example.restdemo.entity.GeologicalClass;
import com.example.restdemo.repository.GeologicalClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GeologicalClassService {

    @Autowired
    private GeologicalClassRepository geologicalClassRepository;

    public List<GeologicalClass> getAllGeologicalClasses() {
        return geologicalClassRepository.findAll();
    }

    public Optional<GeologicalClass> getGeologicalClassById(Long id) {
        return geologicalClassRepository.findById(id);
    }

    public GeologicalClass createGeologicalClass(GeologicalClass geologicalClass) {
        return geologicalClassRepository.save(geologicalClass);
    }

    public GeologicalClass updateGeologicalClass(Long id, GeologicalClass geologicalClassDetails) {
        Optional<GeologicalClass> geologicalClassOptional = geologicalClassRepository.findById(id);
        if (geologicalClassOptional.isPresent()) {
            GeologicalClass geologicalClass = geologicalClassOptional.get();
            geologicalClass.setName(geologicalClassDetails.getName());
            geologicalClass.setCode(geologicalClassDetails.getCode());
            return geologicalClassRepository.save(geologicalClass);
        } else {
            // Handle error - GeologicalClass not found
            return null;
        }
    }

    public void deleteGeologicalClass(Long id) {
        geologicalClassRepository.deleteById(id);
    }
}
