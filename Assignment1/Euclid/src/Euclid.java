/*
 * University of Central Florida
 * COP330 - Fall 2018
 * Author: Ryan Flynn
 */

 // This program implements Euclid's algorithm to determine the Greateest Common Diviser.
 // This is done by using the equation: a = b (q) + r.
 // After one iteration a becomes q, q becomes r, then b and r are solved for.
 // Repeat until r is equal to zero.

public class Euclid
{
  public static void main(String[] args)
	{
		int x = Integer.parseInt(args[0]);
		int y = Integer.parseInt(args[1]);

		System.out.println("\n---------------\n");
		System.out.println("Euclid's Algorithm by Ryan Flynn:\n");
		System.out.println("Inputs: " + x +", "  + y);

		gcd(x,y);
	}

	private static void gcd( int a,int b)
	{
		int A, B, q, r, x;
		A=a;
		B=b;
		x=b;
		q = a/b;
		r = a%b;
		if(r == 0)
		{
			System.out.println( a + " = " + q + " ( " + b +" ) " +" + "+ r);
		}

		while(r!=0)
		{
			q = a/b;
			r = a%b;

			System.out.println( a + " = " + q + " ( " + b +" ) " +" + "+ r);

			if(r!=0)
			{
				a=b;
				b=r;
				x=r;
			}
		}

		System.out.println("\n==> gcd(" + A +", " + B +") = " + x);
	}
}
