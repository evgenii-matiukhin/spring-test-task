package io.github.geo_app.service;

import io.github.geo_app.exceptions.GeoClassNotFoundException;
import io.github.geo_app.model.GeoClass;
import io.github.geo_app.repository.GeoClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeoClassService {

    private final GeoClassRepository geoClassRepository;

    public Page<GeoClass> getAllGeoClasses(Pageable pageable) {
        return geoClassRepository.findAll(pageable);
    }

    public GeoClass getGeoClassById(long id) {
        return geoClassRepository.findById(id)
                .orElseThrow(() -> new GeoClassNotFoundException(id));
    }

    public GeoClass createGeoClass(GeoClass geoClass) {
        return geoClassRepository.save(geoClass);
    }

    public GeoClass updateGeoClass(long id, GeoClass updatedGeoClass) {
        GeoClass existingGeoClass = geoClassRepository.findById(id)
                .orElseThrow(() -> new GeoClassNotFoundException(id));
        existingGeoClass.setName(updatedGeoClass.getName());
        return geoClassRepository.save(existingGeoClass);
    }

    public void deleteGeoClass(long id) {
        geoClassRepository.deleteById(id);
    }

    public List<GeoClass> getGeoClassesBySectionId(long id) {
        return geoClassRepository.findGeoClassesBySectionsId(id);
    }
}
