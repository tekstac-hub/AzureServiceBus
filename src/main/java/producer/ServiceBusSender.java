package producer;

import com.azure.messaging.servicebus.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

public class ServiceBusSender {

    public static void main(String[] args) {
        Properties props = new Properties();

        // Load config.properties
        try (InputStream input = ServiceBusSender.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (input == null) {
                System.err.println("ERROR: config.properties not found in resources folder.");
                return;
            }

            props.load(input);
        } catch (IOException e) {
            System.err.println("ERROR: Failed to load config.properties: " + e.getMessage());
            return;
        }

        String connectionString = props.getProperty("connectionString");
        String queueName = props.getProperty("queueName");

        if (connectionString == null || queueName == null) {
            System.err.println("ERROR: Please set connectionString and queueName in config.properties");
            return;
        }

        System.out.println("DEBUG: Using queue: " + queueName);

        // Create sender client
        ServiceBusSenderClient senderClient = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .sender()
                .queueName(queueName)
                .buildClient();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Type your messages to send to the queue. Type 'exit' to quit.");

        while (true) {
            System.out.print("Enter message: ");
            String userMessage = scanner.nextLine();

            if (userMessage.equalsIgnoreCase("exit")) {
                break;
            }

            ServiceBusMessage message = new ServiceBusMessage(userMessage);

            try {
                senderClient.sendMessage(message);
                System.out.println("DEBUG: Sent message -> " + userMessage);
            } catch (Exception e) {
                System.err.println("ERROR: Failed to send message: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Close resources
        System.out.println("DEBUG: Closing sender client.");
        senderClient.close();
        scanner.close();
        System.out.println("DEBUG: Sender client closed. Exiting.");
    }
}
