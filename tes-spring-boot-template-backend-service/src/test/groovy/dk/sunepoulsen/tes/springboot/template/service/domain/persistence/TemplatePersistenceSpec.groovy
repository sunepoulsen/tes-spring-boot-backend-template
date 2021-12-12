package dk.sunepoulsen.tes.springboot.template.service.domain.persistence

import dk.sunepoulsen.tes.springboot.template.service.domain.persistence.model.TemplateEntity
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import spock.lang.Specification

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles(['ut'])
class TemplatePersistenceSpec extends Specification {

    @Autowired
    TemplateRepository templateRepository

    @Autowired
    TemplatePersistence templatePersistence

    void setup() {
        this.templateRepository.deleteAll()
    }

    void "Check injections"() {
        expect:
            templateRepository != null
            templatePersistence != null
    }

    void "Create new Template"() {
        given:
            TemplateEntity entity = new TemplateEntity(
                id: null,
                name: 'name',
                description: 'description'
            )

        when:
            TemplateEntity result = templatePersistence.create(entity)

        then:
            result == templateRepository.findById(result.id).get()
    }
}
