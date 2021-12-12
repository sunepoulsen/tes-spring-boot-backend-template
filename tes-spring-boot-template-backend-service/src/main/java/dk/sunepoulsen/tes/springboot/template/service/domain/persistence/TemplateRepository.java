package dk.sunepoulsen.tes.springboot.template.service.domain.persistence;

import dk.sunepoulsen.tes.springboot.template.service.domain.persistence.model.TemplateEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TemplateRepository extends PagingAndSortingRepository<TemplateEntity, Long> {
}
