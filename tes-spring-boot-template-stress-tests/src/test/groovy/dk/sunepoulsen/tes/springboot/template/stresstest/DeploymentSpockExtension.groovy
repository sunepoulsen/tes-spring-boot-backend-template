package dk.sunepoulsen.tes.springboot.template.stresstest

import groovy.util.logging.Slf4j
import org.spockframework.runtime.extension.IGlobalExtension
import org.spockframework.runtime.model.SpecInfo
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

@Slf4j
class DeploymentSpockExtension implements IGlobalExtension {
    private static GenericContainer templateBackendContainer = null
    private static GenericContainer postgresqlContainer = null

    static GenericContainer templateBackendContainer() {
        return templateBackendContainer
    }

    @Override
    void start() {
        DockerImageName imageName

        Network network = Network.newNetwork()

        imageName = DockerImageName.parse('postgres:latest')
        postgresqlContainer = new GenericContainer<>(imageName)
            .withEnv('POSTGRES_DB', 'stress-test')
            .withEnv('POSTGRES_USER', 'template')
            .withEnv('POSTGRES_PASSWORD', 'jukilo90')
            .withExposedPorts(5432)
            .withNetwork(network)
            .withNetworkAliases('postgres')
        postgresqlContainer.start()

        imageName = DockerImageName.parse('tes-spring-boot-template-backend-service:1.0.0-SNAPSHOT')
        templateBackendContainer = new GenericContainer<>(imageName)
            .withEnv('SPRING_PROFILES_ACTIVE', 'stress-test')
            .withEnv('JAVA_OPTS', '-agentlib:jdwp=transport=dt_socket,address=8000,suspend=n,server=y')
            .withClasspathResourceMapping('/config/application-stress-test.yml', '/app/resources/application-stress-test.yml', BindMode.READ_ONLY)
            .withExposedPorts(8000, 8080)
            .withNetwork(network)
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
        templateBackendContainer.copyFileFromContainer('/app/logs/service.log', 'build/logs/tes-spring-boot-template-backend-service.log')
        templateBackendContainer.stop()

        postgresqlContainer.stop()
    }
}
