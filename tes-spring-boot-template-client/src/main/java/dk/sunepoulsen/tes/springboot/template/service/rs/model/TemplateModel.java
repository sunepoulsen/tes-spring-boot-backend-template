package dk.sunepoulsen.tes.springboot.template.service.rs.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(description = "Template model")
public class TemplateModel {
    @NotNull
    @Schema(description = "Unique id of a template")
    private Long id;

    @NotNull
    @Schema(description = "The name of the template")
    private String name;

    @Schema(description = "The description of the template")
    private String description;
}
