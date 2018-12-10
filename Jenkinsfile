pipeline {
    agent { label 'buildnode' }
    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
        timeout(time: 30, unit: 'MINUTES')
    }
    triggers {
        pollSCM('H/5 * * * *')
    }
    parameters {
        string(name: 'build_project', defaultValue: 'pitc-marina-build', description: 'The OpenShift Build Project')
        string(name: 'dev_project', defaultValue: 'pitc-marina-dev', description: 'The OpenShift Dev Project')
        string(name: 'test_project', defaultValue: 'pitc-marina-test', description: 'The OpenShift Test Project')
        string(name: 'prod_project', defaultValue: 'pitc-marina-prod', description: 'The OpenShift Prod Project')
        
        string(name: 'openshift_cluster', defaultValue: 'OpenShiftPuzzleProduction', description: 'The OpenShift Cluster')
        string(name: 'openshift_cluster_token', defaultValue: 'openshiftv3_prod_token_client_plugin', description: 'The OpenShift Cluster')
    }
    stages {
        stage('Build') {
            steps {
                sh './gradlew build sonarqube'
            }
        }
        stage('Build Docker Image') {
            steps {
                script{
                	def ocDir = tool "oc"
                	withEnv(["PATH+OC=${ocDir}/bin"]) {
		                openshift.withCluster("${params.openshift_cluster}", "${params.openshift_cluster_token}" ) {
						    openshift.withProject("${params.build_project}") {
						        echo "Running in project: ${openshift.project()}"
						        // get current commit and use it as build input
						        def shortCommit = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()
						        def gitRepo = sh(returnStdout: true, script: 'git config remote.origin.url').trim()
						        def buildSelector = openshift.startBuild("marina-backend","--commit="+shortCommit, 
						        "-e GIT_REPO_URL="+gitRepo, "-e GIT_COMMIT="+shortCommit, "-e BUILD_NUMBER=${env.BUILD_NUMBER}",
						        "-e JOB_NAME=${env.JOB_NAME}", "-e BRANCH_NAME=${env.BRANCH_NAME}", "-e BUILD_URL=${env.BUILD_URL}",
						        "--follow=true")
								timeout(10) {
                                    buildSelector.untilEach(1) {
                                        return (it.object().status.phase == "Complete")
                                    }
                                }
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
		                openshift.withCluster("${params.openshift_cluster}", "${params.openshift_cluster_token}" ) {
						    openshift.withProject("${params.build_project}") {
						        echo "Tagging dev, Project: ${openshift.project()}"
						        def tagSelector = openshift.tag("${params.build_project}/marina-backend:latest", "${params.build_project}/marina-backend:dev")
						        
						    }
						    openshift.withProject("${params.dev_project}") {
						        echo "Deploying to dev, Project: ${openshift.project()}"
						        def deploySelector = openshift.selector("dc/marina-backend").rollout().latest()
						        
								def latestDeploymentVersion = openshift.selector('dc',"marina-backend").object().status.latestVersion
								def rc = openshift.selector('rc', "marina-backend-${latestDeploymentVersion}")
								rc.untilEach(1){
								     def rcMap = it.object()
								     return (rcMap.status.replicas.equals(rcMap.status.readyReplicas))
								}						        
						    }
						}
					}
				}
            }
        }
        stage('Integration Tests Dev') {
            steps {
                echo "Executing integration tests"
            }
        }
        stage('Deploy to Test') {
            steps {
                script{
                	def ocDir = tool "oc"
                	withEnv(["PATH+OC=${ocDir}/bin"]) {
		                openshift.withCluster("${params.openshift_cluster}", "${params.openshift_cluster_token}" ) {
						    openshift.withProject("${params.build_project}") {
						        echo "Tagging test, Project: ${openshift.project()}"
						        def tagSelector = openshift.tag("${params.build_project}/marina-backend:dev", "pitc-marina-build/marina-backend:test")
						        
						    }
						    openshift.withProject("${params.test_project}") {
						        echo "Deploying to test, Project: ${openshift.project()}"
						        def deploySelector = openshift.selector("dc/marina-backend").rollout().latest()
						        def latestDeploymentVersion = openshift.selector('dc',"marina-backend").object().status.latestVersion
								def rc = openshift.selector('rc', "marina-backend-${latestDeploymentVersion}")
								rc.untilEach(1){
								     def rcMap = it.object()
								     return (rcMap.status.replicas.equals(rcMap.status.readyReplicas))
								}
						    }
						}
					}
				}
            }
        }
        stage('Integration Tests Test') {
            steps {
                echo "Executing integration tests"
            }
        }
        stage('Deploy to Prod') {
        	when {
                // Only on master branch
                expression { env.BRANCH_NAME == 'master' }
            }
            steps {
                script{
                	def ocDir = tool "oc"
                	withEnv(["PATH+OC=${ocDir}/bin"]) {
		                openshift.withCluster("${params.openshift_cluster}", "${params.openshift_cluster_token}" ) {
						    openshift.withProject("${params.build_project}") {
						        echo "Tagging prod, Project: ${openshift.project()}"
						        def tagSelector = openshift.tag("${params.build_project}/marina-backend:test", "pitc-marina-build/marina-backend:prod")
						        
						    }
						    openshift.withProject("${params.prod_project}") {
						        echo "Deploying to prod, Project: ${openshift.project()}"
						        def deploySelector = openshift.selector("dc/marina-backend").rollout().latest()
						        def latestDeploymentVersion = openshift.selector('dc',"marina-backend").object().status.latestVersion
								def rc = openshift.selector('rc', "marina-backend-${latestDeploymentVersion}")
								rc.untilEach(1){
								     def rcMap = it.object()
								     return (rcMap.status.replicas.equals(rcMap.status.readyReplicas))
								}
						    }
						}
					}
				}
            }
        }
    }
    post {
        success {
        	rocketSend avatar: 'https://chat.puzzle.ch/emoji-custom/success.png', channel: 'pitc-marina', message: "Deployment success - Branch ${env.BRANCH_NAME} - ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)", rawMessage: true
        }
        unstable {
            rocketSend avatar: 'https://chat.puzzle.ch/emoji-custom/unstable.png', channel: 'pitc-marina', message: "Deployment unstable - Branch ${env.BRANCH_NAME} - ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)", rawMessage: true
        }
        failure {
            rocketSend avatar: 'https://chat.puzzle.ch/emoji-custom/failure.png', channel: 'pitc-marina', message: "Deployment failure - Branch ${env.BRANCH_NAME} - ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)", rawMessage: true
        }
    }
}