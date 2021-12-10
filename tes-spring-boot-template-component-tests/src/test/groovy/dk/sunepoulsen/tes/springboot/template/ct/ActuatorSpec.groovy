package dk.sunepoulsen.tes.springboot.template.ct

import dk.sunepoulsen.tes.springboot.ct.core.http.HttpHelper
import dk.sunepoulsen.tes.springboot.ct.core.verification.HttpResponseVerificator
import spock.lang.Specification

import java.net.http.HttpRequest

class ActuatorSpec extends Specification {

    void "GET /actuator/health returns OK"() {
        given: 'Template service is available'
            DeploymentSpockExtension.templateBackendContainer().isHostAccessible()
            String baseUrl = "http://${DeploymentSpockExtension.templateBackendContainer().host}:${DeploymentSpockExtension.templateBackendContainer().getMappedPort(8080)}"

        when: 'Call GET /actuator/health'
            HttpHelper httpHelper = new HttpHelper()
            HttpRequest httpRequest = httpHelper.newRequestBuilder("${baseUrl}/actuator/health")
                .GET()
                .build()

            HttpResponseVerificator verificator = httpHelper.sendRequest(httpRequest)

        then: 'Response Code is 200'
            verificator.responseCode(200)

        and: 'Content Type is application/json'
            verificator.contentType('application/vnd.spring-boot.actuator.v3+json')

        and: 'Response body is json'
            verificator.bodyIsJson()

        and: 'Verify health body'
            verificator.bodyAsJson() == [
                'status': 'UP'
            ]
    }
}
