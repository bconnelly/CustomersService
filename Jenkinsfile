pipeline{
    agent{
        docker{
            image 'bryan949/poc-agent:0.2.1'
            args '-v /root/.m2:/root/.m2 \
                  -v /root/jenkins/restaurant-resources/:/root/jenkins/restaurant-resources/ \
                  -v /var/run/docker.sock:/var/run/docker.sock \
                  --privileged --env KOPS_STATE_STORE=${KOPS_STATE_STORE} \
                  --env DOCKER_USER=${DOCKER_USER} --env DOCKER_PASS=${DOCKER_PASS}'
            alwaysPull true
        }
    }
    options{
        skipDefaultCheckout()
    }
    environment{
        AWS_SECRET_ACCESS_KEY = credentials('AWS_SECRET_ACCESS_KEY')
        AWS_ACCESS_KEY_ID = credentials('AWS_ACCESS_KEY_ID')
    }
    stages{
        stage("Checkout and store rollback variables"){
            steps {
                checkout scm
                script {
                    // Save the current master commit SHA
                    env.MASTER_COMMIT = sh(script: 'git rev-parse origin/master', returnStdout: true).trim()

                    // Save the current short SHA of the checked-out commit (HEAD)
                    env.GIT_SHA = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()

                    // Save the current image digest of the latest pushed Docker image
                    env.PREV_IMAGE = sh(script: '''
                        docker pull bryan949/poc-customers:latest || true
                        docker inspect --format='{{index .RepoDigests 0}}' bryan949/poc-customers:latest || echo "none"
                    ''', returnStdout: true).trim()
                }
            }
        }
        stage('Maven build and test'){
            steps{
                sh 'mvn verify'
                stash name: 'customers-repo', useDefaultExcludes: false

            }
        }
        stage('build and push docker image'){
            steps{
                unstash 'customers-repo'
                sh '''
                    cp /root/jenkins/restaurant-resources/tomcat-users.xml .
                    cp /root/jenkins/restaurant-resources/context.xml .
                    cp /root/jenkins/restaurant-resources/server.xml .

                    docker build -t bryan949/poc-customers .
                    docker push bryan949/poc-customers:latest

                    rm tomcat-users.xml
                    rm context.xml
                    rm server.xml
                '''
            }
        }
        stage('configure cluster connection'){
            steps{
    	        sh '''
	                kops export kubecfg --admin --name poc.k8s.local
	                if [ -z "$(kops validate cluster | grep ".k8s.local is ready")" ]; then exit 1; fi
	                kubectl config set-context --current --namespace rc
	            '''
            }
        }
        stage('deploy services to cluster - rc namespace'){
            steps{
                sh '''
                    git clone https://github.com/bconnelly/Restaurant-k8s-components.git

                    find Restaurant-k8s-components -type f -path ./Restaurant-k8s-components/customers -prune -o -name *.yaml -print | while read line; do yq -i '.metadata.namespace = "rc"' $line > /dev/null; done
                    yq -i '.metadata.namespace = "rc"' /root/jenkins/restaurant-resources/poc-secrets.yaml > /dev/null
                    yq -i '.metadata.namespace = "rc"' Restaurant-k8s-components/poc-config.yaml > /dev/null
                    yq -i '.metadata.namespace = "rc"' Restaurant-k8s-components/mysql-external-service.yaml > /dev/null

                    kubectl apply -f /root/jenkins/restaurant-resources/poc-secrets.yaml
                    kubectl apply -f Restaurant-k8s-components/customers
                    kubectl apply -f Restaurant-k8s-components/poc-config.yaml
                    kubectl apply -f Restaurant-k8s-components/mysql-external-service.yaml
                    kubectl get deployment
                    kubectl rollout restart deployment customers-deployment

                    if [ -z "$(kops validate cluster | grep ".k8s.local is ready")" ]; then echo "failed to deploy to rc namespace" && exit 1; fi
                    sleep 3
                '''
                stash includes: 'Restaurant-k8s-components/customers/', name: 'k8s-components'
                stash includes: 'Restaurant-k8s-components/tests.py,Restaurant-k8s-components/tests.py', name: 'tests'
            }
        }
        stage('sanity tests'){
            steps{
                unstash 'tests'
                sh '''
                    ls -alF
                    python Restaurant-k8s-components/tests.py ${RC_LB}
                    exit_status=$?
                    if [ "${exit_status}" -ne 0 ];
                    then
                        echo "exit ${exit_status}"
                    fi
                '''

                withCredentials([gitUsernamePassword(credentialsId: 'GITHUB_USERPASS', gitToolName: 'Default')]) {
                    sh '''
                        git checkout rc
                        git checkout master
                        git merge rc
                        git push origin master
                    '''
                }
            }
        }
        stage('deploy to cluster - prod namespace'){
            steps{
                unstash 'k8s-components'

                sh '''
                    find Restaurant-k8s-components -type f -path ./Restaurant-k8s-components/customers -prune -o -name *.yaml -print | while read line; do yq -i '.metadata.namespace = "prod"' $line > /dev/null; done
                    yq -i '.metadata.namespace = "prod"' /root/jenkins/restaurant-resources/poc-secrets.yaml > /dev/null
                    yq -i '.metadata.namespace = "prod"' Restaurant-k8s-components/poc-config.yaml > /dev/null
                    yq -i '.metadata.namespace = "prod"' Restaurant-k8s-components/mysql-external-service.yaml > /dev/null

                    kubectl config set-context --current --namespace prod
                    kubectl apply -f /root/jenkins/restaurant-resources/poc-secrets.yaml
                    kubectl apply -f Restaurant-k8s-components/customers/
                    kubectl apply -f Restaurant-k8s-components/poc-config.yaml
                    kubectl apply -f Restaurant-k8s-components/mysql-external-service.yaml
                    kubectl get deployment
                    kubectl rollout restart deployment customers-deployment

                    if [ -z "$(kops validate cluster | grep ".k8s.local is ready")" ]; then exit 1; fi
                    sleep 3
                '''
            }
        }
    }
    post{
        failure{
            unstash 'customers-repo'
            withCredentials([gitUsernamePassword(credentialsId: 'GITHUB_USERPASS', gitToolName: 'Default')]) {
                sh '''
                    git checkout rc
                    git checkout master
                    git rev-list --left-right master...rc | while read line
                    do
                        COMMIT=$(echo $line | sed 's/[^0-9a-f]*//g')
                        git revert $COMMIT --no-edit
                    done
                    git merge rc
                    git push origin master
                '''
            }
        }
        always{
            script{
                if (getContext(hudson.FilePath)) {
                    cleanWs(cleanWhenAborted: true,
                            cleanWhenFailure: true,
                            cleanWhenNotBuilt: true,
                            cleanWhenSuccess: true,
                            cleanWhenUnstable: true,
                            cleanupMatrixParent: true,
                            deleteDirs: true,
                            disableDeferredWipeout: true)

                    sh '''
                        docker rmi bryan949/poc-customers
                        docker image prune
                    '''
                }
            }

        }
    }
}
