> any updates I do for test.groovy or jenkins-config yml, do I need to restart jenkins server container?

⏺ For jenkins-config.yaml changes - Yes, restart required. JCasC only loads the configuration on startup.

  For test.groovy and pipelines/Jenkinsfile.test changes - No restart needed! These are loaded dynamically:
  - Job DSL files are re-processed when Jenkins detects changes
  - Pipeline scripts are read from the repository each time the job runs

  Quick reload options:

  1. Configuration reload: Manage Jenkins > Configuration as Code > Reload existing configuration (for yaml changes without restart)
  2. Job updates: Changes to .groovy and Jenkinsfile.* files are picked up automatically

  So for your workflow:
  - Edit pipeline logic → No restart needed
  - Edit job parameters/triggers → No restart needed
  - Edit Jenkins system settings → Restart or use reload configuration

  The volume mounts make the files accessible immediately, so most development can happen without container restarts.