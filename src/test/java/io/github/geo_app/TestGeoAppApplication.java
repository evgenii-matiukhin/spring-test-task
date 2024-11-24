package io.github.geo_app;

import org.springframework.boot.SpringApplication;

public class TestGeoAppApplication {

	public static void main(String[] args) {
		SpringApplication.from(GeoAppApplication::main).with(TestGeoAppApplication.class).run(args);
	}

}
