package chating;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NodeI extends Remote{
	String[] ipadresses = {"127.0.0.1", "127.0.0.1", "127.0.0.1"};
	String[] services = {"Sender A", "Sender B", "Sender C"};
	Integer[] ports = {2000, 3000, 4000};
	
	void performTransaction(Transactions t) throws RemoteException, NotBoundException;
	void ack(Transactions t) throws RemoteException;
}
