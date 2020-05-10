package chating;


import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class AckDelayer implements Runnable {
	
	private int port;
	private String serviceName;
	private Transactions transaction;
	
	public AckDelayer(int port, String serviceName, Transactions transaction) {
		this.port = port;
		this.serviceName = serviceName;
		this.transaction = transaction;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(7000);
			Registry reg = LocateRegistry.getRegistry(port);
			NodeI e = (NodeI) reg.lookup(serviceName);
			e.ack(transaction);
		} catch (RemoteException | NotBoundException | InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	
}
