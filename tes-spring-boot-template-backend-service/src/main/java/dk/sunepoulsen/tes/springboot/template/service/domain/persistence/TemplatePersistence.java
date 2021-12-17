package dk.sunepoulsen.tes.springboot.template.service.domain.persistence;

import dk.sunepoulsen.tes.springboot.service.core.domain.logic.ResourceNotFoundException;
import dk.sunepoulsen.tes.springboot.template.service.domain.persistence.model.TemplateEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public TemplateEntity get(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("May not be null");
        }

        Optional<TemplateEntity> entity = repository.findById(id);
        if (entity.isEmpty()) {
            throw new ResourceNotFoundException("id", "The resource does not exist");
        }

        return entity.get();
    }
}
