package dk.sunepoulsen.tes.springboot.template.ct

import dk.sunepoulsen.tes.springboot.client.core.rs.integrations.TechEasySolutionsBackendIntegrator
import dk.sunepoulsen.tes.springboot.client.core.rs.integrations.TechEasySolutionsClient
import dk.sunepoulsen.tes.springboot.client.core.rs.model.monitoring.ServiceHealth
import dk.sunepoulsen.tes.springboot.client.core.rs.model.monitoring.ServiceHealthStatusCode
import spock.lang.Specification

class ActuatorSpec extends Specification {

    TechEasySolutionsBackendIntegrator integrator

    void setup() {
        String baseUrl = "http://${DeploymentSpockExtension.templateBackendContainer().host}:${DeploymentSpockExtension.templateBackendContainer().getMappedPort(8080)}"
        TechEasySolutionsClient client = new TechEasySolutionsClient(new URI(baseUrl))
        this.integrator = new TechEasySolutionsBackendIntegrator(client)
    }

    void "GET /actuator/health returns OK"() {
        given: 'Template service is available'
            DeploymentSpockExtension.templateBackendContainer().isHostAccessible()

        when: 'Call GET /actuator/health'
            ServiceHealth result = integrator.health().blockingGet()

        then: 'Verify health body'
            result == new ServiceHealth(
                status: ServiceHealthStatusCode.UP
            )
    }
}
