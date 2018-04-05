def label = "worker-${UUID.randomUUID().toString()}"


podTemplate(label: label, containers: [
  containerTemplate(name: 'docker', image: 'docker', command: 'cat', ttyEnabled: true), 
  containerTemplate(name: 'kubectl', image: 'lachlanevenson/k8s-kubectl:v1.8.8', command: 'cat', ttyEnabled: true),
  containerTemplate(name: 'helm', image: 'lachlanevenson/k8s-helm:latest', command: 'cat', ttyEnabled: true)

],
volumes: [
  hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock')
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
    def mvnContainer = docker.image('maven')
    mvnContainer.inside('-v /home:/root/.m2') {
      // Set up a shared Maven repo so we don't need to download all dependencies on every build.
      writeFile file: 'settings.xml',
         text: '<settings><localRepository>/m2repo</localRepository></settings>'
      
      // Build with maven settings.xml file that specs the local Maven repo.
      sh 'mvn -B -s settings.xml clean install'
   }
	  
    stage('Build Project') {
       echo "Building Project...$gitBranch:$shortGitCommit"
       container('maven') {
	 stage('Build a Maven project') {
	   sh "mvn -Dmaven.test.skip=true clean install"
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
