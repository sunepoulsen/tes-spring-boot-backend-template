package dk.sunepoulsen.tes.springboot.template.service.domain.template;

import dk.sunepoulsen.tes.springboot.template.service.domain.persistence.TemplatePersistence;
import dk.sunepoulsen.tes.springboot.template.service.domain.persistence.model.TemplateEntity;
import dk.sunepoulsen.tes.springboot.template.client.rs.model.TemplateModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TemplateLogic {
    private TemplateTransformations templateTransformations;
    private TemplatePersistence templatePersistence;

    @Autowired
    public TemplateLogic(TemplateTransformations templateTransformations, TemplatePersistence templatePersistence) {
        this.templateTransformations = templateTransformations;
        this.templatePersistence = templatePersistence;
    }

    TemplateModel create(TemplateModel model) {
        TemplateEntity entity = templateTransformations.toEntity(model);
        return templateTransformations.toModel(templatePersistence.create(entity));
    }
}
