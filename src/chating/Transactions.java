package chating;

import java.io.Serializable;

public class Transactions implements Comparable<Transactions>, Serializable{
	
	private static final long serialVersionUID = 1L;
	
	String tId, sender,msg;
	int pId, clock;
	


	public  Transactions(String tId, int pId, String sender,String msg,int clock) {
		this.tId = tId;
		this.pId = pId;
		this.sender = sender;
		this.msg = msg;
		this.clock = clock;		
	}

	@Override
	public String toString() {
		return sender + ": " + (msg) + ", Logical Clock = " + clock;
	}
	
	// Total Order using logical clocks
	@Override
	public int compareTo(Transactions o) {
		// Tie Breaker
		if(this.clock == o.clock)
			return this.pId - o.pId;
		return this.clock - o.clock;
	}
	
}
