package io.github.geo_app.controller;

import io.github.geo_app.model.GeoClass;
import io.github.geo_app.model.Section;
import io.github.geo_app.service.GeoClassService;
import io.github.geo_app.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/sections", produces = APPLICATION_JSON_VALUE)
public class SectionController {

    private final SectionService sectionService;

    private final GeoClassService geoClassService;

    @GetMapping
    public ResponseEntity<Page<Section>> getSections(Pageable pageable) {
        return ResponseEntity.ok(sectionService.getAllSections(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Section> getSectionById(@PathVariable("id") long id) {
        return ResponseEntity.ok(sectionService.getSectionById(id));
    }

    @PostMapping
    public ResponseEntity<Section> createSection(@RequestBody @Validated Section section) {
        Section result = sectionService.createSection(section);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Section> updateSection(
            @PathVariable("id") long id,
            @RequestBody @Validated Section section) {
        return ResponseEntity.ok(sectionService.updateSection(id, section));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteSection(@PathVariable("id") long id) {
        sectionService.deleteSection(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/geo-classes")
    public ResponseEntity<List<GeoClass>> getGeoClassesBySectionId(@PathVariable("id") long id) {
        return ResponseEntity.ok(geoClassService.getGeoClassesBySectionId(id));
    }

    @GetMapping("/by-code")
    public ResponseEntity<List<Section>> getGeoClassesBySectionId(@RequestParam("code") String code) {
        return ResponseEntity.ok(sectionService.findSectionsByGeoClassCode(code));
    }
}
