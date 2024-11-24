package io.github.geo_app.repository;

import io.github.geo_app.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section, Long> {
    @Query("SELECT s FROM Section s JOIN FETCH s.geoClasses WHERE s.id = :id")
    Optional<Section> findByIdWithGeoClasses(@Param("id") long id);

    List<Section> findSectionsByGeoClassesId(long id);

    List<Section> findSectionsByGeoClassesCode(String code);
}
