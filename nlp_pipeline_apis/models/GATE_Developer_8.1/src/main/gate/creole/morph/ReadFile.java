package gate.creole.morph;

import gate.util.BomStrippingInputStreamReader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: ReadFile.java </p>
 * <p>Description: This class provides methods to read the file (provided by the
 * user) and to have the read access to each and every line separately </p>
 */
public class ReadFile {

  /** Instance of BufferedReader used to read the files with UTF-8 encoding */
  private BufferedReader br;

  /** Pointer which keeps track of what line is accessible to the user */
  private int pointer = 0;

  /** Stores each line of the file as a separate String in the ArrayList */
  private List<String> data;

  /**
   * Constructor - Initialise the buffered Reader instance
   * @param fileURL URL of the file to be read
   */
  public ReadFile(URL fileURL) {

    data = new ArrayList<String>();

    try {
      br = new BomStrippingInputStreamReader(fileURL.openStream(),
              "UTF-8");
    } catch(FileNotFoundException e) {
      e.printStackTrace();
    } catch(IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Reads the file and stores each line as a separate element in the ArrayList
   * @return true if read operation is successful, false otherwise
   */
  public boolean read() {
    String text;
    try {
      text = br.readLine();
      while(text!=null) {
        data.add(text);
        text = br.readLine();
      }
      text = null;
      // file has been read, close it
      br.close();
      // now set the pointer to 0
      pointer = 0;

    } catch(IOException ie) {
      ie.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * This method tells if next line is available to read
   * @return true if line is available, false otherwise
   */
  public boolean hasNext() {
    if(data.size()>pointer) {
      return true;
    } else {
      return false;
    }
  }


  /**
   * This method gives the next available String (line from the file)
   * @return line if available, null otherwise
   */
  public String getNext() {
    if(data.size()>pointer) {
      String value = data.get(pointer);
      pointer++;
      return value;
    } else {
      return null;
    }
  }

  /**
   * Tells the position of the pointer in the file
   * @return line number where the pointer is located in the file
   */
  public int getPointer() {
    return pointer;
  }

}