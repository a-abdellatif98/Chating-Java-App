package chating;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;

public class Node extends UnicastRemoteObject implements NodeI{
	private static final long serialVersionUID = 1L;
	
	final static int n = ipadresses.length;
	
	int lClock, myPort;
	String myIp, myService;
	PriorityQueue<Transactions> sander;
	HashMap<String, Integer> sanderAcks;
	Scanner scan;
	
	protected Node(int idx) throws RemoteException {
		myIp = ipadresses[idx];
		myPort = ports[idx];
		myService = services[idx];
		lClock = 0;
		sander = new PriorityQueue<Transactions>();
		sanderAcks = new HashMap<String, Integer>();
		scan = new Scanner(System.in);
	}

	public void multicastTransaction(Transactions t) throws RemoteException, NotBoundException {
		boolean delay = true;
		for(int i=0; i<n; i++){
			if(delay && t.sender.equals("A")) { // Delay Sending Transaction from A to C
				if(i == 2) {
					Delayer delayer = new Delayer(ports[i], services[i], t);
					new Thread(delayer).start();
					continue;
				}
			}
			Registry reg = LocateRegistry.getRegistry(ports[i]);
			NodeI e = (NodeI) reg.lookup(services[i]);
			e.performTransaction(t);
		}
		System.out.println("End Multicast Transaction:");
		displayTransactions();
		displayAcks();
	}

	@Override
	public void performTransaction(Transactions t) throws RemoteException, NotBoundException {
		sander.add(t);
		if(!t.sender.equalsIgnoreCase(myService)) {
			lClock = Math.max(lClock, t.clock) + 1;
		}
		System.out.println("Perform Transaction:");
		displayTransactions();
		displayAcks();
		multicastAck(t);
	}
	
	private void multicastAck(Transactions t) throws RemoteException, NotBoundException {
		boolean delay = true;
		for(int i=0; i<n; i++){
			if(delay && myService.equals("A")) { // Delay Sending Ack from A to C
				if(i == 2) {
					System.out.println("|||||||||||||||||||||||||||||||||||||||||");
					AckDelayer ackDelayer = new AckDelayer(ports[i], services[i], t);
					new Thread(ackDelayer).start();
					continue;
				}
			}
			Registry reg = LocateRegistry.getRegistry(ports[i]);
			NodeI e = (NodeI) reg.lookup(services[i]);
			e.ack(t);
		}
	}
	
	@Override
	public void ack(Transactions t) throws RemoteException {
		if(sanderAcks.containsKey(t.tId)){
			sanderAcks.put(t.tId, sanderAcks.get(t.tId) + 1);
		}
		else sanderAcks.put(t.tId, 1);
	}

	public void fetchNewTransaction() throws RemoteException {
		if(sander.size() > 0 && sanderAcks.containsKey(sander.peek().tId) 
				&& sanderAcks.get(sander.peek().tId) == n){
			//sending
			System.out.println("Fetch New msg:");
			displayTransactions();
			displayAcks();
			
			Transactions t = sander.poll();
			sanderAcks.remove(t.tId);
			
			System.out.println("After Fetch New msg:");
			displayTransactions();
			displayAcks();

			System.out.println("Performing msg: " + t);
			
			System.out.println("The msg is = " + t.msg);
		}
	}
	
	private void displayTransactions() {
		System.out.println("---------------massege--------------");
		for(Transactions t: sander) {
			System.out.println("(" + (t.msg) +" "+ t.sender + ", " + t.clock + ", " + t.tId + ")");
		}
		System.out.println("=========================================");
	}
	
	private void displayAcks() {
		System.out.println("---------------Acks--------------");
		Set<String> keys = sanderAcks.keySet();
		for(String k: keys) {
			System.out.println("(" + k + ", " + sanderAcks.get(k) + ")");
		}
		System.out.println("=========================================");
	}
	
}
