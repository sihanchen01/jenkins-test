pipeline {
  agent any

  stages {
    stage('Check Azure CLI') {
      steps {
        sh 'az version'
      }
    }

    stage('Run Command on VM') {
      steps {
        sh '''
          az vm run-command invoke \
            --resource-group "my-resource-group" \
            --name "my-vm" \
            --command-id RunShellScript \
            --scripts "echo Hello from Jenkins"
        '''
      }
    }
  }
}
