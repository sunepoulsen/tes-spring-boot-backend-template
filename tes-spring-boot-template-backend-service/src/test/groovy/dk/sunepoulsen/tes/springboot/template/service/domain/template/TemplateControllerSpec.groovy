package dk.sunepoulsen.tes.springboot.template.service.domain.template

import dk.sunepoulsen.tes.springboot.client.core.rs.model.ServiceError
import dk.sunepoulsen.tes.springboot.service.core.domain.logic.ResourceNotFoundException
import dk.sunepoulsen.tes.springboot.service.core.domain.requests.ApiBadRequestException
import dk.sunepoulsen.tes.springboot.service.core.domain.requests.ApiNotFoundException
import dk.sunepoulsen.tes.springboot.template.client.rs.model.TemplateModel
import dk.sunepoulsen.tes.springboot.template.service.domain.persistence.model.TemplateEntity
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mapping.PropertyReferenceException
import org.springframework.data.util.ClassTypeInformation
import spock.lang.Specification
import spock.lang.Unroll

class TemplateControllerSpec extends Specification {

    TemplateLogic templateLogic
    TemplateController sut

    void setup() {
        templateLogic = Mock(TemplateLogic)
        sut = new TemplateController(templateLogic)
    }

    @Unroll
    void "Create Template returns OK: #_testcase"() {
        given:
            TemplateModel model = new TemplateModel(
                id: null,
                name: 'name',
                description: _description
            )
            TemplateModel expected = new TemplateModel(
                id: 1L,
                name: model.name,
                description: model.description
            )

        when:
            TemplateModel result = sut.create(model)

        then:
            result == expected
            1 * templateLogic.create(model) >> expected

        where:
            _testcase                 | _description
            'description is not null' | 'description'
            'description is null'     | null
    }

    @Unroll
    void "Create Template returns bad request: #_testcase"() {
        given:
            TemplateModel model = new TemplateModel(
                id: _id,
                name: _name
            )

        when:
            sut.create(model)

        then:
            ApiBadRequestException exception = thrown(ApiBadRequestException)
            exception.getServiceError().code == _errorCode
            exception.getServiceError().param == _errorParam
            exception.getServiceError().message == _errorMessage

            0 * templateLogic.create(_)

        where:
            _testcase        | _id  | _name  | _errorCode | _errorParam | _errorMessage
            'id is not null' | 10   | 'name' | null       | 'id'        | 'must be null'
            'name is null'   | null | null   | null       | 'name'      | 'must not be null'
    }

    void "Find all templates with unknown sorting"() {
        given:
            Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, 'wrong'))

        when:
            sut.findAll(pageable)

        then:
            ApiBadRequestException exception = thrown(ApiBadRequestException)
            exception.getServiceError() == new ServiceError(
                param: 'sort',
                message: 'Unknown sort property'
            )

            0 * templateLogic.create(_)
            1 * templateLogic.findAll(pageable) >> {
                throw new PropertyReferenceException('wrong', ClassTypeInformation.from(TemplateEntity.class), [])
            }
    }

    void "Get Template returns OK"() {
        given:
            TemplateModel model = new TemplateModel(
                id: 5L,
                name: 'name',
                description: 'description'
            )

        when:
            TemplateModel result = sut.get(model.id)

        then:
            result == model
            1 * templateLogic.get(model.id) >> model
    }

    void "Get Template gets IllegalArgumentException"() {
        when:
            sut.get(null)

        then:
            ApiBadRequestException exception = thrown(ApiBadRequestException)
            exception.getServiceError() == new ServiceError(
                param: 'id',
                message: 'message'
            )

            1 * templateLogic.get(null) >> {
                throw new IllegalArgumentException('message')
            }
    }

    void "Get Template gets ResourceNotFoundException"() {
        when:
            sut.get(null)

        then:
            ApiNotFoundException exception = thrown(ApiNotFoundException)
            exception.getServiceError() == new ServiceError(
                param: 'id',
                message: 'message'
            )

            1 * templateLogic.get(null) >> {
                throw new ResourceNotFoundException('id', 'message')
            }
    }

}
