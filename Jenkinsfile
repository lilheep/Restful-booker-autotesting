pipeline {
  agent {
    kubernetes {
      yaml '''
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: maven
    image: maven:3-eclipse-temurin-25
    command: ["sleep"]
    args: ["infinity"]
'''
    }
  }

  options {
    timeout(time: 20, unit: 'MINUTES')
  }

  environment {
    TELEGRAM_CHAT_ID = '786258626'
    BOT_TOKEN = credentials('BOT_TOKEN')
  }

  stages {
    stage('Run Tests') {
      steps {
        container('maven') {
          withCredentials([file(credentialsId: 'local-properties', variable: 'LOCAL_PROPS')]) {
            sh '''
              cp "$LOCAL_PROPS" local.properties
              mvn -B clean test -Dmaven.test.failure.ignore=true
            '''
          }
        }
      }
    }
  }

  post {
    always {
      script {
        container('maven') {
          sh 'chmod -R a+rwX target || true'
        }

        def r = junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'

        try {
          allure results: [[path: 'target/allure-results']]
        } catch (e) {
          echo "Allure failed: ${e.message}"
        }

        def status = currentBuild.currentResult
        def icon = status == 'SUCCESS' ? '✅' : (status == 'UNSTABLE' ? '⚠️' : '❌')
        def message = """${icon} ${env.JOB_NAME} #${env.BUILD_NUMBER}: ${status}

PASSED: ${r.passCount}
FAILED: ${r.failCount}
SKIPPED: ${r.skipCount}
TOTAL: ${r.totalCount}

Allure: ${env.BUILD_URL}allure/"""

        withEnv(["TG_TEXT=${message}"]) {
          container('maven') {
            sh '''
              curl -s -X POST "https://api.telegram.org/bot$BOT_TOKEN/sendMessage" \
                -d chat_id="$TELEGRAM_CHAT_ID" \
                --data-urlencode "text=$TG_TEXT"
            '''
          }
        }
      }
    }
  }
}