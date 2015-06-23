package Typedef;

import java.math.BigInteger;

public class Edge
{		
	public BigInteger data;
	
	public Edge(BigInteger data)
	{
		this.data = data;
	}

	public Edge()
	{
		super();
		
		data = BigInteger.ZERO;
		
	}
}
