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
        withCredentials([file(credentialsId: 'azure-jenkins-sp', variable: 'AZURE_AUTH')]) {
            sh '''

            az login --service-principal \
              --username "$(jq -r .clientId $AZURE_AUTH)" \
              --password "$(jq -r .clientSecret $AZURE_AUTH)" \
              --tenant   "$(jq -r .tenantId $AZURE_AUTH)"

                az account set --subscription "$(jq -r .subscriptionId $AZURE_AUTH)"

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
}
