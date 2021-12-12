package dk.sunepoulsen.tes.springboot.template.ct

import dk.sunepoulsen.tes.springboot.client.core.rs.exceptions.ClientBadRequestException
import dk.sunepoulsen.tes.springboot.client.core.rs.model.ServiceError
import dk.sunepoulsen.tes.springboot.template.client.rs.model.TemplateModel
import spock.lang.Specification

class TemplatesSpec extends Specification {

    void setup() {
        DeploymentSpockExtension.clearDatabase()
    }

    void "POST /templates returns OK"() {
        given: 'Template service is available'
            DeploymentSpockExtension.templateBackendContainer().isHostAccessible()

            TemplateModel model = new TemplateModel(
                name: 'name',
                description: 'description'
            )

        when: 'Call POST /templates'
            TemplateModel result = DeploymentSpockExtension.templateBackendIntegrator().create(model).blockingGet()

        then: 'Verify health body'
            result.id > 0L
            result == new TemplateModel(
                id: result.id,
                name: 'name',
                description: 'description'
            )
    }

    void "POST /templates returns BAD_REQUEST"() {
        given: 'Template service is available'
            DeploymentSpockExtension.templateBackendContainer().isHostAccessible()

        when: 'Call POST /templates'
            DeploymentSpockExtension.templateBackendIntegrator().create(
                new TemplateModel(
                    description: 'description'
                )
            ).blockingGet()

        then: 'Verify exception for bad_request'
            ClientBadRequestException exception = thrown(ClientBadRequestException)
            exception.serviceError == new ServiceError(
                param: 'name',
                message: 'must not be null'
            )
    }
}
