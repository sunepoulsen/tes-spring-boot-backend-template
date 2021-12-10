package dk.sunepoulsen.tes.springboot.template.ct

import dk.sunepoulsen.tes.springboot.ct.core.http.HttpHelper
import dk.sunepoulsen.tes.springboot.ct.core.verification.HttpResponseVerificator
import spock.lang.Specification

import java.net.http.HttpRequest

class SwaggerSpec extends Specification {

    void "GET /swagger-ui.html returns OK"() {
        given: 'Health service is available'
            DeploymentSpockExtension.deployment.waitForAvailable(DeploymentSpockExtension.CONTAINER_NAME)

        when: 'Call GET /swagger-ui.html'
            HttpHelper httpHelper = new HttpHelper(DeploymentSpockExtension.deployment)
            HttpRequest httpRequest = httpHelper.newRequestBuilder(DeploymentSpockExtension.CONTAINER_NAME, '/swagger-ui.html')
                .GET()
                .build()

            HttpResponseVerificator verificator = httpHelper.sendRequest(httpRequest)

        then: 'Response Code is 200'
            verificator.responseCode(200)

        and: 'Content Type is text/html'
            verificator.contentType('text/html')
    }
}