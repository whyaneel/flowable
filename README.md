# Why Flowable?
You've a requirement where your application needs to have a long-running process and
- When, you're looking for an orchestration pattern/ tool
- When, you've a modelling mindset
- When, you're looking for low-code or no-code approach
- When, you're bored of translating requirements
- When, you've tasks that can be run in predictable order
- When, you've tasks that can be run in unpredictable order
- When, you've tasks that can be run with rules engine
- When, you want to take advantage of dynamic process execution, ad-hoc tasks implementations
- When, you're a fan of open-source with production grade quality tool
- And, a scalable, easy-to integrate as simple as Embeddable, or with Spring
- And, can be deployed to Cloud Foundry, Kubernetes and even can go server-less (boot up **< 15ms** approx)
- _And, especially you're looking for intelligent automation of business processes_
- _And, to take advantage of integration with message driven systems like Kafka, JMS, MQ_
- _And, you've a need for process-driven applications_

Then, **Flowable** should be your choice and these come with **Flowable open-source** itself.

Wait, You can also extend with **Flowable Enterprise** features like
- Digital Assistant, powered by AI which gives meaningful replies to your intents
- Conversational Engagement
- External Chat Channels including WhatsApp, LINE, and many more
- Built-in Content Repository, also extendable with other CMS
- Templating Engine
- Maps Integration over Conversations
- Reliability

Please note that **Flowable Enterprise** comprises **Flowable Engage, Flowable Work, Flowable Orchestrate**

# Scope
Limited to BPMN Engine, will focus to explain through code mostly, but wherever needed will add additional info to digest the fundamentals.
Here I'll walk you through with help of Docker Technologies, as the demand for Containerization is High. Follow branch specific readme, once you checkout branch for each section.

## Boilerplate
This section focuses on SpringBoot app with Flowable. Developers who are exploring for the first time go with this.

There's no readme for this branch, Unless you can't follow by tests, you can skip and proceed to next Section.
```
git checkout https://github.com/whyaneel/flowable.git

cd flowable

git checkout boilerplate
```

- You'll have Flowable Process Engine ready for use with in-memory database
- You'll have an auto deployed process (bpmn20.xml file)
- You'll notice how Listeners and Delegates for Tasks are resolved
- **You'll have to go through Test to understand**
    - a process-driven application can be run, we can split whole test into multiple API Endpoints to integrate with UI
    - how to create a job/ process instance
    - how a user task (wait state), service task (auto complete) transition to next tasks
    - how a user or group can be assigned to a task
- Test is expected to fail, uncomment line of code `markComplete_ForParallelTasks` to pass the test
- This exercise is purely done based on Tutorial from Joshua https://youtu.be/43_OLrxU3so
- [x] You'll see the need for External Database to have the State Machine even your spring-boot application crashes or restarted, come on then follow **OneTaskApp** section.

## w/ Externalise Database (Postgres)
This section focuses on minimalistic setup of Flowable plus externalising database. Of course this time we take advantage of docker for containerization.

```
git checkout https://github.com/whyaneel/flowable.git

cd flowable

git checkout onetaskapp
```

We've modelled a **OneTask** process in Flowable Modeller with Start Event, Service Task, End Event. And downloaded the file as **OneTask.bpmn20.xml**.
![OneTask Process](https://github.com/whyaneel/flowable/blob/onetaskapp/readme/OneTask_BPMN_Model.png?raw=true)

-  And Copied the **OneTask.bpmn20.xml** to **src/main/resources/processes** folder, sothat FlowableAutoConfiguration will deploy them, which will be picked up by ProcessEngine.

- **application.properties** has property **DATABASE_DRIVER_CLASS_NAME** as **org.postgresql.Driver** to externalise database to **postgres**, yes!! that simple.

- For the trigger, we wrote a simple Rest Controller 

## w/ Event Driven Architecture

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

**Kafka-native Approach** with Single Node Kafka Cluster and by updating pom.xml, application.properties
 - **pom.xml** will  have dependency **org.springframework.kafka:spring-kafka**
 - **application.properties** has property **BOOTSTRAP_SERVER** as **kafka:9092** to communicate natively with **kafka**, yes!! that simple again.
 
For the trigger, instead of Rest Controller here we want to use a different approach.
- You can manually produce a message to the topic ONE_TASK_TOPIC_JSON from Kafka Console
- Mocking Produce Message with a additional Flowable Process

## Bonus 
Have a look at Flowable Engage Architecture

![Flowable Engage](https://github.com/whyaneel/flowable/blob/master/readme/flowable-engage-arch.png?raw=true)

Grab more details at Source: https://documentation.flowable.com/engage-install/3.5.0/110-overview.html

## You've questions?
Signup on public forum https://forum.flowable.org/, otherwise I'm happy to help.

👏 Hope you learnt some basics of Flowable
