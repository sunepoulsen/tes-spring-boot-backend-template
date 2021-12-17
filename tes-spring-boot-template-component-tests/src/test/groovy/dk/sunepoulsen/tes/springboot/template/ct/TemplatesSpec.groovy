package dk.sunepoulsen.tes.springboot.template.ct

import dk.sunepoulsen.tes.springboot.client.core.rs.exceptions.ClientBadRequestException
import dk.sunepoulsen.tes.springboot.client.core.rs.exceptions.ClientNotFoundException
import dk.sunepoulsen.tes.springboot.client.core.rs.model.PaginationResult
import dk.sunepoulsen.tes.springboot.client.core.rs.model.ServiceError
import dk.sunepoulsen.tes.springboot.ct.core.http.HttpHelper
import dk.sunepoulsen.tes.springboot.ct.core.verification.HttpResponseVerificator
import dk.sunepoulsen.tes.springboot.template.client.rs.TemplateIntegrator
import dk.sunepoulsen.tes.springboot.template.client.rs.model.TemplateModel
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import spock.lang.Specification

import java.net.http.HttpRequest

class TemplatesSpec extends Specification {

    TemplateIntegrator integrator

    void setup() {
        this.integrator = DeploymentSpockExtension.templateBackendIntegrator()
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
            TemplateModel result = integrator.create(model).blockingGet()

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
            integrator.create(
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

    void "GET /templates with no sorting returns OK"() {
        given: 'Template service is available'
            DeploymentSpockExtension.templateBackendContainer().isHostAccessible()

            (1..5).each {
                integrator.create(new TemplateModel(
                    name: "name-${it}",
                    description: "description-${it}"
                )).blockingGet()
            }

        when: 'Call POST /templates'
            Pageable pageable = PageRequest.of(0, 20)
            PaginationResult<TemplateModel> result = integrator.findAll(pageable).blockingGet()

        then: 'Verify health body'
            result.metadata.page == 0
            result.metadata.size == 20
            result.metadata.totalPages == 1
            result.metadata.totalItems == 5
            result.results.size() == 5
            result.results[0].name == 'name-1'
    }

    void "GET /templates with sorting returns OK"() {
        given: 'Template service is available'
            DeploymentSpockExtension.templateBackendContainer().isHostAccessible()

            (1..5).each {
                integrator.create(new TemplateModel(
                    name: "name-${it}",
                    description: "description-${it}"
                )).blockingGet()
            }

        when: 'Call POST /templates'
            Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, 'name'))
            PaginationResult<TemplateModel> result = integrator.findAll(pageable).blockingGet()

        then: 'Verify health body'
            result.metadata.page == 0
            result.metadata.size == 20
            result.metadata.totalPages == 1
            result.metadata.totalItems == 5
            result.results.size() == 5
            result.results[0].name == 'name-5'
    }

    void "GET /templates with bad sorting property"() {
        given: 'Template service is available'
            DeploymentSpockExtension.templateBackendContainer().isHostAccessible()

        when: 'Call POST /templates'
            Pageable pageable = PageRequest.of(0, 20, Sort.by('wrong'))
            PaginationResult<TemplateModel> result = integrator.findAll(pageable).blockingGet()

        then: 'Verify health body'
            ClientBadRequestException exception = thrown(ClientBadRequestException)
            exception.serviceError == new ServiceError(
                param: 'sort',
                message: 'Unknown sort property'
            )
    }

    void "GET /templates with bad query parameters"() {
        given: 'Template service is available'
            DeploymentSpockExtension.templateBackendContainer().isHostAccessible()
            String baseUrl = "http://${DeploymentSpockExtension.templateBackendContainer().host}:${DeploymentSpockExtension.templateBackendContainer().getMappedPort(8080)}"

            (1..5).each {
                integrator.create(new TemplateModel(
                    name: "name-${it}",
                    description: "description-${it}"
                )).blockingGet()
            }

        when: 'Call GET /templates'
            HttpHelper httpHelper = new HttpHelper()
            HttpRequest httpRequest = httpHelper.newRequestBuilder("${baseUrl}/templates?size=wrong&page=0")
                .GET()
                .build()

            HttpResponseVerificator verificator = httpHelper.sendRequest(httpRequest)

        then: 'Response Code is 200'
            verificator.responseCode(200)

        and: 'Content Type is json'
            verificator.contentType('application/json')

        and: 'Check error body'
            PaginationResult<TemplateModel> body = verificator.bodyAsJsonOfType(PaginationResult)
            body.metadata.page == 0
            body.metadata.size == 20
            body.metadata.totalPages == 1
            body.metadata.totalItems == 5
            body.results.size() == 5
            body.results[0].name == 'name-1'
    }

    void "GET /templates/{id}: Found"() {
        given: 'Template service is available'
            DeploymentSpockExtension.templateBackendContainer().isHostAccessible()

            TemplateModel model = integrator.create(new TemplateModel(
                name: "name",
                description: "description"
            )).blockingGet()

        when: 'Call GET /templates/{id}'
            TemplateModel result = integrator.get(model.id).blockingGet()

        then: 'Verify returned template'
            result == model
    }

    void "GET /templates/{id}: Not Found"() {
        given: 'Template service is available'
            DeploymentSpockExtension.templateBackendContainer().isHostAccessible()

        when: 'Call GET /templates/{id}'
            integrator.get(1L).blockingGet()

        then: 'Verify returned template'
            ClientNotFoundException exception = thrown(ClientNotFoundException)
            exception.serviceError == new ServiceError(
                param: 'id',
                message: 'The resource does not exist'
            )
    }

    void "GET /templates/{id}: Id is null"() {
        given: 'Template service is available'
            DeploymentSpockExtension.templateBackendContainer().isHostAccessible()

        when: 'Call GET /templates/{id}'
            integrator.get(null).blockingGet()

        then: 'Verify returned template'
            thrown(NullPointerException)
    }

}
