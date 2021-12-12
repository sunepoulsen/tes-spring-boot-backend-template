package dk.sunepoulsen.tes.springboot.template.service.domain.persistence;

import dk.sunepoulsen.tes.springboot.template.service.domain.persistence.model.TemplateEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TemplatePersistence {
    private TemplateRepository repository;

    @Autowired
    public TemplatePersistence(TemplateRepository repository) {
        this.repository = repository;
    }

    public TemplateEntity create(TemplateEntity entity) {
        return repository.save(entity);
    }
}
