### Pre-requisites
- Git
- Docker
- Maven

### 1. Checkout code
```
git checkout https://github.com/whyaneel/flowable.git

cd flowable

git checkout onetaskapp-kafka
```

### 2. Understand the Components

#### 2.1 Single Node Kafka Cluster
With help of docker we have added Kafka, Zookeeper docker images which forms a single node kafka cluster.
- confluentinc/cp-kafka:5.5.1
- confluentinc/cp-zookeeper:5.5.1

#### 2.2 Application Properties of Spring Boot Application
```
# Kafka Integration
spring.kafka.listener.missing-topics-fatal=false
spring.kafka.bootstrap-servers=${BOOTSTRAP_SERVER}
spring.kafka.producer.properties.acks=${PRODUCER_ACKS}
consumer.group=onetaskapp
```

#### 2.3 OneTaskKafkaConsumer BPMN Process
We've modelled  **OneTaskKafkaConsumer** process in Flowable Modeller with Start Event Registry Event, Service Task, End Event. 
- Start Event Registry Event, which waits for a message on topic configured to trigger the process automatically. 
- Also, mapped Service Task (LogHelloTask) with a Spring Bean using delegateExpression **${logHelloTask}** which implements Flowable's **JavaDelegate**.

Here is the **OneTaskKafkaConsumer** BPMN Process

