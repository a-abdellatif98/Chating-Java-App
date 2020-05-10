package chating;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import chating.Transactions;

public class Main {
	
	public static void main(String[] args){
		int pId = 0;
		
		try {
			System.out.println("Process: " + NodeI.services[pId]);
			
			Node obj = new Node(pId);
			initServer(obj);
			initClient(obj, pId);
			
		} catch (RemoteException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (NotBoundException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	public static void initServer(Node obj) throws RemoteException{
		Registry reg = LocateRegistry.createRegistry(obj.myPort);
		reg.rebind(obj.myService, obj);
	}
	
	private static void initClient(Node obj, int pId) throws RemoteException, NotBoundException {
		
		Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    obj.fetchNewTransaction();
                } catch (RemoteException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }, 0, 100);
		
		while (true){
			System.out.println("sending msg");
			String msg = obj.scan.next();
			String tId = UUID.randomUUID().toString();
			String sender = obj.myService;
			obj.lClock++;
			Transactions t = null;			
			t = new Transactions(tId, pId, sender, msg,obj.lClock);			
			obj.multicastTransaction(t);
		}
		
	}
	
}
