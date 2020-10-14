package rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.*;
import java.util.Arrays;

import common.*;

public class RMIServer extends UnicastRemoteObject implements RMIServerI {

	private int totalMessages = -1;
	private int[] receivedMessages;

	public RMIServer() throws RemoteException {
	}

	public void receiveMessage(MessageInfo msg) throws RemoteException {
		// TO-DO: On receipt of first message, initialise the receive buffer
		if (totalMessages == -1) {
			totalMessages = msg.totalMessages;
			System.out.println("First message received, num messages expected: " + totalMessages);
			receivedMessages = new int[totalMessages];
		}

		// TO-DO: Log receipt of the message
		int message_id = msg.messageNum;
		System.out.println("Received Message: " + message_id);
		receivedMessages[message_id] = 1;

		// TO-DO: If this is the last expected message, then identify
		//        any missing messages
		if (message_id == (totalMessages - 1)) {
			missingMessageLogs();
		}
	}


	public static void main(String[] args) {

		RMIServer rmis = null;

		// TO-DO: Initialise Security Manager
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		// TO-DO: Instantiate the server class
		try {
			rmis = new RMIServer();
			rebindServer("RMIServer", rmis);
		} catch (Exception e) {
			System.out.println("Server Exception");
			e.printStackTrace();
		}
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

	protected static void rebindServer(String serverName, RMIServer server) {
		// TO-DO:
		// Start / find the registry (hint use LocateRegistry.createRegistry(...)
		// If we *know* the registry is running we could skip this (eg run rmiregistry in the start script)
		Registry registry = null;
		int port = 1099;
		try {
			LocateRegistry.createRegistry(port);
		}
		catch (RemoteException e) {
			System.out.println("Registry already running");
		}
		try {
			registry = LocateRegistry.getRegistry();
			registry.rebind(serverName, server);
			System.out.println("RMIServer is bound");
		}
		catch (RemoteException e) {
			System.out.println("Can't find registry");
		}

	}
}
