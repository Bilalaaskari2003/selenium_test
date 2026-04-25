pipeline {
    agent {
        docker {
            image 'markhobson/maven-chrome'
            args '-u root:root -v /var/lib/jenkins/.m2:/root/.m2'
        }
    }

    stages {
        stage('Clone Repository') {
            steps {
                // ⚠️ CHANGE THIS URL TO YOUR REPOSITORY ⚠️
                git branch: 'main', url: 'https://github.com/Bilalaaskari2003/selenium_test.git'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn clean test'
            }
        }
        
        stage('Publish Test Results') {
            steps {
                junit '**/target/surefire-reports/*.xml'
            }
        }
    }

    post {
        always {
            script {
                // Get commit author email
                sh "git config --global --add safe.directory ${env.WORKSPACE}"
                
                def committer = sh(
                    script: "git log -1 --pretty=format:'%ae'",
                    returnStdout: true
                ).trim()
                
                echo "Sending email to: ${committer}"

                // Check if test reports exist
                def reportsExist = sh(
                    script: "ls target/surefire-reports/*.xml 2>/dev/null && echo 'yes' || echo 'no'",
                    returnStdout: true
                ).trim()

                def emailBody = ""

                if (reportsExist == 'yes') {
                    def raw = sh(
                        script: "grep -h '<testcase' target/surefire-reports/*.xml 2>/dev/null || echo ''",
                        returnStdout: true
                    ).trim()

                    if (raw.isEmpty()) {
                        emailBody = """
Test Summary (Build #${env.BUILD_NUMBER})

⚠️ No test cases found in the reports.

Please check if your test classes are properly annotated with @Test.
"""
                    } else {
                        int total = 0
                        int passed = 0
                        int failed = 0
                        int skipped = 0
                        def details = ""

                        raw.split('\n').each { line ->
                            total++
                            
                            // Extract test name
                            def nameMatcher = (line =~ /name="([^"]+)"/)
                            def name = nameMatcher ? nameMatcher[0][1] : "unknown"

                            if (line.contains('<failure') || line.contains('failure')) {
                                failed++
                                details += "❌ ${name} — FAILED\n"
                            } else if (line.contains('<skipped') || line.contains('</skipped>') || line.contains('skipped="true"')) {
                                skipped++
                                details += "⏭️ ${name} — SKIPPED\n"
                            } else {
                                passed++
                                details += "✅ ${name} — PASSED\n"
                            }
                        }

                        emailBody = """
===========================================
    TEST RESULTS - Build #${env.BUILD_NUMBER}
===========================================

📊 SUMMARY
-----------
Total Tests:   ${total}
✅ Passed:      ${passed}
❌ Failed:      ${failed}
⏭️ Skipped:     ${skipped}

📋 DETAILED RESULTS
-------------------
${details}

🔗 View Full Report: ${env.BUILD_URL}

===========================================
"""
                    }
                } else {
                    emailBody = """
===========================================
    TEST RESULTS - Build #${env.BUILD_NUMBER}
===========================================

⚠️ ERROR: No test reports found!

Possible issues:
1. Maven test phase did not run correctly
2. No test classes found in src/test/java
3. Test classes don't have @Test annotations
4. Surefire plugin not configured properly in pom.xml

🔗 Check Console Output: ${env.BUILD_URL}console

===========================================
"""
                }

                // Send email
                emailext(
                    to: committer,
                    subject: "Jenkins Build #${env.BUILD_NUMBER} - Test Results",
                    body: emailBody
                )
                
                echo "Email sent successfully to ${committer}"
            }
        }
    }
}
