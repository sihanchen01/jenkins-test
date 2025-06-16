pipeline {
  agent any

  environment {
    RESOURCE_GROUP = 'my-resource-group'
    VM_NAME        = 'my-vm'
  }
  stages {
    stage('Install jq') {
      steps {
        sh 'apt-get update && apt-get install -y jq'
      }
    }

    stage('Azure Login & Command') {
      steps {
        withCredentials([file(credentialsId: 'azure-jenkins-sp', variable: 'AZURE_AUTH')]) {
          sh '''
            az login --service-principal \
              --username "$(jq -r .clientId $AZURE_AUTH)" \
              --password "$(jq -r .clientSecret $AZURE_AUTH)" \
              --tenant   "$(jq -r .tenantId $AZURE_AUTH)"

            az account set --subscription "$(jq -r .subscriptionId $AZURE_AUTH)"

            az vm run-command invoke \
              --resource-group "$RESOURCE_GROUP" \
              --name "$VM_NAME" \
              --command-id RunShellScript \
              --scripts "echo Hello from Jenkins"
          '''
        }
      }
    }
  }
}
