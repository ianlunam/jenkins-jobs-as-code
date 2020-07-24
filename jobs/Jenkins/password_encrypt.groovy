folder("Jenkins")
buildMonitorView("Jenkins") {
    recurse(true)
    jobs {
        regex(".*Jenkins.*")
    }
}
pipelineJob("Jenkins/password-encrypt") {
    description("Encrypt passwords for jbcrypt usage")
    parameters {
        stringParam("password", "MyObScUrEpAsSwOrD", "Password to encrypt")
    }
    logRotator(numToKeep = 20)
    definition {
        cps {
            script("import org.mindrot.jbcrypt.BCrypt\nprintln \"Encrypted password: #jbcrypt:\" + BCrypt.hashpw(password, BCrypt.gensalt());")
        }
    }
}
