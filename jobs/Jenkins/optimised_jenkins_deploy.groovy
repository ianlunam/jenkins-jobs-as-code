folder("Jenkins")
buildMonitorView("Jenkins") {
    recurse(true)
    jobs {
        regex(".*Jenkins.*")
    }
}
pipelineJob("Jenkins/optimised-jenkins-deploy") {
    description("The purpose of this task is to deploy Jenkins service to the Jenkins ECS cluster")
    parameters {
        stringParam("branch", "master", "Jenkins-as-code Branch")
        stringParam("projectName", "jenkins", "Project name")
        choiceParam("ecrRepo", ['jenkins'], "ECR repo name")
        stringParam("dockerVersion", "Jenkins-jenkins-v1.1.0-128-g20416e9", "Docker repo version number")
        stringParam("serviceName", "jenkins-optimised-service", "Service name")
        stringParam("ecsClusterName", "jenkins-optimised-cluster", "Ecs cluster name")
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
