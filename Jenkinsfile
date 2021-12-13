pipeline {
	agent {
		docker {
			image 'openjdk:8'
			alwaysPull true
		}
	}

	environment {
		KEYSTORE = credentials('kdt-jenkins-key')
	}

	stages {
		stage('Build') {
			steps {
				sh './mvnw --batch-mode -Dmaven.repo.local=maven compile'
			}
		}
		stage('Verify') {
			steps {
				sh './mvnw --batch-mode -Dmaven.repo.local=maven verify'
			}
		}
		stage('Package') {
			steps {
				sh './mvnw --batch-mode -Dmaven.repo.local=maven package'
			}
		}
		stage ('Publish to Repository') {
			when {
				branch 'main'
			}
			steps {
				sh './mvnw --batch-mode deploy -Dkeystore=${KEYSTORE}'
			}
		}
	}
}
