pipeline {
    agent any

    stages {
        stage("Checkout") {
            steps {
                checkout scm
            }
        }

        stage('Build frontend') {
            agent {
                docker {
                    reuseNode true
                    image 'node:9.5'
                }
            }
            environment {
                HOME = '.'
            }
            
            steps {
                sh 'npm install bower'

                sh 'npm install'
                sh './node_modules/.bin/bower install --config.interactive=false'

                sh 'npm run build'
            }
        }

        stage("Test backend") {
            agent {
                docker {
                    reuseNode true
                    image 'maven:3.5-jdk-8-alpine'
                    args '-v $HOME/.m2:/root/.m2'
                }
            }

            steps {
                sh "mvn test"
            }
        }

        stage("Prebuild") {
            environment {
                localProperties = credentials('momus-local-properties')
                googleKey = credentials('momus-gdrive-credentials')
            }

            steps {
                sh "cp ${localProperties} ${WORKSPACE}/src/main/resources/local.properties"
                sh "cp ${googleKey} ${WORKSPACE}/src/main/resources/googlekey.p12"
            }

        }

        stage("Build app") {
            agent {
                docker {
                    reuseNode true
                    image 'maven:3.5-jdk-8-alpine'
                    args '-v $HOME/.m2:/root/.m2'
                }
            }

            steps {
                sh ("mvn -Pprod -DskipTests install")
            }
        }
    }

    post {
        always {
            archiveArtifacts 'target/momus*.war'
            junit 'target/surefire-reports/*.xml'
        }
    }

}
// node {

//     stage("Checkout") {
//         checkout scm
//     }

//     docker.image("node:9.5").inside {
//         withEnv([
//             /*
//             Set home to our current directory because we do not have
//             permission in the default directory
//             */
//             "HOME=.",
//         ]) {
//             stage("Install frontend dependencies") {
//                 sh "npm install bower"

//                 sh "npm install"
//                 sh "./node_modules/.bin/bower install --config.interactive=false"
//             }

//             stage("Build frontend") {
//                 sh "npm run build"
//             }
//         }
//     }

//     docker.image("maven:3.5-jdk-8-alpine").inside("-v $HOME/.m2:/root/.m2") {
//         stage("Test backend") {
//             sh "mvn test"
//             junit 'target/surefire-reports/*.xml'
//         }

//         stage("Prebuild") {
//             withCredentials([
//                 file(
//                     credentialsId: "momus-local-properties",
//                     variable: "localProperties"
//                 ),
//                 file(
//                     credentialsId: "momus-gdrive-credentials",
//                     variable: 'googleKey'
//                 )
//             ]) {
//                 sh "cp ${localProperties} ${WORKSPACE}/src/main/resources/local.properties"
//                 sh "cp ${googleKey} ${WORKSPACE}/src/main/resources/googlekey.p12"
//             }
//         }

//         stage("Build app") {
//             sh ("mvn -Pprod -DskipTests install")
//         }
//     }

//     if (env.BRANCH_NAME == "master" && (currentBuild.result == 'SUCCESS')) {
//         stage("Deploy") {
//             withCredentials([sshUserPrivateKey(
//                 credentialsId: "jenkins-ssh-key",
//                 keyFileVariable: "identityFile"
//             )]) {
//                 sh ("scp -i ${identityFile} -o StrictHostKeyChecking=no " +
//                     "target/momus.war java.smint.no:/home/jenkins/momus")
//             }
//         }
//     }
// }
