package dk.sunepoulsen.tes.springboot.template.service.domain.template;

import dk.sunepoulsen.tes.springboot.client.core.rs.exceptions.ModelValidateException;
import dk.sunepoulsen.tes.springboot.client.core.rs.model.PaginationResult;
import dk.sunepoulsen.tes.springboot.client.core.rs.model.ServiceError;
import dk.sunepoulsen.tes.springboot.client.core.rs.transformations.PaginationTransformations;
import dk.sunepoulsen.tes.springboot.client.core.rs.validation.ModelValidator;
import dk.sunepoulsen.tes.springboot.service.core.domain.requests.ApiBadRequestException;
import dk.sunepoulsen.tes.springboot.template.client.rs.model.TemplateModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.cfg.defs.NullDef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TemplateController {

    private TemplateLogic templateLogic;

    @Autowired
    public TemplateController(TemplateLogic templateLogic) {
        this.templateLogic = templateLogic;
    }

    @RequestMapping( value = "/templates", method = RequestMethod.POST )
    @ResponseStatus( HttpStatus.CREATED )
    @Operation(summary = "Create a new template")
    @ApiResponses({
        @ApiResponse(responseCode = "201",
            description = "A new template has been created",
            content = { @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = TemplateModel.class)
            ) }
        ),
        @ApiResponse(responseCode = "400",
            description = "The template model is not valid",
            content = { @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ServiceError.class)
            ) }
        )
    })
    public TemplateModel create(
        @RequestBody
        @Parameter(description = "The template model to create")
        TemplateModel model)
    {
        try {
            ModelValidator.validate(model, TemplateModel.class, mappings -> mappings
                .field("id")
                .ignoreAnnotations(true)
                .constraint(new NullDef())
            );

            return templateLogic.create(model);
        }
        catch( ModelValidateException ex) {
            handleModelValidateException(ex);
            return null;
        }
    }

    @RequestMapping( value = "/templates", method = RequestMethod.GET )
    @ResponseStatus( HttpStatus.OK )
    public PaginationResult<TemplateModel> find(Pageable pageable) {
        return PaginationTransformations.toPaginationResult(templateLogic.findAll(pageable));
    }

    private void handleModelValidateException(ModelValidateException ex) {
        ex.getViolations().stream().findFirst().ifPresent(validateViolationModel -> {
            throw new ApiBadRequestException(validateViolationModel.getParam(), validateViolationModel.getMessage(), ex);
        });

        throw new ApiBadRequestException("Unknown validation error", ex);
    }
}
