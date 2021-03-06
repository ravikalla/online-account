pipeline {
    agent any
    parameters {
        string(name: 'MYSQL_ROOT_PASSWORD', defaultValue: 'root', description: 'MySQL password')
        string(name: 'DOCKER_USER', defaultValue: '', description: 'User ID of the Dockerhub')
        string(name: 'DOCKER_TOKEN', defaultValue: '', description: 'Token to upload docker image to dockerhub')
    }
    stages {
        stage ("Initialize Jenkins Env") {
         steps {
            sh '''
            echo "PATH = ${PATH}"
            echo "M2_HOME = ${M2_HOME}"
            '''
         }
        }
        stage('Download Code') { 
            steps { 
               echo 'checking out'
               checkout scm
            }
        }
        stage('Execute Tests'){
            steps {
                echo 'Testing'
                sh 'mvn test'
            }
        }
        stage('Code Quality Check'){
            steps {
                echo 'Sonar code quality check'
                sh 'mvn sonar:sonar -Dsonar.host.url=http://sonarqube:9000'
            }
        }
        stage('Build Application'){
            steps {
                echo 'Building...'
                sh 'mvn clean install -Dmaven.test.skip=true'
            }
        }
        stage('Build and Upload Docker Image') {
            steps {
                echo 'Building Docker image'
                sh 'docker build -t ravikalla/online-account:1 .'

                echo 'Uploading Docker image'
                sh '''
                docker tag ravikalla/online-account:1 ravikalla/online-account:1
                docker login --username=$DOCKER_USER --password=$DOCKER_TOKEN
                docker push ravikalla/online-account:1
                '''
            }
        }
       stage('Create Database') {
            steps {
                echo 'Running Database Image'
/* Commented as MySQL is replaced by H2
                sh 'docker stop bankmysql || true && docker rm bankmysql || true'
                sh 'docker run --detach --name=bankmysql --env="MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD}" -p 3306:3306 mysql'
                sh 'sleep 20'
                sh 'docker exec -i bankmysql mysql -uroot -p${MYSQL_ROOT_PASSWORD} < src/main/resources/import.sql'
*/
            }
        }
        stage('Deploy and Run') {
            steps {
                echo 'Running Application'
                sh 'docker stop cloudbank || true && docker rm cloudbank || true'
            //  Commented as MySQL is replaced by H2
            //  sh 'docker run --detach --name=cloudbank -p 8800:8800 --link bankmysql:localhost -t ravikalla/online-bank:1'
                sh 'docker run --detach --name=cloudbank -p 8800:8800 ravikalla/online-account:1'
            }
        }
    }
}
