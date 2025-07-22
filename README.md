# Jenkins Local Testing Environment

A complete Jenkins setup using Docker Compose with Configuration as Code (JCasC), role-based permissions, and multiple pipeline examples.

## 🚀 Quick Start

### Prerequisites
- Docker and Docker Compose installed
- Zscaler Root CA certificate (if behind corporate proxy)

### 1. Start Jenkins
```bash
# Build and start Jenkins
docker-compose up -d

# Check status
docker-compose ps
```

### 2. Access Jenkins
- **URL**: http://localhost:8080
- **Admin**: `admin` / `admin123`
- **Developer**: `developer` / `dev123`
- **Tester**: `tester` / `test123`
- **Deployer**: `deployer` / `deploy123`

### 3. Available Pipelines
- **test-pipeline**: Multi-stage CI/CD demo pipeline
- **production-pipeline**: Restricted production deployment
- **lqb-test/lqb-{env}-{action}**: Liquibase database jobs (6 jobs total)

## 📁 Project Structure

```
jenkins-test/
├── jenkins_home/              # Persistent Jenkins data (auto-created)
├── jenkins-config.yaml        # Main JCasC configuration
├── jobs-config/               # Job DSL definitions
│   ├── test.groovy           # Test pipeline job
│   ├── production.groovy     # Production pipeline job
│   └── seed.lqb.groovy       # Liquibase jobs generator
├── pipelines/                 # Pipeline scripts
│   └── Jenkinsfile.test      # Test pipeline implementation
├── plugins.txt               # Jenkins plugins to install
├── docker-compose.yml        # Container orchestration
├── Dockerfile               # Custom Jenkins image
└── README.md                # This file
```

## 🔐 User Permissions

### Global Permissions
- **admin**: Full system administration
- **developer**: Read access + job building
- **tester**: Read-only access
- **deployer**: Read access + deployment rights

### Job-Level Permissions
- **test-pipeline**: Developer can build, Tester read-only
- **production-pipeline**: Only Deployer can build
- **lqb jobs**: Environment-based restrictions (prod = deployer only)

## 🛠 Configuration Management

### Jenkins Configuration as Code (JCasC)
- **File**: `jenkins-config.yaml`
- **Auto-applied**: Changes take effect on restart
- **Includes**: Users, permissions, plugins, system settings

### Job DSL
- **Directory**: `jobs-config/`
- **Auto-loaded**: Jobs created automatically via JCasC
- **Dynamic**: Add new `.groovy` files and restart

### Pipeline Scripts
- **Directory**: `pipelines/`
- **Usage**: Referenced by Job DSL or inline in jobs
- **Live editing**: Mounted as volume for development

## 📊 Features

### ✅ Included
- **Role-based access control** with project matrix
- **Blue Ocean** modern UI
- **Multiple pipeline examples** with different complexity
- **Persistent storage** survives container restarts
- **Timezone configured** for Asia/Singapore
- **Dark theme** support
- **Workspace cleanup** automation
- **JUnit test reporting**

### 🔧 Jenkins Plugins Installed
- Configuration as Code
- Job DSL
- Blue Ocean
- Pipeline plugins
- Git integration
- JUnit reporting
- Workspace cleanup
- Dark theme

## 🐳 Container Management

### Start/Stop
```bash
# Start services
docker-compose up -d

# Stop services
docker-compose down

# Restart Jenkins only
docker-compose restart jenkins-server

# View logs
docker-compose logs jenkins-server -f
```

### Rebuild (after plugin/config changes)
```bash
# Rebuild Jenkins image
docker-compose build jenkins-server

# Restart with new image
docker-compose up -d jenkins-server
```

## 🔌 Jenkins Agent Setup

### Built-in Node
- **Executors**: 0 (disabled)
- **Mode**: EXCLUSIVE (coordination only)
- **Result**: All jobs must run on agents

