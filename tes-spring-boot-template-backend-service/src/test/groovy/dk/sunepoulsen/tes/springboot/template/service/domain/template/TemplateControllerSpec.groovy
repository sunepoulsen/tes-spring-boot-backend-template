package dk.sunepoulsen.tes.springboot.template.service.domain.template

import dk.sunepoulsen.tes.springboot.service.core.domain.requests.ApiBadRequestException
import dk.sunepoulsen.tes.springboot.template.client.rs.model.TemplateModel
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
}
