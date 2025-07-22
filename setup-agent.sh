#!/bin/bash
# Script to set up Jenkins agent connection

echo "🔧 Jenkins Agent Setup Script"
echo "=============================="

# Step 1: Check if Jenkins is running
echo "1. Checking Jenkins status..."
if ! curl -s http://localhost:8080 > /dev/null; then
    echo "❌ Jenkins is not running. Please start Jenkins first with: docker-compose up jenkins-server"
    exit 1
fi
echo "✅ Jenkins is running"

# Step 2: Instructions for manual setup
echo ""
echo "2. Manual Setup Instructions:"
echo "   👤 Login to Jenkins: http://localhost:8080"
echo "   📝 Credentials: admin/admin123"
echo ""
echo "3. Create Agent Node:"
echo "   🔗 Go to: Manage Jenkins → Nodes"
echo "   ➕ Click: New Node"
echo "   📋 Name: docker-agent"
echo "   ☑️ Type: Permanent Agent"
echo ""
echo "4. Configure Agent:"
echo "   📁 Remote root directory: /home/jenkins/agent"
echo "   👤 Launch method: Launch agent by connecting it to the controller"
echo "   💾 Click Save"
echo ""
echo "5. Get Connection Info:"
echo "   📋 Copy the secret from the agent page"
echo "   📝 Note the connection command"
echo ""
echo "6. Update docker-compose.yml:"
echo "   🔧 Set JENKINS_SECRET environment variable with the secret"
echo "   🔄 Restart agent: docker-compose up jenkins-agent"
echo ""
echo "📚 For automation, you can also use Jenkins CLI or REST API"
echo "   Example: jenkins-cli.jar -s http://localhost:8080 create-node docker-agent"
echo ""
echo "🎯 Alternative: Run jobs on built-in executor (simpler setup)"