![Consumer_OneTaskKafkaConsumerProcess__StartEventRegistryEvent_Details](https://github.com/whyaneel/flowable/blob/onetaskapp-kafka/readme/Consumer_OneTaskKafkaConsumerProcess__StartEventRegistryEvent_Details.png?raw=true)

We have kept these event and channel files under **/resources/eventregistry/** folder, sothat FlowableAutoConfiguration will deploy them, which will be picked up by EventRegistryEngine.
- Event: oneTaskKafkaConsume.event
- Channel: oneTaskKafkaInbound.channel (Topic as ONE_TASK_TOPIC_JSON)
- Consumer_MappingsFromEventPayload: (We do this in Flowable Modeler)

![Consumer_MappingsFromEventPayload](https://github.com/whyaneel/flowable/blob/onetaskapp-kafka/readme/Consumer_MappingsFromEventPayload.png?raw=true)

For the trigger, instead of Rest Controller here we want to use a different approach.
- You can manually produce a message to the topic ONE_TASK_TOPIC_JSON
- Or, I've created a another process to simplify mocking producing the message with **OneTaskKafkaProducer**

#### 2.4 OneTaskKafkaProducer BPMN Process
Have a look at **OneTaskKafkaProducer** BPMN Process, which is again modelled in Flowable Modeller with Start Event, Send Event Task, End Event.
- Send Event Task, which can either produce a message or consume a message based on Outbound and Inbound configuration with Event & Channel Files
- Here we are using Send Event Task as a Producer

![Producer_OneTaskKafkaProducerProcess__SendEventRegistryEvent_Details](https://github.com/whyaneel/flowable/blob/onetaskapp-kafka/readme/Producer_OneTaskKafkaProducerProcess__SendEventRegistryEvent_Details.png?raw=true)

We have kept these event and channel files under **/resources/eventregistry/** folder, sothat FlowableAutoConfiguration will deploy them, which will be picked up by EventRegistryEngine.
- Event: oneTaskKafkaProduce.event
- Channel: oneTaskKafkaOutbound.channel (Topic as ONE_TASK_TOPIC_JSON)
- Producer_MappingsToEventPayload: (We do this in Flowable Modeler)

![Producer_MappingsToEventPayload](https://github.com/whyaneel/flowable/blob/onetaskapp-kafka/readme/Producer_MappingsToEventPayload.png?raw=true)

For the trigger, we are using CommandLineRunner which will produce 1 Sample Message on Starting the Application. So you can relax and watch how Flowable is communicating with Kafka, as a Producer and Consumer.

Technically, we can combine these 2 processes as one, but I want to give you a One Task Process approach so we know what's happening with each Process.

### 2. Bring Up
```
./startup.sh

docker ps
```

#### It shows as below and you're ready to use
```
CONTAINER ID   IMAGE                             COMMAND                  CREATED             STATUS             PORTS                                                                                  NAMES
ee72d2c4f985   confluentinc/cp-kafka:5.5.1       "/etc/confluent/dock…"   About an hour ago   Up About an hour   0.0.0.0:9092->9092/tcp, :::9092->9092/tcp                                              kafka
39b6047db178   flowable/all-in-one               "/opt/tomcat/bin/cat…"   About an hour ago   Up About an hour   0.0.0.0:8080->8080/tcp, :::8080->8080/tcp                                              flowable
d9ec64c69939   postgres:latest                   "docker-entrypoint.s…"   About an hour ago   Up About an hour   0.0.0.0:5432->5432/tcp, :::5432->5432/tcp                                              database
ebc6c7e428cb   one-task-app-kafka:latest         "java -cp /app/resou…"   About an hour ago   Up 35 minutes      0.0.0.0:7090->7090/tcp, :::7090->7090/tcp, 0.0.0.0:9090->9090/tcp, :::9090->9090/tcp   one-task
3d9d4e09bc15   confluentinc/cp-zookeeper:5.5.1   "/etc/confluent/dock…"   About an hour ago   Up About an hour   2888/tcp, 0.0.0.0:2181->2181/tcp, :::2181->2181/tcp, 3888/tcp                          zookeeper

```
Please note that **flowable** container is generally not needed, unless you need to model a process. Perhaps access via http://localhost:8080/flowable-modeler/ and import the **OneTaskKafkaConsumer.bpmn20.xml** to see delegateExpression, channelKey, eventKey or other parameters.

### 3. Test & Debug
#### 3.1 Logs of Spring Boot Application at port 9090
```
docker-compose logs -f one-task
```

#### 3.2 Logs - EventRegistryEngine created
![Startup__EventRegistryEngine_created](https://github.com/whyaneel/flowable/blob/onetaskapp-kafka/readme/Startup__EventRegistryEngine_created.png?raw=true)

#### 3.2 Logs - All Inbound Outbound Channels are read
![Startup__configures_all_Inbound_Outbound_Channels](https://github.com/whyaneel/flowable/blob/onetaskapp-kafka/readme/Startup__configures_all_Inbound_Outbound_Channels.png?raw=true)

#### 3.3 DB - Check DB for following tables whether auto deployment is successful for processes and event, channel files

![Process_Auto_Deployed](https://github.com/whyaneel/flowable/blob/onetaskapp-kafka/readme/Process_Auto_Deployed.png?raw=true)
![EventsChannels_Auto_Deployed](https://github.com/whyaneel/flowable/blob/onetaskapp-kafka/readme/EventsChannels_Auto_Deployed.png?raw=true)

#### 3.4 Logs - CommandLineRunner Mocking ProduceMessage with SendEventRegistryTask
CommandLineRunner which will produce 1 Sample Message on Starting the Application with help of OneTaskKafkaProducer process

![CommandLineRunner__Mocking_ProduceMessage_with__SendEventRegistryTask](https://github.com/whyaneel/flowable/blob/onetaskapp-kafka/readme/CommandLineRunner__Mocking_ProduceMessage_with__SendEventRegistryTask.png?raw=true)

We can actually see this message available in kafka topic as well.

#### 3.5 Logs - StartEventRegistryEvent Triggers OneTaskKafkaConsumer Process
As soon as the message arrived on configured topic, StartEventRegistryEvent gets triggered

![StartEventRegistryEvent_Triggers_OneTaskKafkaConsumer_Process](https://github.com/whyaneel/flowable/blob/onetaskapp-kafka/readme/StartEventRegistryEvent_Triggers_OneTaskKafkaConsumer_Process.png?raw=true)

#### 3.6 Kafka - Alternatively, Manually Produce Message
- In this case we  don't need OneTaskKafkaProducer process and its event/ channel files
- We can produce a message to the topic ONE_TASK_TOPIC_JSON using kafka-console-producer, and immediately the message gets processed by **OneTaskKafkaConsumer** Process
```
docker exec -it kafka bash

kafka-console-producer --broker-list kafka:9092 --topic ONE_TASK_TOPIC_JSON

{"name":"Anil","githubHandle":"whyaneel"}
```

#### Exit Terminals Wherever required
```
Ctrl + C
```

### 4. Clean Up
`./shutdown.sh`