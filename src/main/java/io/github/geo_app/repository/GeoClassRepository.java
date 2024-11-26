package io.github.geo_app.repository;

import io.github.geo_app.model.GeoClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GeoClassRepository extends JpaRepository<GeoClass, Long> {

    GeoClass findByCode(String code);

    List<GeoClass> findGeoClassesBySectionsId(long id);
}
