package com.sbytestream.samples.sqs;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

public class Application {
    private static void help() {
        System.out.println("Sends messages to an AWS SQS queue");
        System.out.println("java -jar sqsman p|c <queue-name>");
        System.out.println("p: runs the program as a sender of messages to the queue");
        System.out.println("c: runs the program as a reader of messages of the queue");
    }

    public static void main(String[] args) {
        new Application().run(args);
    }

    private void run(String[] args) {
        if (args.length != 2) {
            help();
            return;
        }

        String mode = args[0];
        if (!mode.equalsIgnoreCase(MODE_PRODUCER) && !mode.equalsIgnoreCase(MODE_CONSUMER)) {
            System.out.println("Invalid mode!");
            return;
        }

        String queueName = args[1];
        client = SqsClient.builder().region(Region.US_EAST_1).build();
        GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
                .queueName(queueName)
                .build();
        String queueUrl = client.getQueueUrl(getQueueRequest).queueUrl();

        if (mode.equalsIgnoreCase(MODE_PRODUCER)) {
            System.out.println("Running as producer (q to exit)");
            produce(queueUrl);
        }
        else if (mode.equalsIgnoreCase(MODE_CONSUMER)) {
            System.out.println("Running as consumer");
            consume(queueUrl);
        }
    }

    private void produce(String queueUrl) {
        Long messageId = 0L;
        while (true) {
            System.out.println(("Enter message: "));
            String message = System.console().readLine();
            if (message.equalsIgnoreCase("q")) {
                break;
            }

            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(message)
                    .messageDeduplicationId(messageId.toString())
                    .messageGroupId("1")
                    .delaySeconds(0)
                    .build();

            client.sendMessage(sendMsgRequest);
            messageId++;
        }
    }

    private void consume(String queueUrl) {
        boolean shutdown = false;
        while (!shutdown) {
            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .waitTimeSeconds(10)
                    .maxNumberOfMessages(1)
                    .build();

            ReceiveMessageResponse resp = client.receiveMessage(receiveRequest);

            for (Message msg : resp.messages()) {
                System.out.println(msg.body());

                DeleteMessageRequest deleteReq = DeleteMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .receiptHandle(msg.receiptHandle())
                        .build();

                DeleteMessageResponse deleteResp = client.deleteMessage(deleteReq);
                if (deleteResp.sdkHttpResponse().statusCode() != 200) {
                    System.out.println("Could not delete message");
                }

                if (msg.body().equalsIgnoreCase("shutdown")) {
                    System.out.println("Shutdown received, exiting...");
                    shutdown = true;
                    break;
                }
            }
        }
    }

    private SqsClient client = null;
    private final String MODE_CONSUMER = "c";
    private final String MODE_PRODUCER = "p";

}
