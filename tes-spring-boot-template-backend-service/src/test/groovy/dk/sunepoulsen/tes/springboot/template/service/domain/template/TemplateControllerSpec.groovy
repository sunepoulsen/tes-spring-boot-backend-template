package dk.sunepoulsen.tes.springboot.template.service.domain.template

import dk.sunepoulsen.tes.springboot.service.core.domain.requests.ApiBadRequestException
import dk.sunepoulsen.tes.springboot.template.service.rs.model.TemplateModel
import spock.lang.Specification
import spock.lang.Unroll

class TemplateControllerSpec extends Specification {

    TemplateController sut

    void setup() {
        sut = new TemplateController()
    }

    void "Create Template returns OK"() {
        given:
            TemplateModel model = new TemplateModel(
                name: 'name'
            )

        when:
            sut.create(model)

        then:
            thrown(UnsupportedOperationException)
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

        where:
            _testcase        | _id | _name  | _errorCode | _errorParam | _errorMessage
            'id is not null' | 10  | 'name' | null       | 'id'        | 'must be null'
    }
}
