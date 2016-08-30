package nlp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by synerzip 
 */
public class Main {
    /**
     * Main Function that starts calling all modules. Loads all modules data sets
     * @param args
     */
    public static void main(String[] args) {
        String input = getInput(args);
        if(input!=null){
	        String output = new NLP().parseInput(input);
	        System.out.println(output);
	    }else{
	    	System.out.println("FILE NOT FOUND. PLEASE CHECK WHETHER FILE PATH IS ACCURATE.");
	    }
    }

    /**
     * Method that check whether a filename is passed or not. If yes, it reads the file else it accepts sentences from user and returns the same
     * @param args
     * @return
     */
    private static String getInput(String[] args){
        String input;
        if(args.length==0){
            System.out.println("Enter the Sentences: ");
            Scanner scanner = new Scanner(System.in);
            input = scanner.nextLine();
            scanner.close();
        }else{
            input = getDataFromFile(args[0]);
        }
        return input;
    }

    /**
     * Method to read data from the file and return a String of that data
     * @param fileName
     * @return
     */
	private static String getDataFromFile(String fileName){
        BufferedReader bufferedReader;
        String text = "";
        String line;
        try {
            bufferedReader = new BufferedReader(new FileReader(fileName));
            while ((line = bufferedReader.readLine()) != null) {
                text += line + " ";
            }
            text = text.trim();
        }catch (FileNotFoundException e) {
        	text = null;
            e.printStackTrace();
        }catch (IOException e) {
        	text = null;
            e.printStackTrace();
        }
        return text;
    }
}
