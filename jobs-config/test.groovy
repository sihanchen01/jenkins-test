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
    cron('H 2 * * *') // Run daily at 2 AM
  }
  
  logRotator {
    numToKeep(10)
    daysToKeep(30)
  }
  
  definition {
    cpsScm {
      scm {
        git {
          remote {
            url('.')
          }
          branches('*/master')
        }
      }
      scriptPath('pipelines/Jenkinsfile.test')
    }
  }
}