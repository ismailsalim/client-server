package rmi;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.*;

import common.MessageInfo;

public class RMIClient {

	public static void main(String[] args) {

		RMIServerI iRMIServer = null;

		// Check arguments for Server host and number of messages
		if (args.length < 2) {
			System.out.println("Needs 2 arguments: ServerHostName/IPAddress, TotalMessageCount");
			System.exit(-1);
		}

		String urlServer = new String("rmi://" + args[0] + "/RMIServer");
		int numMessages = Integer.parseInt(args[1]);

		// TO-DO: Initialise Security Manager
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			// TO-DO: Bind to RMIServer
			iRMIServer = (RMIServerI) Naming.lookup(urlServer);
			// TO-DO: Attempt to send messages the specified number of times
			for (int i = 0; i < numMessages; i++) {
				System.out.println("Sending message");
				iRMIServer.receiveMessage(new MessageInfo(numMessages, i));
			}
		}
		catch (RemoteException e) {
			e.printStackTrace();
			System.out.println("Remote Exception: " + e);
		}
		catch (Exception e) {
			System.out.println("Not Bound Exception: " + e);
		}
	}
}
