folder("Jenkins")
buildMonitorView("Jenkins") {
    recurse(true)
    jobs {
        regex(".*Jenkins.*")
    }
}
pipelineJob("Jenkins/create-jenkins-ecs-cluster") {
    description("The purpose of this task is to create a build ECS cluster for Jenkins. NB: If giving 24GB Ram to Jenkins service, this instance needs to be a c5.4xlarge")
    parameters {
        stringParam("branch", "master", "Jenkins-as-code Branch")
        stringParam("projectName", "jenkins", "Project name")
        stringParam("stackName", "jenkins-ecs", "ECS stack name")
        choiceParam("ec2InstanceType", ['m5.xlarge', 'm5.large', 'm4.large', 'm4.xlarge', 'c5.xlarge', 'c5.4xlarge'], "Select the EC2 CPU instance type ")
        stringParam("sshKeyName", "AwsKey", "Project Ssh key")
    }
    logRotator(numToKeep = 20)
    definition {
        cpsScm {
            scriptPath("jenkins/infra/create-jenkins-cluster.gvy")
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
