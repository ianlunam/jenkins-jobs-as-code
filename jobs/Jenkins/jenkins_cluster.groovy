folder("Jenkins")
buildMonitorView("Jenkins") {
    recurse(true)
    jobs {
        regex(".*Jenkins.*")
    }
}
freeStyleJob("Jenkins/jenkins-cluster") {
    description("The purpose of this task is to create a build ECS cluster for Jenkins. NB: If giving 24GB Ram to Jenkins service, this instance needs to be a c5.4xlarge")
    parameters {
        stringParam("Branch", "master", "jenkins-jobs-as-code Branch")
        stringParam("StackName", "jenkins-ecs", "Cloudformation stack name")
        stringParam("S3Bucket", "my-bucket", "S3 bucket for template storage")
        stringParam("TemplateFile", "cloudformation/jenkins-cluster.json", "Cloudformation Template")
        choiceParam("InstanceType", ['t2.nano', 't2.micro', 't2.small', 't2.medium', 'm5.xlarge', 'm5.large', 'm4.large', 'm4.xlarge', 'c5.xlarge', 'c5.4xlarge'], "Select the EC2 CPU instance type ")
        stringParam("SshKeyName", "AwsKey", "Project Ssh key")
        stringParam("ParameterNames", "InstanceType SshKeyName", "List of parameters that need to be passed to cloudformation template")
    }
    logRotator(numToKeep = 20)
    steps {
        shell("scripts/create-cluster.sh")
    }
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
