pipeline {
    agent any
    options { skipDefaultCheckout() }

    environment {
        momusWar = 'target/momus-*.war'
        testReports = 'target/surefire-reports/*.xml'
        teamsUrl = 'https://outlook.office.com/webhook/18a676a7-fd27-4e0a-8b81-fd91abd9692a@c845b8fa-0078-426b-8679-0da9f5eb0eed/JenkinsCI/d103706acbc94615aac713a658615647/9f437a56-fe33-4eb8-b7eb-6bfdf3ac4f70'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

	    stage('Set up environment') {
            environment {
                localProperties = credentials('momus-local-properties')
                googleKey = credentials('momus-gdrive-credentials')
            }

            steps {
                sh "cp ${localProperties} ${WORKSPACE}/src/main/filters/local.properties"
                sh "cp ${googleKey} ${WORKSPACE}/src/main/resources/googlekey.p12"
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
                sh 'npm ci'

                sh 'npm run build'
                sh 'npm run build:beta'
            }
        }

        stage('Test backend') {
            agent {
                docker {
                    reuseNode true
                    image 'maven:3.5-jdk-8-alpine'
                    args '-v $HOME/.m2:/root/.m2'
                }
            }

            steps {
                sh 'mvn test'
            }

            post {
                always {
                    junit testReports
                }
            }
        }

        stage('Build app') {
            agent {
                docker {
                    reuseNode true
                    image 'maven:3.5-jdk-8-alpine'
                    args '-v $HOME/.m2:/root/.m2'
                }
            }

            steps {
                sh 'mvn -Pprod -DskipTests install'
            }
        }

        stage('Deploy') {
            when {
                branch 'master'
            }

            environment {
                serverHost = 'java.smint.no'
                serverWarDir = '/home/jenkins/momus'
            }

            steps {
                sshagent (
                    credentials: ['jenkins-ssh-key']
                ) {
                    sh "scp -o StrictHostKeyChecking=no ${momusWar} ${serverHost}:${serverWarDir}"
}
            }
        }
    }

    post {
        always {
            script {
                if(BRANCH_NAME == 'master') {
                    archiveArtifacts momusWar
                }

                if(BRANCH_NAME in ['master', 'develop']) {
                    notifyTeams(teamsUrl, currentBuild.currentResult)
                }
            }
        }
    }
}
