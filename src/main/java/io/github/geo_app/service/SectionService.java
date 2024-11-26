package io.github.geo_app.service;

import io.github.geo_app.exceptions.SectionNotFoundException;
import io.github.geo_app.model.GeoClass;
import io.github.geo_app.model.Section;
import io.github.geo_app.repository.GeoClassRepository;
import io.github.geo_app.repository.SectionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SectionService {

    @Autowired
    private final SectionRepository sectionRepository;

    @Autowired
    private final GeoClassRepository geoClassRepository;

    public Page<Section> getAllSections(Pageable pageable) {
        return sectionRepository.findAll(pageable);
    }

    public Section getSectionById(long id) {
        return sectionRepository.findByIdWithGeoClasses(id)
                .orElseThrow(() -> new SectionNotFoundException(id));
    }

    public Section createSection(Section section) {
        Set<GeoClass> uniqueClasses = new HashSet<>();
        for (GeoClass gc : section.getGeoClasses()) {
            GeoClass existingClass = geoClassRepository.findByCode(gc.getCode());
            if (existingClass != null) {
                uniqueClasses.add(existingClass);
            } else {
                uniqueClasses.add(gc);
            }
        }
        section.setGeoClasses(uniqueClasses);
        return sectionRepository.save(section);
    }

    public Section updateSection(long id, Section updatedSection) {
        Section existingSection = sectionRepository.findById(id)
                .orElseThrow(() -> new SectionNotFoundException(id));
        existingSection.setName(updatedSection.getName());

        Set<GeoClass> uniqueClasses = new HashSet<>();
        for (GeoClass gc : updatedSection.getGeoClasses()) {
            GeoClass existingClass = geoClassRepository.findByCode(gc.getCode());
            if (existingClass != null) {
                uniqueClasses.add(existingClass);
            } else {
                uniqueClasses.add(gc);
            }
        }
        existingSection.setGeoClasses(uniqueClasses);
        return sectionRepository.save(existingSection);
    }

    @Transactional
    public void deleteSection(long id) {
        sectionRepository.deleteById(id);
    }

    public List<Section> findSectionsByGeoClassCode(String code) {
        return sectionRepository.findSectionsByGeoClassesCode(code);
    }
}
