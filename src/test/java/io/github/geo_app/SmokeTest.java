package io.github.geo_app;

import io.github.geo_app.controller.GeoClassController;
import io.github.geo_app.controller.SectionController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SmokeTest {
    @Autowired
    private SectionController sectionController;

    @Autowired
    private GeoClassController geoClassController;

    @Test
    void contextLoads() throws Exception {
        assertThat(sectionController).isNotNull();
        assertThat(geoClassController).isNotNull();
    }
}
