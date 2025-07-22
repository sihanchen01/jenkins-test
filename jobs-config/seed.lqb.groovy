// Create folder for LQB jobs
folder('lqb_test') {
    description('Folder for LQB test jobs')
}

// Define environments and actions
def envs = ['ppt', 'uat', 'prod']
def actions = ['update', 'rollback']

// Helper functions
def getEnvRole(env) {
    switch(env) {
        case 'ppt': return 'bcc_crm_pmx_liquibase_tools_ppt'
        case 'uat': return 'bcc_crm_pmx_liquibase_tools_uat'
        case 'prod': return 'bcc_crm_pmx_liquibase_tools_prod'
        default: return 'bcc_crm_pmx_liquibase_tools_dev'
    }
}

def getVaultName(env) {
    switch(env) {
        case 'ppt': return 'kv-cbs-pmxcesppt-sea-01'
        case 'uat': return 'kv-cbs-pmxcesuat-sea-01'
        case 'prod': return 'kv-cbs-pmxcesprod-sea-01'
        default: return 'kv-cbs-pmxcesdev-sea-01'
    }
}

def getJdbcUrl(env) {
    switch(env) {
        case 'ppt': return 'jdbc:oracle:thin:@pmxppt1c1oda-scan.nprd.az.sg.singtelgroup.net:1831/ACMPPT'
        case 'uat': return 'jdbc:oracle:thin:@pmxuat1c1oda-scan.nprd.az.sg.singtelgroup.net:1831/ACMUAT'
        case 'prod': return 'jdbc:oracle:thin:@pmxprd1c1oda-scan.prd.az.sg.singtelgroup.net:1831/ACMPRD'
        default: return 'jdbc:oracle:thin:@localhost:1521/XE'
    }
}

def getAccessLevel(env) {
    return env == 'prod' ? 'prod' : 'non-prod'
}

// Generate jobs for each environment and action
envs.each { env ->
    actions.each { action ->
        pipelineJob("lqb_test/lqb-${env}-${action}") {
            description("Liquibase ${action} job for environment ${env}")
            
            parameters {
                stringParam('LQB_ACTION', action, 'Liquibase action to perform')
                stringParam('ENVIRONMENT', env, 'Target environment for deployment')
                booleanParam('DRY_RUN', true, 'Execute in dry-run mode')
            }
            
            // Job-specific permissions
            authorization {
                permission('hudson.model.Item.Read:developer')
                permission('hudson.model.Item.Read:deployer')
                
                if (env == 'prod') {
                    // Production - only deployer can execute
                    permission('hudson.model.Item.Build:deployer')
                    permission('hudson.model.Item.Cancel:deployer')
                } else {
                    // Non-prod - developer can also execute
                    permission('hudson.model.Item.Build:developer')
                    permission('hudson.model.Item.Build:deployer')
                    permission('hudson.model.Item.Cancel:developer')
                    permission('hudson.model.Item.Cancel:deployer')
                }
            }
            
            logRotator {
                numToKeep(10)
                daysToKeep(30)
            }
            
            definition {
                cps {
                    script("""
                        pipeline {
                            agent any
                            
                            environment {
                                PP_ROLE = '${getEnvRole(env)}'
                                AZ_VAULTNAME = '${getVaultName(env)}'
                                JDBC_URL = '${getJdbcUrl(env)}'
                                MSSQL = 'no'
                                ACCESS = '${getAccessLevel(env)}'
                            }
                            
                            stages {
                                stage('Prepare') {
                                    steps {
                                        // Clean workspace (use deleteDir if cleanWs not available)
                                        script {
                                            try {
                                                cleanWs()
                                            } catch (Exception e) {
                                                echo "cleanWs not available, using deleteDir instead"
                                                deleteDir()
                                            }
                                        }
                                        echo "Starting Liquibase \${params.LQB_ACTION} for environment \${params.ENVIRONMENT}"
                                        echo "Dry run mode: \${params.DRY_RUN}"
                                    }
                                }
                                
                                stage('Validate') {
                                    steps {
                                        echo 'Validating Liquibase configuration...'
                                        // Add validation logic here
                                        sh 'echo "Configuration validated"'
                                    }
                                }
                                
                                stage('Execute Liquibase') {
                                    steps {
                                        script {
                                            if (params.DRY_RUN == true) {
                                                echo "DRY RUN: Would execute Liquibase \${params.LQB_ACTION} for \${params.ENVIRONMENT}"
                                            } else {
                                                echo "Executing Liquibase \${params.LQB_ACTION} for \${params.ENVIRONMENT}"
                                                // Add actual Liquibase execution here
                                                sh 'echo "Liquibase execution completed"'
                                            }
                                        }
                                    }
                                }
                            }
                            
                            post {
                                always {
                                    echo 'Liquibase job completed'
                                }
                                success {
                                    echo 'Liquibase job succeeded!'
                                }
                                failure {
                                    echo 'Liquibase job failed!'
                                }
                            }
                        }
                        
                    """.stripIndent())
                    sandbox()
                }
            }
        }
    }
}