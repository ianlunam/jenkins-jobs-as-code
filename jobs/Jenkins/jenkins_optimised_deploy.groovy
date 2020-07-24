folder("Jenkins")
buildMonitorView("Jenkins") {
    recurse(true)
    jobs {
        regex(".*Jenkins.*")
    }
}
pipelineJob("Jenkins/jenkins-optimised-deploy") {
    description("The purpose of this task is to deploy Jenkins service to the Jenkins ECS cluster")
    parameters {
        stringParam("branch", "master", "Jenkins-as-code Branch")
        choiceParam("ecrRepo", ['jenkins'], "ECR repo name")
        stringParam("dockerVersion", "Jenkins-jenkins-optimised-v3.0.0-70-ga588f5e", "Docker repo version number")
        stringParam("serviceName", "jenkins-optimised-service", "Service name")
        stringParam("ecsClusterName", "jenkins-optimised-ecs", "Ecs cluster name")
    }
    logRotator(numToKeep = 20)
    definition {
        cpsScm {
            scriptPath("jenkins/deploy/jenkins-optimised-deploy.gvy")
            scm {
                git {
                    remote {
                        url("https://github.com/ianlunam/jenkins-as-code.git")
                        credentials("github-user")
                    }
                    branch("*/\${branch}")
                }
            }
        }
    }
}
