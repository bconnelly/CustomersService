pipeline{
    agent{
        docker{
            image 'bryan949/fullstack-agent:0.1'
            args '-v /root/.m2:/root/.m2 \
                  -v /root/jenkins/restaurant-resources/:/root/jenkins/restaurant-resources/ \
                  -v /var/run/docker.sock:/var/run/docker.sock \
                  --privileged --env KOPS_STATE_STORE=' + env.KOPS_STATE_STORE + \
                  ' --env DOCKER_USER=' + env.DOCKER_USER + ' --env DOCKER_PASS=' + env.DOCKER_PASS
            alwaysPull true
        }
    }
    stages{
        stage('maven build and test, docker build and push'){
            steps{
                echo 'Packaging and testing:'
                sh '''
                    mvn verify
                    ls -alF
                '''
                stash includes: 'target/CustomersService.war', name: 'war'

            }
        }
        stage('build docker images'){
            steps{
                sh '''
                    docker login --username=$DOCKER_USER --password=$DOCKER_PASS
                    cp /root/jenkins/restaurant-resources/tomcat-users.xml .
                    cp /root/jenkins/restaurant-resources/context.xml .
                    cp /root/jenkins/restaurant-resources/server.xml .
                    docker build -t bryan949/fullstack-customers .
                    docker push bryan949/fullstack-customers:latest
                '''
            }
        }
        stage('configure cluster connection'){
            steps{
    	        sh '''
    	            ls -alF
	                kops export kubecfg --admin --name fullstack.k8s.local
	                if [ -z "$(kops validate cluster | grep ".k8s.local is ready")" ]; then exit 1; fi
	                kubectl config set-context --current --namespace dev
	            '''
            }
        }
        stage('deploy services to cluster'){
            steps{
                script{
                    sh 'git clone https://github.com/bconnelly/Restaurant-k8s-components.git'

                    def fileString = sh(script: 'find Restaurant-k8s-components -type f -path ./Restaurant-k8s-components/.git -prune -o -name *.yaml -print', returnStdout: true)
                    echo fileString
                    def files = fileString.split("\n")
                    for(file in files){
                        sh 'yq e \'.metadata.namespace = \"dev\"\' ' + file
                    }

                    sh '''
                        kubectl apply -f /root/jenkins/restaurant-resources/fullstack-secrets.yaml
                        kubectl apply -f Restaurant-k8s-components/
                        kubectl apply -f Restaurant-k8s-components/customers
                        kubectl get deployment
                        kubectl rollout restart deployment customers-deployment
                    '''
                    sh '''
                        if [ -z "$(kops validate cluster | grep ".k8s.local is ready")" ]; then exit 1; fi
                    '''
                }
            }
        }
        stage('integration testing'){
            steps{
                script{
                    sh '''
                        export LOAD_BALANCER="a886fa07e7d52403b85d9b8e2b9f6966-682684080.us-east-1.elb.amazonaws.com"
                        export SERVICE_PATH="RestaurantService"
                        export CUSTOMER_NAME=$RANDOM

                        SEAT_CUSTOMER_RESULT="$(curl -X POST -s -o /dev/null -w '%{http_code}' -d "firstName=$CUSTOMER_NAME&address=someaddress&cash=1.23" $LOAD_BALANCER/$SERVICE_PATH/seatCustomer)"
                        if [ "$SEAT_CUSTOMER_RESULT" != 200 ]; then echo "$SEAT_CUSTOMER_RESULT" && exit 1; fi

                        GET_OPEN_TABLES_RESULT="$(curl -s -o /dev/null -w %{http_code}  $LOAD_BALANCER/$SERVICE_PATH/getOpenTables)"
                        if [ "$GET_OPEN_TABLES_RESULT" != 200 ]; then echo "$GET_OPEN_TABLES_RESULT" && exit 1; fi

                        SUBMIT_ORDER_RESULT="$(curl -X POST -s -o /dev/null -w %{http_code} -d "firstName=$CUSTOMER_NAME&tableNumber=1&dish=food&bill=1.23" $LOAD_BALANCER/$SERVICE_PATH/submitOrder)"
                        if [ "$SUBMIT_ORDER_RESULT" != 200 ]; then echo "$SUBMIT_ORDER_RESULT" && exit 1; fi

                        BOOT_CUSTOMER_RESULT="$(curl -X POST -s -o /dev/null -w %{http_code} -d "firstName=$CUSTOMER_NAME" $LOAD_BALANCER/$SERVICE_PATH/bootCustomer)"
                        if [ "$BOOT_CUSTOMER_RESULT" != 200 ]; then echo "$GET_OPEN_TABLES_RESULT" && exit 1; fi
                    '''
                }
            }
        }
//         stage('cleanup'){
//             steps{
//                 script{
//
//                 }
//             }
//         }
    }
    post{
        always{
            script{
                sh 'docker rmi bryan949/fullstack-customers'
                sh 'docker image prune'
            }

            cleanWs(cleanWhenAborted: true,
                    cleanWhenFailure: true,
                    cleanWhenNotBuilt: true,
                    cleanWhenSuccess: true,
                    cleanWhenUnstable: true,
                    cleanupMatrixParent: true,
                    deleteDirs: true,
                    disableDeferredWipeout: true
            )
        }
    }
}