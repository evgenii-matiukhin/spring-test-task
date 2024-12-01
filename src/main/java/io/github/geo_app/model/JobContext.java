package io.github.geo_app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class JobContext {

    private JobType type;

    // input import file only
    private MultipartFile file;
}