### Docker Agent (Optional)
1. **Create node** in Jenkins UI: Manage Jenkins → Nodes → New Node
2. **Name**: `docker-agent`
3. **Type**: Permanent Agent
4. **Remote directory**: `/home/jenkins/agent`
5. **Launch method**: Launch agent by connecting it to controller
6. **Copy secret** from agent page
7. **Update docker-compose.yml**:
   ```yaml
   environment:
     - JENKINS_SECRET=<paste-secret-here>
   ```
8. **Start agent**: `docker-compose up jenkins-agent`

## 📝 Adding New Jobs

### Method 1: Job DSL (Recommended)
1. Create new `.groovy` file in `jobs-config/`
2. Add to `jenkins-config.yaml` jobs section:
   ```yaml
   - file: /var/jenkins_home/casc_configs/jobs-config/your-job.groovy
   ```
3. Restart Jenkins: `docker-compose restart jenkins-server`

### Method 2: Pipeline Script
1. Create `Jenkinsfile.*` in `pipelines/`
2. Reference in Job DSL with `scriptPath`

## 🔍 Troubleshooting

### Jenkins Won't Start
```bash
# Check logs for errors
docker-compose logs jenkins-server

# Look for JCasC errors
docker-compose logs jenkins-server | grep -i "casc\|severe\|error"
```

### Permission Issues
```bash
# Fix jenkins_home ownership (if needed)
sudo chown -R 1000:1000 ./jenkins_home
```

### Agent Connection Issues
```bash
# Check agent logs
docker-compose logs jenkins-agent

# Verify agent secret in docker-compose.yml
# Ensure agent node exists in Jenkins UI
```

### Job DSL Syntax Errors
- Use Job DSL API documentation: https://jenkinsci.github.io/job-dsl-plugin/
- Check Job DSL playground in Jenkins: Manage Jenkins → Script Console

## 🔄 Data Persistence

### What's Persistent
- ✅ **All job configurations** and build history
- ✅ **User settings** and preferences  
- ✅ **Plugin data** and caches
- ✅ **Credentials** and secrets
- ✅ **System configuration** changes

### What's Code-Managed
- ✅ **Users and permissions** (jenkins-config.yaml)
- ✅ **Jobs and pipelines** (jobs-config/*.groovy)
- ✅ **Plugin list** (plugins.txt)
- ✅ **System settings** (jenkins-config.yaml)

### Backup Strategy
```bash
# Backup Jenkins data
tar -czf jenkins-backup-$(date +%Y%m%d).tar.gz jenkins_home/

# Backup configuration (version controlled)
git add . && git commit -m "Jenkins config update"
```

## 🌐 Network Configuration

### Corporate Proxy (if needed)
Update `docker-compose.yml` environment:
```yaml
environment:
  - http_proxy=http://proxy.company.com:8080
  - https_proxy=http://proxy.company.com:8080
  - no_proxy=localhost,127.0.0.1
```

### Port Configuration
- **Jenkins UI**: 8080 → 8080
- **Agent JNLP**: 50000 → 50000

## 📚 Additional Resources

- [Jenkins Configuration as Code](https://www.jenkins.io/projects/jcasc/)
- [Job DSL Plugin](https://plugins.jenkins.io/job-dsl/)
- [Pipeline Syntax](https://www.jenkins.io/doc/book/pipeline/syntax/)
- [Blue Ocean Documentation](https://www.jenkins.io/doc/book/blueocean/)

## 🤝 Development

### Adding New Features
1. Update relevant configuration files
2. Test in local environment
3. Commit changes with descriptive messages
4. Document changes in README

### Configuration Files Priority
1. **Jenkins-config.yaml**: System-level settings
2. **Job DSL files**: Job definitions and permissions
3. **Jenkinsfiles**: Pipeline implementation
4. **plugins.txt**: Required plugins

---

🎯 **Ready to start?** Run `docker-compose up -d` and visit http://localhost:8080!