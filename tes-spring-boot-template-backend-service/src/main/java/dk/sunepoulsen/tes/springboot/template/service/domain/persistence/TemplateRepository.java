package dk.sunepoulsen.tes.springboot.template.service.domain.persistence;

import dk.sunepoulsen.tes.springboot.template.service.domain.persistence.model.TemplateEntity;
import org.springframework.data.repository.CrudRepository;

public interface TemplateRepository extends CrudRepository<TemplateEntity, Long> {
}
