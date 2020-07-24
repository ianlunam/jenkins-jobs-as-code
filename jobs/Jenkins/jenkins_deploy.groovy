folder("Jenkins")
buildMonitorView("Jenkins") {
    recurse(true)
    jobs {
        regex(".*Jenkins.*")
    }
}
pipelineJob("Jenkins/jenkins-deploy") {
    description("The purpose of this task is to deploy Jenkins service to the Jenkins ECS cluster")
    parameters {
        stringParam("Branch", "master", "jenkins-jobs-as-code Branch")
        stringParam("StackName", "jenkins-service", "Cloudformation stack name")
        stringParam("S3Bucket", "my-bucket", "S3 bucket for template storage")
        stringParam("TemplateFile", "cloudformation/jenkins-cluster.json", "Cloudformation Template")
        choiceParam("DockerRepo", ['jenkins'], "ECR repo name")
        stringParam("RepoVersion", "latest", "Docker repo version number")
        stringParam("EcsClusterName", "jenkins-cluster", "Ecs cluster name")
        stringParam("ParameterNames", "RepoVersion EcsClusterName DockerRepo", "List of parameters that need to be passed to cloudformation template")
    }

    logRotator(numToKeep = 20)
    definition {
        cpsScm {
            scriptPath("scripts/create-cluster.sh")
            scm {
                git {
                    remote {
                        url("https://github.com/ianlunam/jenkins-jobs-as-code.git")
                        credentials("github-user")
                    }
                    branch("*/\${Branch}")
                }
            }
        }
    }
}
