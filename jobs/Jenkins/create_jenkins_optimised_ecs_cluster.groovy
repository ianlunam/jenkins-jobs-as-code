folder("Jenkins")
buildMonitorView("Jenkins") {
    recurse(true)
    jobs {
        regex(".*Jenkins.*")
    }
}
pipelineJob("Jenkins/create-jenkins-optimised-ecs-cluster") {
    description("The purpose of this task is to create a build ECS cluster for Jenkins. NB: If giving 24GB Ram to Jenkins service, this instance needs to be a c5.4xlarge")
    parameters {
        stringParam("branch", "master", "Jenkins-as-code Branch")
        stringParam("stackName", "jenkins-optimised-ecs", "ECS stack name")
        choiceParam("ec2InstanceType", ['c5.4xlarge | CPU-16 vCore | RAM-32 GB| Network-10 Gigabit', 'm5.xlarge | CPU-4 vCore | RAM-16 GB| Network-10 Gigabit', 'm5.large | CPU-2 vCore | RAM-8 GB | NetWork-10 Gigabit', 'm4.large | CPU-2 vCore | RAM-8 GB | Network-Moderate', 'm4.xlarge | CPU-4 vCore | RAM-16 GB | Network-High', 'c5.xlarge | CPU-4 vCore | RAM-8 GB | Network-10 Gigabit'], "Select the EC2 CPU instance type.")
    }
    logRotator(numToKeep = 20)
    definition {
        cpsScm {
            scriptPath("jenkins/infra/create-jenkins-optimised-cluster.gvy")
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
