#!/usr/bin/env groovy

pipeline {
    agent any
    stages {
        stage('Clean') {
            steps {
                echo 'Cleaning Project'
                sh 'chmod +x gradlew'
                sh './gradlew clean --no-daemon'
            }
        }
        stage('Tag Detection Test') {
            when {
                tag 'release-*'
            }
            environment {
                RELEASE_MODE = '1'
            }
            steps {
                echo 'Detected tag ${env.TAG_NAME}, not building snapshot'
            }
        }
        stage('Build and Deploy Snapshot') {
            when {
                not {
                    tag 'release-*'
                }
            }
            steps {
                sh './gradlew build sortArtifacts publish --no-daemon'
            }
        }
    }
    post {
        always {
            archive 'build/libs/**.jar'
        }
    }
}
