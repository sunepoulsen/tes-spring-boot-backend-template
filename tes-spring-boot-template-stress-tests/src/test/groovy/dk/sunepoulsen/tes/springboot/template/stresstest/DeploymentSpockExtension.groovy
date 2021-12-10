package dk.sunepoulsen.tes.springboot.template.stresstest

import dk.sunepoulsen.tes.springboot.ct.core.spock.DefaultDeploymentSpockExtension

class DeploymentSpockExtension extends DefaultDeploymentSpockExtension {
    static String COMPOSE_NAME = 'stress-tests'
    static String CONTAINER_NAME = 'tes-spring-boot-template-service'

    DeploymentSpockExtension() {
        super(COMPOSE_NAME, [CONTAINER_NAME])
    }
}
