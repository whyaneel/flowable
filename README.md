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

-  And Copied the **OneTask.bpmn20.xml** to **src/main/resources/processes** folder for auto deployment by flowable starter

- **application.properties** has property **DATABASE_PLATFORM** as **org.hibernate.dialect.PostgreSQL9Dialect** to externalise database to **postgres**, yes!! that simple.

## w/ Event Driven Architecture
[Pending] Kafka-native approach (kafka, zookeeper)

## Bonus 
Have a look at Flowable Enterprise Architecture
[Pending] Comparision

## You've questions?
Signup on public forum https://forum.flowable.org/, otherwise I'm happy to help.

👏 Hope you learnt some basics of Flowable
