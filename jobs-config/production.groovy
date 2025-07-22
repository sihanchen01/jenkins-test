pipelineJob('production-pipeline') {
  description('Production deployment pipeline - restricted access')
  
  parameters {
    stringParam('VERSION', 'latest', 'Version to deploy')
    booleanParam('CONFIRM_DEPLOY', false, 'Confirm production deployment')
  }
  
  // Production job permissions - more restrictive
  authorization {
    // Only deployer can build production
    permission('hudson.model.Item.Read:deployer')
    permission('hudson.model.Item.Build:deployer')
    permission('hudson.model.Item.Cancel:deployer')
    permission('hudson.model.Item.Configure:deployer')
    
    // Developers can only read
    permission('hudson.model.Item.Read:developer')
    
    // Testers cannot access production pipeline at all
    // (no permissions granted)
  }
  
  logRotator {
    numToKeep(5)
    daysToKeep(14)
  }
  
  definition {
    cps {
      script('''
        pipeline {
          agent any
          
          stages {
            stage('Validate') {
              steps {
                echo "Deploying version: ${params.VERSION}"
                echo "Deployment confirmed: ${params.CONFIRM_DEPLOY}"
                
                script {
                  if (!params.CONFIRM_DEPLOY) {
                    error("Production deployment must be confirmed!")
                  }
                }
              }
            }
            
            stage('Deploy to Production') {
              steps {
                echo 'Deploying to production environment...'
                sh 'sleep 5'
                echo 'Production deployment completed'
              }
            }
          }
          
          post {
            success {
              echo 'Production deployment successful!'
            }
            failure {
              echo 'Production deployment failed!'
            }
          }
        }
      ''')
      sandbox()
    }
  }
}