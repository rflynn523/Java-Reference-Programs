/*
 * University of Central Florida
 * COP3330 - Fall 2018
 * Author: Ryan Flynn
 */

 // Tokens refer to the words in a sentence.
 // Shingles refer to a two character string where the adjacent letters in a
 // sentence make up each string.
 // cats.txt must be passed in through the command line when running SentenceUtilsTest

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SentenceUtils
{
	private String sentence;
	private String[] tokens;
	private String[] shingles;

	public SentenceUtils(String s)
	{
		sentence = s;
		generateTokens();
		generateShingles();
	}

	private void generateTokens()
	{
		tokens = sentence.split(" ");
	}

	private void generateShingles()
	{
		shingles = new String[sentence.length()-1];

		for(int i = 0; i < sentence.length()-1; i++)
		{
			shingles[i] = String.valueOf(sentence.charAt(i)) + String.valueOf(sentence.charAt(i+1));
		}
	}

	public void report()
	{
		System.out.println(sentence + "\n");
		System.out.println("Tokens:");

		for (int i = 0; i < tokens.length; i++)
		{
			System.out.println(i + ":" + tokens[i]);
		}

		System.out.print("\n");
		System.out.println("Shingles:");

		for(int i = 1; i<= shingles.length; i++)
		{
			System.out.print("'" + shingles[i-1]+ "'");
			if(i%10 == 0 && i != 1)
			{
				System.out.print("\n");
			}

			else
			{
				System.out.print(" ");
			}
		}

		System.out.print("\n");
	}
}
