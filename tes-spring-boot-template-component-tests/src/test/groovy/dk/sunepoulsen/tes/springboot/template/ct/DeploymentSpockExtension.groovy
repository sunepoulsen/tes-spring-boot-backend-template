package dk.sunepoulsen.tes.springboot.template.ct

import dk.sunepoulsen.tes.springboot.client.core.rs.integrations.TechEasySolutionsBackendIntegrator
import dk.sunepoulsen.tes.springboot.client.core.rs.integrations.TechEasySolutionsClient
import org.spockframework.runtime.extension.IGlobalExtension
import org.spockframework.runtime.model.SpecInfo
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

class DeploymentSpockExtension implements IGlobalExtension {
    private static GenericContainer templateBackendContainer = null

    static GenericContainer templateBackendContainer() {
        return templateBackendContainer
    }

    static TechEasySolutionsBackendIntegrator templateBackendIntegrator() {
        String baseUrl = "http://${templateBackendContainer().host}:${templateBackendContainer().getMappedPort(8080)}"
        TechEasySolutionsClient client = new TechEasySolutionsClient(new URI(baseUrl))

        return new TechEasySolutionsBackendIntegrator(client)
    }

    @Override
    void start() {
        DockerImageName imageName = DockerImageName.parse('tes-spring-boot-template-backend-service:1.0.0-SNAPSHOT')
        templateBackendContainer = new GenericContainer<>(imageName)
            .withEnv('SPRING_PROFILES_ACTIVE', 'ct')
            .withEnv('JAVA_OPTS', '-agentlib:jdwp=transport=dt_socket,address=8000,suspend=n,server=y')
            .withExposedPorts(8000, 8080)
            .waitingFor(
                Wait.forHttp('/actuator/health')
                    .forStatusCode(200)
            )
        templateBackendContainer.start()
    }

    @Override
    void visitSpec(SpecInfo spec) {
    }

    @Override
    void stop() {
        templateBackendContainer.stop()
    }
}
