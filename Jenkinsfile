def project_url= 'https://github.com.cnpmjs.org/hrers/e2buy.git'
def github_auth= 'hrers'
node {
    def mvnHome
    stage('拉取代码') { // for display purposes
      checkout([$class: 'GitSCM', branches: [[name: "${branch}"]], extensions: [],
       userRemoteConfigs: [[credentialsId: "${github_auth}", url: "${project_url}"]]])
    }
    stage('构建代码') {
       sh "mvn -f e2buy-common clean install"
       sh "mvn -f ${project_name} clean package"
    }
    stage('发布代码') {
       echo 'deploy code'
    }
}
