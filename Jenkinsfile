def project_url= 'https://github.com.cnpmjs.org/hrers/e2buy.git'
def github_auth= 'hrers'
def github_branch='test_jenkins'
node {
    def mvnHome
    stage('拉取代码') { // for display purposes
      checkout([$class: 'GitSCM', branches: [[name: "${github_branch}"]], extensions: [],
       userRemoteConfigs: [[credentialsId: "${github_auth}", url: "${project_url}"]]])
    }
    stage('构建代码') {
       echo 'build code'
    }
    stage('发布代码') {
       echo 'deploy code'
    }
}
