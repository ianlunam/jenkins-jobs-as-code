folder("Jenkins")
buildMonitorView("Jenkins") {
    recurse(true)
    jobs {
        regex(".*Jenkins.*")
    }
}
freeStyleJob("Jenkins/optimised-jenkins-build") {
    description("None")
    steps {
        shell("./build-jenkins.sh")
    }
    wrappers {
        credentialsBinding {
            amazonWebServicesCredentialsBinding {
                accessKeyVariable("AWS_ACCESS_KEY_ID")
                secretKeyVariable("AWS_SECRET_ACCESS_KEY")
                credentialsId("aws-profile-credentials")
            }
        }
    }
    parameters {
        stringParam("branch", "master", "Jenkins-as-code Branch")
    }
    logRotator(numToKeep = 20)
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
