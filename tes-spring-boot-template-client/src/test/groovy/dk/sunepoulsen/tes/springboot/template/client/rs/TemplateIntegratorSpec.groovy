package dk.sunepoulsen.tes.springboot.template.client.rs

import dk.sunepoulsen.tes.springboot.client.core.rs.exceptions.ClientBadRequestException
import dk.sunepoulsen.tes.springboot.client.core.rs.integrations.TechEasySolutionsClient
import dk.sunepoulsen.tes.springboot.client.core.rs.model.PaginationResult
import dk.sunepoulsen.tes.springboot.client.core.rs.model.PaginationResultMetaData
import dk.sunepoulsen.tes.springboot.client.core.rs.model.ServiceError
import dk.sunepoulsen.tes.springboot.template.client.rs.model.TemplateModel
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import spock.lang.Specification
import spock.lang.Unroll

import java.util.concurrent.CompletableFuture

class TemplateIntegratorSpec extends Specification {

    private TechEasySolutionsClient client
    private TemplateIntegrator sut

    void setup() {
        this.client = Mock(TechEasySolutionsClient)
        this.sut = new TemplateIntegrator(this.client)
    }

    void "Create new template: OK"() {
        given:
            TemplateModel model = new TemplateModel(name: 'name')

        when:
            TemplateModel result = sut.create(model).blockingGet()

        then:
            result.name == 'name'

            1 * client.post('/templates', model, TemplateModel) >> CompletableFuture.completedFuture(model)
    }

    void "Create new template: Map exception"() {
        given:
            TemplateModel model = new TemplateModel(name: 'name')

        when:
            sut.create(new TemplateModel(name: 'name')).blockingGet()

        then:
            thrown(ClientBadRequestException)

            1 * client.post('/templates', model, TemplateModel) >> CompletableFuture.supplyAsync(() -> {
                throw new ClientBadRequestException(null, new ServiceError(message: 'message'))
            })
    }

    void "Get templates with no pagination: OK"() {
        when:
            PaginationResult<TemplateModel> result = sut.findAll().blockingGet()

        then:
            result.metadata.totalItems == 20

            1 * client.get('/templates', PaginationResult) >> CompletableFuture.completedFuture(
                new PaginationResult<TemplateModel>(
                    metadata: new PaginationResultMetaData(
                        totalItems: 20
                    )
                )
            )
    }

    @Unroll
    void "Get templates with pagination: #_testcase"() {
        when:
            PaginationResult<TemplateModel> result = sut.findAll(_pagination).blockingGet()

        then:
            result.metadata.totalItems == 20

            1 * client.get("/templates?${_query}", PaginationResult) >> CompletableFuture.completedFuture(
                new PaginationResult<TemplateModel>(
                    metadata: new PaginationResultMetaData(
                        totalItems: 20
                    )
                )
            )

        where:
            _testcase                       | _query                                        | _pagination
            'Default page'                  | 'page=0&size=20'                              | PageRequest.of(0, 20)
            'Sort by one field'             | 'page=0&size=20&sort=field1'                  | PageRequest.of(0, 20, Sort.by('field1'))
            'Sort by one field descending'  | 'page=0&size=20&sort=field1,desc'             | PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, 'field1'))
            'Sort by two fields'            | 'page=0&size=20&sort=field1&sort=field2'      | PageRequest.of(0, 20, Sort.by('field1', 'field2'))
            'Sort by two fields descending' | 'page=0&size=20&sort=field1&sort=field2,desc' | PageRequest.of(0, 20, Sort.by([Sort.Order.asc('field1'), Sort.Order.desc('field2')]))
    }

    void "Get template: OK"() {
        given:
            TemplateModel foundModel = new TemplateModel(id: 5L, name: 'name')

        when:
            TemplateModel result = sut.get(5L).blockingGet()

        then:
            result == foundModel

            1 * client.get('/templates/5', TemplateModel) >> CompletableFuture.completedFuture(foundModel)
    }

    void "Get template: Map Exceptions"() {
        when:
            sut.get(5L).blockingGet()

        then:
            ClientBadRequestException exception = thrown(ClientBadRequestException)
            exception.getServiceError() == new ServiceError(
                message: 'message'
            )

            1 * client.get('/templates/5', TemplateModel) >> CompletableFuture.supplyAsync(() -> {
                throw new ClientBadRequestException(null, new ServiceError(message: 'message'))
            })
    }
}
