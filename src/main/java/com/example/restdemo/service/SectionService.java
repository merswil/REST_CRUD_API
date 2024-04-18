package com.example.restdemo.service;

import com.example.restdemo.entity.Section;
import com.example.restdemo.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SectionService {

    @Autowired
    private SectionRepository sectionRepository;

    public List<Section> getAllSections() {
        return sectionRepository.findAll();
    }

    public Optional<Section> getSectionById(Long id) {
        return sectionRepository.findById(id);
    }

    public Section createSection(Section section) {
        return sectionRepository.save(section);
    }

    public Section updateSection(Long id, Section sectionDetails) {
        Optional<Section> sectionOptional = sectionRepository.findById(id);
        if (sectionOptional.isPresent()) {
            Section section = sectionOptional.get();
            section.setName(sectionDetails.getName());
            section.setGeologicalClasses(sectionDetails.getGeologicalClasses());
            return sectionRepository.save(section);
        } else {
            // Handle error - Section not found
            return null;
        }
    }

    public void deleteSection(Long id) {
        sectionRepository.deleteById(id);
    }

    public List<Section> getSectionsByGeologicalClassCode(String code) {
        return sectionRepository.findByGeologicalClassesCode(code);
    }
}

