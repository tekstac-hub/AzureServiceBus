package consumer;

import com.azure.messaging.servicebus.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServiceBusReceiver {

    public static void main(String[] args) {
        Properties props = new Properties();

        // Load config.properties from resources folder
        try (InputStream input = ServiceBusReceiver.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (input == null) {
                System.err.println("Unable to find config.properties in resources folder.");
                return;
            }

            props.load(input);

        } catch (IOException e) {
            System.err.println("Failed to load config.properties: " + e.getMessage());
            return;
        }

        String connectionString = props.getProperty("connectionString");
        String queueName = props.getProperty("queueName");

        if (connectionString == null || queueName == null) {
            System.err.println("Please set connectionString and queueName in config.properties");
            return;
        }

        // Create processor client to receive messages
        ServiceBusProcessorClient processorClient = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .processor()
                .
                
                queueName(queueName)
                .processMessage(context -> {
                    String body = context.getMessage().getBody().toString();
                    System.out.println("Received: " + body);
                })
                .processError(context -> System.err.println("Error occurred: " + context.getException()))
                .buildProcessorClient();

        processorClient.start();

        System.out.println("Listening for messages… Press ENTER to exit");
        new java.util.Scanner(System.in).nextLine();

        processorClient.close();
    }
}
