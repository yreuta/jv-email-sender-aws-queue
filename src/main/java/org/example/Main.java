package org.example;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import service.Sender;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String from = "from@mail";
        String username = "username";
        String password = "password";
        String host = "host";
        String queueUrl = "your queue";

        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
        receiveMessageRequest.setMaxNumberOfMessages(10);
        receiveMessageRequest.withMessageAttributeNames("All");
        List<Message> messages;

       while ((messages = sqs.receiveMessage(receiveMessageRequest).getMessages()).size() != 0) {
            Message message = messages.get(0);
            Sender sender = new Sender(username, password, host);
            String to = message.getMessageAttributes().get("email").getStringValue();
            boolean messageStatus = sender.sendMessage(from,
                    to, message.getMessageId(), message.getBody());
            if (messageStatus) {
                String messageReceiptHandle = message.getReceiptHandle();
                sqs.deleteMessage(new DeleteMessageRequest(queueUrl, messageReceiptHandle));
            } else {
                System.out.println("Can't send message with messageId: "
                        + message.getMessageId());
            }
        }
    }

}
