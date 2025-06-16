pipeline {
  agent any

  environment {
    RESOURCE_GROUP = 'my-resource-group'
    VM_NAME        = 'my-vm'
  }
  stages {

    stage('Azure Login & Command') {
      steps {
       withCredentials([
          string(credentialsId: 'azure-client-id', variable: 'AZ_CLIENT_ID'),
          string(credentialsId: 'azure-client-secret', variable: 'AZ_CLIENT_SECRET'),
          string(credentialsId: 'azure-tenant-id', variable: 'AZ_TENANT_ID'),
          string(credentialsId: 'azure-subscription-id', variable: 'AZ_SUBSCRIPTION_ID')
        ]) {
          sh '''
           az login --service-principal \
              --username "$AZ_CLIENT_ID" \
              --password "$AZ_CLIENT_SECRET" \
              --tenant   "$AZ_TENANT_ID"

            az account set --subscription "$AZ_SUBSCRIPTION_ID"

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
