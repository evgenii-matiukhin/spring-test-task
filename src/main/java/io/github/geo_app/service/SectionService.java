package io.github.geo_app.service;

import io.github.geo_app.exceptions.SectionNotFoundException;
import io.github.geo_app.model.GeoClass;
import io.github.geo_app.model.Section;
import io.github.geo_app.repository.GeoClassRepository;
import io.github.geo_app.repository.SectionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final SectionRepository sectionRepository;

    private final GeoClassRepository geoClassRepository;

    public Page<Section> getAllSections(Pageable pageable) {
        return sectionRepository.findAll(pageable);
    }

    public Section getSectionById(long id) {
        return sectionRepository.findByIdWithGeoClasses(id)
                .orElseThrow(() -> new SectionNotFoundException(id));
    }

    @Transactional
    public Section createSection(Section section) {
        saveOrUpdateGeoClass(section);
        return sectionRepository.save(section);
    }

    public void saveOrUpdateGeoClass(Section section) {
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
    }

    @Transactional
    public Section updateSection(long id, Section updatedSection) {
        Section existingSection = sectionRepository.findById(id)
                .orElseThrow(() -> new SectionNotFoundException(id));
        existingSection.setName(updatedSection.getName());

        saveOrUpdateGeoClass(updatedSection);
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
