import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class LZW {

	private String fileName;
	private int dictionarySize;
	private ArrayList<String> message;
	private ArrayList<String> dictionary;
	private ArrayList<Integer> output;
	private ArrayList<String> dictionarySingles;

	public LZW(String newFileName, int size)
	// Create an image and read the data from the file
	{
		output = new ArrayList<Integer>();
		this.fileName = newFileName;
		this.dictionarySize = size;
		try {
			this.message = readFile(newFileName);
		} catch (EOFException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.dictionary = uniques(this.message);
		this.dictionarySingles = uniques(this.message);
	}
	public void decompressionRatio(){
		//System.out.println(output.size() +  "  array size");
		double mBits = (message.size() * 8.0)/(output.size()* 8.0);
		System.out.println("Compression Ratio = " + mBits);
		
	}
	
	

	public void encode() {

		for (int i = 0; i < message.size(); i++) {

			String w = message.get(i);

			if (i == message.size() - 1) {

				output.add(dictionary.indexOf(w));
				break;
			}
			int j = i + 1;

			if (dictionary.contains(w + message.get(j))) {

				w = w + message.get(j);

				j = j + 1;

				while (dictionary.contains(w + message.get(j))) {

					w += message.get(j);

					j++;
				}

				if (dictionary.size() < this.dictionarySize)
					dictionary.add(w + message.get(j));
				output.add(dictionary.indexOf(w));

				i = j - 1;

			} else {

				if (dictionary.size() < this.dictionarySize)
					dictionary.add(w + message.get(j));
				output.add(dictionary.indexOf(w));
			}

		}

	}

	public String decode() {

		int i = 0;
		String code = "";

		while (i < output.size()) {

			int K = i + 1;
			String w = dictionarySingles.get(output.get(i));
			code += w;
			
				try{
				if(dictionarySingles.contains(dictionarySingles.get(output.get(K)))){
					
					dictionarySingles.add(w + dictionarySingles.get(output.get(K)).charAt(0));
				}
				
					
				}
				catch(Exception e)
				{
					dictionarySingles.add(w + w.charAt(0));
				}
			
			
			i++;

		}

		return code;

	}

	public ArrayList<String> uniques(ArrayList<String> l) {

		Set<String> set = new LinkedHashSet<String>();
		for (int i = 0; i < l.size(); i++) {

			set.add(l.get(i));
		}

		ArrayList<String> list = new ArrayList<String>();
		list.addAll(set);
		if (list.get(list.size() - 1) == " ")
			list.remove(list.size() - 1);

		return list;
	}

	public ArrayList<String> readFile(String fileName) throws EOFException
	// read a data from a PPM file
	{

		ArrayList<String> list = new ArrayList<String>();
		BufferedReader reader;

		try {
			reader = new BufferedReader(new FileReader(fileName));
			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					// ...
					System.out.println(line);
					String[] tokens = line.split("");
					for (int i = 0; i < tokens.length; i++) {

						list.add(tokens[i]);

					}
					list.add("\n");

				}
				
				System.out.println(list.size() + " is the size after reading file");
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;

	}

	public void printMessage() {
		System.out.println("Message to encode is :");
		for (int i = 0; i < message.size(); i++)
			System.out.print(message.get(i));
		
		System.out.println();
	}

	public void printdictoutput() {
		System.out.println("Index\t\tEntry");
		System.out.println("-------------------");
		for (int i = 0; i < dictionary.size(); i++)
			System.out.println(i + "\t\t" + dictionary.get(i));
			
		System.out.println();
		
		System.out.println("Encoded Text:");
		for (int i = 0; i < output.size(); i++)
			System.out.print(output.get(i) + " ");
		
		System.out.println();
	}

}
