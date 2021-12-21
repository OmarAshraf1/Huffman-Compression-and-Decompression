import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class Helper {
	//Method to read files,takes file path and required grouping bytes 
	public ArrayList<String> ReadFile(String filepath,int n) throws IOException{
		Path path = Paths.get(filepath) ;
		byte[] fileAsbytes = Files.readAllBytes(path);
		ArrayList<String> grouped = new ArrayList<String>() ;
		int i = 0 ;
		while(i < fileAsbytes.length) {
			String temp = "" ; 
			//get n bytes
			for (int j = 0; j < n; j++) {
				temp = temp + fileAsbytes[i] ;
				i++;
				
			}
			//push grouped bytes
			grouped.add(temp);
			
		}
		return grouped;
	}
	

}
