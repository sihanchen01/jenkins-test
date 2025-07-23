pipelineJob('test-pipeline') {
  description('''
    Comprehensive test pipeline demonstrating Jenkins CI/CD capabilities:
    
    Features:
    • Multi-stage pipeline: Checkout → Build → Test → Quality → Security → Package → Deploy
    • Parameterized builds: Environment selection, test toggle, build configuration
    • Parallel testing: Unit tests and integration tests run simultaneously
    • Artifact management: Builds and packages are archived automatically
    • Test reporting: JUnit test results published and displayed
    • Build retention: Keeps 10 builds for 30 days with automatic cleanup
    • Scheduled execution: Runs daily at 2 AM via cron trigger
    • Conditional deployment: Staging deployment based on environment parameter
    
    Parameters:
    • ENVIRONMENT: Target environment (staging/production) - affects deployment behavior
    • RUN_TESTS: Toggle to enable/disable test execution for faster builds
    • BUILD_TYPE: Configuration type (debug/release) - affects build optimization
    
    Usage Examples:
    • Development testing: RUN_TESTS=true, BUILD_TYPE=debug, ENVIRONMENT=staging
    • Production builds: RUN_TESTS=true, BUILD_TYPE=release, ENVIRONMENT=production
    • Quick iteration: RUN_TESTS=false, BUILD_TYPE=debug (skips test stages)
    
    This pipeline serves as a template for real projects and demonstrates Jenkins best practices
    including proper error handling, workspace cleanup, and comprehensive logging.
  ''')
  
  parameters {
    stringParam('ENVIRONMENT', 'staging', 'Target environment for deployment')
    booleanParam('RUN_TESTS', true, 'Whether to run tests')
    choiceParam('BUILD_TYPE', ['debug', 'release'], 'Build configuration')
  }
  
  triggers {
    cron('H H * * 0') // Runs once a week on Sunday
  }
  
  logRotator {
    numToKeep(10)
    daysToKeep(30)
  }
  
  // Job-specific permissions
  authorization {
    permission('hudson.model.Item.Read:developer')
    permission('hudson.model.Item.Build:developer')
    permission('hudson.model.Item.Cancel:developer')
    permission('hudson.model.Item.Workspace:developer')
    
    // Tester can only read and view results
    permission('hudson.model.Item.Read:tester')
    permission('hudson.model.Item.Workspace:tester')
    
    // Deployer can build and deploy
    permission('hudson.model.Item.Read:deployer')
    permission('hudson.model.Item.Build:deployer')
    permission('hudson.model.Item.Cancel:deployer')
    permission('hudson.model.Item.Configure:deployer')
  }
  
  definition {
    cps {
      script('''
        pipeline {
          agent { label 'docker-agent' }
          
          environment {
            APP_NAME = 'test-app'
            BUILD_NUMBER = "${env.BUILD_NUMBER}"
            TIMESTAMP = sh(script: "date +%Y%m%d-%H%M%S", returnStdout: true).trim()
          }
          
          stages {
            stage('Checkout') {
              steps {
                echo "Starting build for ${env.APP_NAME} #${env.BUILD_NUMBER}"
                echo "Environment: ${params.ENVIRONMENT}"
                echo "Build Type: ${params.BUILD_TYPE}"
                
                // Simulate checkout
                sh 'echo "Checking out source code..."'
                sh 'sleep 2'
              }
            }
            
            stage('Build') {
              steps {
                echo 'Building application...'
                sh """
                  echo "Building ${env.APP_NAME} in ${params.BUILD_TYPE} mode"
                  mkdir -p build
                  echo "Build completed at ${env.TIMESTAMP}" > build/info.txt
                  sleep 3
                """
              }
              post {
                always {
                  archiveArtifacts artifacts: 'build/**', allowEmptyArchive: true
                }
              }
            }
            
            stage('Test') {
              when {
                expression { params.RUN_TESTS == true }
              }
              parallel {
                stage('Unit Tests') {
                  steps {
                    echo 'Running unit tests...'
                    sh """
                      echo "Running unit tests for ${env.APP_NAME}"
                      mkdir -p test-results
                      echo "<?xml version='1.0'?><testsuite tests='5' failures='0' errors='0'></testsuite>" > test-results/unit-tests.xml
                      sleep 2
                    """
                  }
                  post {
                    always {
                      junit testResults: 'test-results/*.xml', allowEmptyResults: true
                    }
                  }
                }
                stage('Integration Tests') {
                  steps {
                    echo 'Running integration tests...'
                    sh """
                      echo "Running integration tests for ${params.ENVIRONMENT}"
                      sleep 3
                      echo "Integration tests passed"
                    """
                  }
                }
              }
            }
            
            stage('Code Quality') {
              steps {
                echo 'Analyzing code quality...'
                sh """
                  echo "Running static code analysis"
                  mkdir -p reports
                  echo "Code quality check completed" > reports/quality.txt
                  sleep 1
                """
              }
            }
            
            stage('Security Scan') {
              steps {
                echo 'Running security scan...'
                sh """
                  echo "Scanning for security vulnerabilities"
                  echo "Security scan completed - no issues found"
                  sleep 2
                """
              }
            }
            
            stage('Package') {
              steps {
                echo 'Packaging application...'
                sh """
                  echo "Creating package for ${env.APP_NAME}"
                  mkdir -p packages
                  tar -czf packages/${env.APP_NAME}-${env.BUILD_NUMBER}.tar.gz build/
                  echo "Package created successfully"
                """
              }
              post {
                success {
                  archiveArtifacts artifacts: 'packages/*.tar.gz'
                }
              }
            }
            
            stage('Deploy to Staging') {
              when {
                expression { params.ENVIRONMENT == 'staging' }
              }
              steps {
                echo "Deploying to ${params.ENVIRONMENT}..."
                sh """
                  echo "Deploying ${env.APP_NAME} to ${params.ENVIRONMENT}"
                  echo "Deployment completed successfully"
                  sleep 2
                """
              }
            }
          }
          
          post {
            always {
              echo 'Pipeline execution completed'
              sh 'echo "Cleaning up temporary files..."'
            }
            success {
              echo 'Pipeline succeeded!'
              // In a real scenario, you might send notifications here
              sh 'echo "Build #${BUILD_NUMBER} completed successfully"'
            }
            failure {
              echo 'Pipeline failed!'
              // In a real scenario, you might send failure notifications
              sh 'echo "Build #${BUILD_NUMBER} failed - check logs for details"'
            }
            unstable {
              echo 'Pipeline is unstable'
            }
            cleanup {
              deleteDir() // Clean up workspace
            }
          }
        }
      ''')
      sandbox()
    }
  }
}