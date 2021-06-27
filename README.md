### Pre-requisites
- Git
- Docker
- Maven

### 1. Checkout code
```
git checkout https://github.com/whyaneel/flowable.git

cd flowable

git checkout onetaskapp
```
We've modelled a **one-task** process in Flowable Modeller with Start Event, Service Task, End Event. Also, mapped Service Task (LogHelloTask) with a Spring Bean using delegateExpression **${logHelloTask}** which implements Flowable's **JavaDelegate**.

Here is the BPMN Process

![OneTask Process](https://github.com/whyaneel/flowable/blob/onetaskapp/readme/OneTask_BPMN_Model.png?raw=true)

### 2. Bring Up
```
./startup.sh

docker ps
```

#### It shows as below and you're ready to use
```
CONTAINER ID   IMAGE                 COMMAND                  CREATED          STATUS          PORTS                                                                                  NAMES
e54669636ace   one-task-app:latest   "java -cp /app/resou…"   28 seconds ago   Up 19 seconds   0.0.0.0:7090->7090/tcp, :::7090->7090/tcp, 0.0.0.0:9090->9090/tcp, :::9090->9090/tcp   one-task
9e9893df8416   flowable/all-in-one   "/opt/tomcat/bin/cat…"   28 seconds ago   Up 21 seconds   0.0.0.0:8080->8080/tcp, :::8080->8080/tcp                                              flowable
be754a38c3ff   postgres:latest       "docker-entrypoint.s…"   28 seconds ago   Up 24 seconds   0.0.0.0:5432->5432/tcp, :::5432->5432/tcp                                              database
```
Please note that **flowable** container is generally not needed, unless you need to model a process. Perhaps access via http://localhost:8080/flowable-modeler/ and import the **OneTask.bpmn20.xml** to see delegateExpression or other parameters.

### 3. Test & Debug
#### Check whether App is running at 9090
```
docker-compose logs -f one-task
```

![OneTaskApp Bootup Logs](https://github.com/whyaneel/flowable/blob/onetaskapp/readme/OneTaskApp_Running.png?raw=true)

#### Check DB for following tables whether auto deployment is successful 

![ONE_TASK_DB](https://github.com/whyaneel/flowable/blob/onetaskapp/readme/OneTaskModel_Auto_Deployed.png?raw=true)

#### Hit the API to start the process
```
curl -X POST 'http://localhost:9090/one-task/Anil'
```
You will get a JobId as response for the curl and also you'll see following logs from **LogHelloTask** Spring Bean Implementation. This means a process instance got created and the **events, tasks** are executed in order as per **OneTask.bpmn20.xml** Model.

![OneTaskApp ProcessStarted Logs](https://github.com/whyaneel/flowable/blob/onetaskapp/readme/LogHelloTask_Logs.png?raw=true)

#### Exit Terminals Wherever required
```
Ctrl + C
```

### 4. Clean Up
`./shutdown.sh`
