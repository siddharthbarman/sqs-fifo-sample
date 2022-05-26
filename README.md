# aws-sqs-sample
The project demonstrates how to send and receive messages using long polling with AWS SQS FIFO queues. 

# Directory structure
code  - contains the Java client source code
docs  - some documentation
infra - Cloud Formation script to create a SQS FIFO queue

# Pre-requisites
- An AWS account with rights to create stacks using CloudFormation. 
- Rights to works with AWS SQS.
- Maven or a Java development IDE to compile the Java code
- An EC2 instance with JRE 1.8 or above to run the compiled Java application.

# Compiling
The project is based on Maven so you should already have Maven installed on your machine. If you use 
a Java development IDE like IntelliJ you can build the project using the IDE.

Building using Maven:
```
mvn package
```
The output is an executable jar named 'sqsman-1-jar-with-dependencies.jar'

# Running the program
The program runs in two modes:
- Producer
- Consumer

Before you can successfully run the program, you need to have an AWS SQS FIFO queue created in your AWS environment. The program assumes its running on a server which has a role attached to it that allows it to send and receive messages to and from the AWS SQS queue. You'll need to know the name of the queue as it needs to be passed to the program.


## Running as a producer
Running as a producer it allows you to type a text message on the console and sends it to the specified SQS FIFO queue.
```
java -jar sqsman-1-jar-with-dependencies.jar p demo-queue.fifo
```
Entering 'q' terminates the application.


## Running as consumer
Running as a consumer, sqsman monitors the specified SQS FIFO queue and read one message at a time, displays it on the screen and deletes it from the queue. 

```
java -jar sqsman-1-jar-with-dependencies.jar c demo-queue.fifo
```
If the consumer receives a special message 'shutdown' it shuts it self down. 
