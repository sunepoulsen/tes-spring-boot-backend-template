package dk.sunepoulsen.tes.springboot.template.service.domain.persistence;

import dk.sunepoulsen.tes.springboot.template.service.domain.persistence.model.TemplateEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<TemplateEntity> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }
}
