package io.github.geo_app.controller;

import io.github.geo_app.model.GeoClass;
import io.github.geo_app.service.GeoClassService;
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
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/geo-classes", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class GeoClassController {

    private final GeoClassService geoClassService;

    @GetMapping
    public ResponseEntity<Page<GeoClass>> getGeoClasses(Pageable pageable) {
        return ResponseEntity.ok(geoClassService.getAllGeoClasses(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeoClass> getGeoClassById(@PathVariable("id") long id) {
        return ResponseEntity.ok(geoClassService.getGeoClassById(id));
    }

    @PostMapping
    public ResponseEntity<GeoClass> createGeoClass(@RequestBody @Validated GeoClass geoClass) {
        GeoClass result = geoClassService.createGeoClass(geoClass);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeoClass> updateGeoClass(
            @PathVariable("id") long id,
            @RequestBody @Validated GeoClass geoClass) {
        return ResponseEntity.ok(geoClassService.updateGeoClass(id, geoClass));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteGeoClass(@PathVariable("id") long id) {
        geoClassService.deleteGeoClass(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
