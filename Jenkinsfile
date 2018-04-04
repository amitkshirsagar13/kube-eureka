def label = "worker-${UUID.randomUUID().toString()}"


podTemplate(label: label, containers: [
  containerTemplate(name: 'maven', image: 'maven:3.3.9-jdk-8-alpine', ttyEnabled: true, command: 'cat'),
  containerTemplate(name: 'docker', image: 'docker', command: 'cat', ttyEnabled: true), 
  containerTemplate(name: 'kubectl', image: 'lachlanevenson/k8s-kubectl:v1.8.8', command: 'cat', ttyEnabled: true),
  containerTemplate(name: 'helm', image: 'lachlanevenson/k8s-helm:latest', command: 'cat', ttyEnabled: true)

],
volumes: [
  hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock'),
  persistentVolumeClaim(mountPath: '/home/jenkins/.mvnrepo', claimName: 'jenkins-mvn-local-repo', readOnly: false),
  secretVolume(mountPath: '/home/jenkins/.m2/', secretName: 'jenkins-maven-settings')
]) {
  node(label) {
	
	def profile = "dev"
    def myRepo = checkout scm
    def gitCommit = myRepo.GIT_COMMIT
    def gitBranch = myRepo.GIT_BRANCH
    def shortGitCommit = "${gitCommit[0..10]}"
    //def mvnTool = tool 'maven'
    def project = "basekube"
    def application = "kube-eureka"
    def dockerApp
    stage('Build Project') {
       echo "Building Project...$gitBranch:$shortGitCommit"
       container('maven') {
	        stage('Build a Maven project') {
		   withMaven(mavenLocalRepo: ".repository") {
	             sh "mvn -Dmaven.test.skip=true clean install"
		   }
	        }
	    }
       
    }    
    stage('Create Docker images and Push') {
       echo "Project: $project | Application: $application | tag: $shortGitCommit"
      container('docker') {
        withCredentials([[$class: 'UsernamePasswordMultiBinding',
          credentialsId: 'docker-hub-credentials',
          usernameVariable: 'DOCKER_HUB_USER',
          passwordVariable: 'DOCKER_HUB_PASSWORD']]) {
          sh """
            docker login -u ${DOCKER_HUB_USER} -p ${DOCKER_HUB_PASSWORD}
            docker build -t amitkshirsagar13/$application:$shortGitCommit -t amitkshirsagar13/$application:latest .
            docker push amitkshirsagar13/$application:$shortGitCommit
            docker push amitkshirsagar13/$application:latest
            """
        }
      }
    }
    stage('Deploy helm release') {
      container('helm') {
		sh "helm upgrade --install $application --namespace $gitBranch ./cicd/$application/ --set profile=$profile --set branch=$gitBranch --set commit=$shortGitCommit --set application=$application"
      }
    }
  }
}
