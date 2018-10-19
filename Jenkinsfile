#!groovy

node {
    stage("Checkout") {
        checkout scm
    }

    docker.image("node:9.5").inside {
        withEnv([
            /*
            Set home to our current directory because we do not have
            permission in the default directory
            */
            "HOME=.",
        ]) {
            stage("Install frontend dependencies") {
                sh "npm install grunt-cli bower"

                sh "npm install"
                sh "./node_modules/.bin/bower install --config.interactive=false"
            }

            stage("Build frontend") {
                sh "./node_modules/.bin/grunt build"
            }
        }
    }

    docker.image("maven:3.5-jdk-8-alpine").inside("-v $HOME/.m2:/root/.m2") {
        stage("Test backend") {
            sh "mvn test"
        }

        stage("Prebuild") {
            withCredentials([
                file(
                    credentialsId: "momus-local-properties",
                    variable: "localProperties"
                ),
                file(
                    credentialsId: "momus-gdrive-credentials",
                    variable: 'googleKey'
                )
            ]) {
                sh "cp ${localProperties} ${WORKSPACE}/src/main/resources/local.properties"
                sh "cp ${googleKey} ${WORKSPACE}/src/main/resources/googlekey.p12"
            }
        }

        stage("Build app") {
            sh ("mvn -Pprod -DskipTests install")
        }
    }

    if (env.BRANCH_NAME == "master") {
        stage("Deploy") {
            withCredentials([sshUserPrivateKey(
                credentialsId: "jenkins-ssh-key",
                keyFileVariable: "identityFile"
            )]) {
                sh ("scp -i ${identityFile} -o StrictHostKeyChecking=no " +
                    "target/momus.war java.smint.no:/home/jenkins/momus")
            }
        }
    }
}
