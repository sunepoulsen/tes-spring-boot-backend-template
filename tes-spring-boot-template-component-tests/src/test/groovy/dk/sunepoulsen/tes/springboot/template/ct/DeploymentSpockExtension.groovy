package dk.sunepoulsen.tes.springboot.template.ct

import dk.sunepoulsen.tes.springboot.ct.core.spock.DefaultDeploymentSpockExtension

class DeploymentSpockExtension extends DefaultDeploymentSpockExtension {
    static String CONTAINER_NAME = 'tes-spring-boot-template-service'

    DeploymentSpockExtension() {
        super('ct', [CONTAINER_NAME])
    }
}