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
    environment{
        AWS_SECRET_ACCESS_KEY = credentials('AWS_SECRET_ACCESS_KEY')
        AWS_ACCESS_KEY_ID = credentials('AWS_ACCESS_KEY_ID')
        TOMCAT_USER = credentials('TOMCAT_USER')
        TOMCAT_PASS = credentials('TOMCAT_PASS')
    }
    stages{
        stage("Maven build and test"){
            steps {
                dir('CustomersService'){
                    script {
                        git url: "${CUSTOMERS_REPO}"
                        env.GIT_SHA = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                        env.MASTER_COMMIT = sh(script: 'git rev-parse master', returnStdout: true).trim()
                        env.PREV_IMAGE = sh(script: '''
                                                    docker pull bryan949/poc-customer:latest >> /dev/null
                                                    docker inspect --format='{{index .RepoDigests 0}}' bryan949/poc-customer:latest
                                                    ''', returnStdout: true).trim()
                    }
                }

                echo "MASTER_COMMIT: ${env.MASTER_COMMIT}"
                echo "GIT_SHA: ${env.GIT_SHA}"
                echo "PREV_IMAGE: ${env.PREV_IMAGE}"

                sh '''
                    mvn verify
                   '''
                stash name: 'customers-repo', useDefaultExcludes: false
            }
        }
        stage('Build and push docker image'){
            steps{
                dir('docker-build'){
                    unstash 'customers-repo'
                    withCredentials([
                        file(credentialsId: 'TOMCAT_SERVER_XML', variable: 'TOMCAT_SERVER_XML'),
                        file(credentialsId: 'TOMCAT_CONTEXT_XML', variable: 'TOMCAT_CONTEXT_XML')
                    ]) {
                        sh '''
                            cp "$TOMCAT_SERVER_XML" ./server.xml
                            cp "$TOMCAT_CONTEXT_XML" ./context.xml

                            docker build \
                             --build-arg TOMCAT_USER=${TOMCAT_USER} \
                             --build-arg TOMCAT_PASS=${TOMCAT_PASS} \
                             -t bryan949/poc-customers:${GIT_SHA} .
                            docker tag bryan949/poc-customers:${GIT_SHA} bryan949/poc-customers:latest
                            docker push bryan949/poc-customers:${GIT_SHA}
                            docker push bryan949/poc-customers:latest
                        '''
                    }
                }
            }
        }
        stage('Configure cluster connection'){
            steps{
    	        sh '''
	                kops export kubecfg --admin --name poc.k8s.local
	                if [ -z "$(kops validate cluster | grep ".k8s.local is ready")" ]; then exit 1; fi
	                kubectl config set-context --current --namespace rc
	            '''
            }
        }
        stage('Deploy services to cluster - rc'){
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

                    kubectl rollout restart deployment customers-deployment

                    if [ -z "$(kops validate cluster | grep ".k8s.local is ready")" ]; then echo "failed to deploy to rc namespace" && exit 1; fi
                    sleep 3
                '''
                stash includes: 'Restaurant-k8s-components/customers/', name: 'k8s-components'
                stash includes: 'Restaurant-k8s-components/tests.py', name: 'tests'
            }
        }
        stage('Sanity tests'){
            steps{
                unstash 'tests'
                sh '''
                    python Restaurant-k8s-components/tests.py ${RC_LB}
                    if [ $? -ne 0 ];
                    then
                        echo "Sanity tests failed"
                        exit 1
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
        stage('Deploy to cluster - prod'){
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
                    kubectl apply -f Restaurant-k8s-components/{poc-config.yaml,mysql-external-service.yaml}

                    kubectl rollout restart deployment customers-deployment

                    if [ -z "$(kops validate cluster | grep ".k8s.local is ready")" ]; then exit 1; fi
                    sleep 3
                '''
            }
        }
    }
    post{
        failure{
            withCredentials([gitUsernamePassword(credentialsId: 'GITHUB_USERPASS', gitToolName: 'Default')]) {
                unstash 'customers-repo'
                sh '''
                    echo "Reverting git master branch to previous commit ${MASTER_COMMIT}"
                    git checkout master
                    git reset --hard ${MASTER_COMMIT}
                    git push origin master --force
                '''
            }

            sh '''
                echo "Rolling back Docker image to previous digest"
                docker pull ${PREV_IMAGE}
                docker tag ${PREV_IMAGE} bryan949/poc-customers:latest
                docker push bryan949/poc-customers:latest

                POD=$(kubectl get pod | grep customer | cut -d ' ' -f 1)
                kubectl delete pod $POD
            '''

        }
        always{
            cleanWs(cleanWhenAborted: true,
                    cleanWhenFailure: true,
                    cleanWhenNotBuilt: true,
                    cleanWhenSuccess: true,
                    cleanWhenUnstable: true,
                    cleanupMatrixParent: true,
                    deleteDirs: true,
                    disableDeferredWipeout: true)

            sh '''
                docker rmi bryan949/poc-customers:${GIT_SHA} || true
                docker rmi bryan949/poc-customers:latest || true
                docker image prune -f || true
            '''
        }
    }
}
