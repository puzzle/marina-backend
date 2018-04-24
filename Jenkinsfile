pipeline {
    agent { label 'buildnode' }
    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
        timeout(time: 10, unit: 'MINUTES')
    }
    triggers {
        pollSCM('H/5 * * * *')
    }
    parameters {
        string(name: 'build_project', defaultValue: 'pitc-marina-build', description: 'The OpenShift Build Project')
        string(name: 'dev_project', defaultValue: 'pitc-marina-dev', description: 'The OpenShift Dev Project')
        string(name: 'test_project', defaultValue: 'pitc-marina-test', description: 'The OpenShift Test Project')
        string(name: 'prod_project', defaultValue: 'pitc-marina-prod', description: 'The OpenShift Prod Project')
    }
    stages {
        stage('Build') {
            steps {
                sh './gradlew build'
            }
        }
        stage('Build Docker Image') {
            steps {
                script{
                	def ocDir = tool "oc"
                	withEnv(["PATH+OC=${ocDir}/bin"]) {
		                openshift.withCluster('OpenShiftPuzzleProduction', 'openshiftv3_prod_token_client_plugin' ) {
						    openshift.withProject(${params.build_project}) {
						        echo "Running in project: ${openshift.project()}"
						        def buildSelector = openshift.startBuild("marina-backend")
								buildSelector.logs('-f')
						    }
						}
					}
				}
            }
        }
        stage('Deploy to Dev') {
            steps {
                script{
                	def ocDir = tool "oc"
                	withEnv(["PATH+OC=${ocDir}/bin"]) {
		                openshift.withCluster('OpenShiftPuzzleProduction', 'openshiftv3_prod_token_client_plugin' ) {
						    openshift.withProject(${params.build_project}) {
						        echo "Tagging dev, Project: ${openshift.project()}"
						        openshift.tag("marina-build/marina-backend:latest", "marina-build/marina-backend:dev")
						        
						    }
						    openshift.withProject(${params.dev_project}) {
						        echo "Deploying to dev, Project: ${openshift.project()}"
						        // TODO: remove image change trigger and trigger deployment here
						        
						    }
						}
					}
				}
            }
        }
        stage('Integration Tests') {
            steps {
                echo "Executing integration tests"
            }
        }
        stage('Deploy to Test') {
            steps {
                script{
                	def ocDir = tool "oc"
                	withEnv(["PATH+OC=${ocDir}/bin"]) {
		                openshift.withCluster('OpenShiftPuzzleProduction', 'openshiftv3_prod_token_client_plugin' ) {
						    openshift.withProject(${params.build_project}) {
						        echo "Tagging dev, Project: ${openshift.project()}"
						        openshift.tag("marina-build/marina-backend:dev", "marina-build/marina-backend:test")
						        
						    }
						    openshift.withProject(${params.dev_project}) {
						        echo "Deploying to dev, Project: ${openshift.project()}"
						        // TODO: remove image change trigger and trigger deployment here
						        
						    }
						}
					}
				}
            }
        }
        stage('Integration Tests') {
            steps {
                echo "Executing integration tests"
            }
        }
        stage('Deploy to Prod') {
            steps {
                script{
                	def ocDir = tool "oc"
                	withEnv(["PATH+OC=${ocDir}/bin"]) {
		                openshift.withCluster('OpenShiftPuzzleProduction', 'openshiftv3_prod_token_client_plugin' ) {
						    openshift.withProject(${params.build_project}) {
						        echo "Tagging dev, Project: ${openshift.project()}"
						        openshift.tag("marina-build/marina-backend:test", "marina-build/marina-backend:prod")
						        
						    }
						    openshift.withProject(${params.dev_project}) {
						        echo "Deploying to dev, Project: ${openshift.project()}"
						        // TODO: remove image change trigger and trigger deployment here
						        
						    }
						}
					}
				}
            }
        }
    }
    post {
        success {
            echo 'Success'
        }
        unstable {
            echo 'Unstable'
        }
        failure {
            echo 'Error'
        }
    }
}