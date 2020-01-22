/*
 * University of Central Florida
 * COP3330 - Fall 2018
 * Author: Ryan Flynn
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SentenceUtilsTest
{
	private static List<SentenceUtils> slist = new ArrayList<SentenceUtils>();

	public static void main(String[] args)
	{
		System.out.println("\n----------------------------------\n");
		System.out.println("COP3330 Sentence Utility Program by Ryan Flynn");
		System.out.println("\nInput file name: " + args[0]);

		try
		{
			File file = new File(args[0]);
			Scanner scanner = new Scanner(file);

			while(scanner.hasNextLine())
			{
				String s = scanner.nextLine();
				int sLength = s.length();

				if(sLength != 0 )
				{
					SentenceUtils sent = new SentenceUtils(s);
					slist.add(sent);
				}
			}

			int nums = slist.size();
			System.out.println("Number of Sentences " + nums+ "\n");

			int i = 0;
			while(i<nums)
			{
				System.out.println("Sentence " + i + ">" );
				slist.get(i).report();

				if(i == nums - 1)
				{
					break;
				}

				System.out.print("\n");
				i++;
			}

			scanner.close();
		}

		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
}
