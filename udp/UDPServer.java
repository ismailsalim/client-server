/*
 * Created on 01-Mar-2016
 */
package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;

import common.MessageInfo;

public class UDPServer {

    private DatagramSocket recvSoc;
    private int totalMessages = -1;
    private int[] receivedMessages;
    private boolean close;
    private int port;

    private void run() {
        int pacSize = 1000;
        byte[] pacData;
        DatagramPacket pac;

        // TO-DO: Receive the messages and process them by calling processMessage(...).
        //        Use a timeout (e.g. 30 secs) to ensure the program doesn't block forever
        try {
            recvSoc = new DatagramSocket(port);
            int timeOutLength = 30000;
            recvSoc.setSoTimeout(timeOutLength);
            pacData = new byte[pacSize];
            while (true) {
                pac = new DatagramPacket(pacData, pacSize);
                recvSoc.receive(pac);
                String dataAsString = new String(pac.getData());
                close = processMessage(dataAsString);
                DatagramPacket reply = new DatagramPacket(pac.getData(), pac.getLength(),
                        pac.getAddress(), pac.getPort());
                recvSoc.send(reply);
                if (close) {
                	recvSoc.close();
				}

            }
        } catch (SocketTimeoutException e) {
            System.out.println("Timeout reached! " + e);
            System.out.println("Log analysis: ");
            missingMessageLogs();
            recvSoc.close();
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println(("IO: " + e.getMessage()));
        }
    }

    public boolean processMessage(String data) {

        MessageInfo msg = null;
//		 TO-DO: Use the data to construct a new MessageInfo object
        try {
            msg = new MessageInfo(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // TO-DO: On receipt of first message, initialise the receive buffer
        if (totalMessages == -1) {
            totalMessages = msg.totalMessages;
            receivedMessages = new int[totalMessages];
        }

        // TO-DO: Log receipt of the message
        System.out.println("Received Message: " + msg.messageNum);
        receivedMessages[msg.messageNum] = 1;

        // TO-DO: If this is the last expected message, then identify
        //        any missing messages
        if (msg.messageNum == totalMessages - 1) {
            missingMessageLogs();
            return true;
        }
        return false;

    }

    private void missingMessageLogs() {
        System.out.println("Number of messages expected: " + totalMessages);
        int totalReceived = Arrays.stream(receivedMessages).sum();
        System.out.println("Number of messages received: " + totalReceived);
        if (totalReceived != totalMessages) {
            System.out.println((totalMessages - totalReceived) + " messages dropped");
            System.out.println("Identifying dropped messages:");
            for (int i = 0; i < totalMessages; i++) {
                if (receivedMessages[i] == 0) {
                    System.out.println("Message " + i + " dropped");
                }
            }
        }
    }


    public UDPServer(int rp) {
        // TO-DO: Initialise UDP socket for receiving data
        port = rp;
        // Done Initialisation
        System.out.println("UDPServer ready");
    }

    public static void main(String args[]) {
        int recvPort;

        // Get the parameters from command line
        if (args.length < 1) {
            System.err.println("Arguments required: recv port");
            System.exit(-1);
        }
        recvPort = Integer.parseInt(args[0]);

        // TO-DO: Construct Server object and start it by calling run().
        UDPServer server = new UDPServer(recvPort);
        server.run();

    }

}
