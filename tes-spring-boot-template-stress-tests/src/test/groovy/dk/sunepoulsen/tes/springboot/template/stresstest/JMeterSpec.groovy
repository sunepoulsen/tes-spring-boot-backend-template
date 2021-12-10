package dk.sunepoulsen.tes.springboot.template.stresstest

import dk.sunepoulsen.tes.springboot.ct.core.docker.DockerIntegrator
import dk.sunepoulsen.tes.springboot.ct.core.util.ProcessUtils
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import spock.lang.Specification

@Slf4j
class JMeterSpec extends Specification {

    static File WORKING_DIR = new File('build/test-results/jmeter')

    void "Execute JMeter Test"() {
        given: 'Health service is available'
            DeploymentSpockExtension.deployment.waitForAvailable(DeploymentSpockExtension.CONTAINER_NAME)

        when: 'Find external ports'
            String port = DockerIntegrator.findExternalPort("${DeploymentSpockExtension.COMPOSE_NAME}_${DeploymentSpockExtension.CONTAINER_NAME}_1", DockerIntegrator.PRIVATE_SERVICE_PORT)
            log.info("Container ${DeploymentSpockExtension.CONTAINER_NAME} is accessible on port ${port}")

        and: 'Stress test working dir is empty'
            if (WORKING_DIR.exists()) {
                WORKING_DIR.deleteDir()
            }

            WORKING_DIR.mkdirs()

        and: 'Create user.properties file'
            String propertyFile = '/user.properties'
            String profile = System.getProperty('stress.test.profile')
            if (profile != null && !profile.empty) {
                propertyFile = "/user-${profile}.properties"
            }

            Properties properties = new Properties()
            properties.load(getClass().getResourceAsStream(propertyFile))

            properties.put('service.port', port)

            log.info("Using ${propertyFile} property file with the stress test")
            properties.store(new FileOutputStream(WORKING_DIR.path + '/stress-test.properties'), '')

        and: 'Execute JMeter test'
            boolean jMeterResult = ProcessUtils.execute('jmeter -n -t ../../../src/test/resources/stress-test.jmx -p stress-test.properties -l results.jtl -e -o report-html', WORKING_DIR)
            def statisticResults = new JsonSlurper().parse(new File(WORKING_DIR.path + '/report-html/statistics.json'))

        then: 'Verify stress test result'
            jMeterResult
            statisticResults.Total.errorPct == 0.0
    }

}
