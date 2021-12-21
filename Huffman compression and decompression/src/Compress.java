import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import javax.swing.text.html.HTMLDocument.Iterator;

public class Compress {
	//min heap to store the tree, sorted by number of occurrence 
	//so least occurred nodes are in the first and can be 
	//connected first, so the node with maximum occurrence 
	//has smaller compressed code size, otherwise has larger
	//as they occur less, which lead to smaller file size 
	PriorityQueue<Node> minHeap = new PriorityQueue<Node>( (a,b) -> a.frequency - b.frequency ) ;
	
	//HashMap to map each unique bytes to their frequency
	Map<String, Integer> FreqMap = new HashMap<String, Integer>() ;
	
	//Map to map codes and their value
	Map<String, String> CodeMap = new HashMap<String, String>() ;
	
	
	Node root ;
	
	
	
	int nbytes;
	String infilepath ;
	String output  ;
	//Method to read files,takes file path and required grouping bytes 
		public void ReadFile(String filepath,int n) throws IOException{
			
			FileInputStream fin=new FileInputStream(filepath);  
			
			BufferedInputStream bin=new BufferedInputStream(fin);    
			int i = 0 ;
			while((i=bin.read())!=-1){    
			    String temp = "" ;
			    temp = temp + (char) i ; 
			    if(n>1) { //read n bytes
			    	for (int j = 1; j < n; j++) {
						
						i = bin.read();
						if(i == -1 ) {
							break ;
						}
						
						temp = temp + (char) i ; 
					}
			    }
			 	
			 	FreqMap.put(temp, FreqMap.getOrDefault(temp, 0) + 1);
			}    
			
			bin.close();
			
		}
	
	//Compress method
	public void Compress(String filepath,String outputpath,int n) throws IOException {
		nbytes = n ;
		infilepath = filepath;
		output = outputpath ;
		//read groups of bytes and put them in the freq map
		ReadFile(filepath, nbytes);
	
		//build the tree
		BuildTree();
		
		//get the codes from the tree
		GenerateCode(root, "");
		//write header function maps to freq table	
		//write the compressed file 
		WriteCompressedFile();
		
		
		
	}
	
	
	//Method to build the tree
	public void BuildTree() {
		//Iterate over frequency table and build heap
		for(Map.Entry<String, Integer> entry : FreqMap.entrySet()) {
			String value = entry.getKey() ;
			int freq = entry.getValue();
			//intialize node
			Node node = new Node(value, freq);
			minHeap.add(node);
		}
		//Build tree where the parent node is the parent of the least two nodes' frequency
		//until we have only one root of the tree
		while(minHeap.size() > 1) {
			//pop the 2 least node
			Node min1 = minHeap.poll();
			Node min2 = minHeap.poll(); 
			int newfreq = min1.frequency + min2.frequency ;
			//new node and put it in the heap
			Node newNode = new Node(min1, min2, null, newfreq);
			minHeap.add(newNode);
		}
		
		root = minHeap.peek();
	}
	//generate codes from the tree
	public void GenerateCode(Node root,String path) {
		if(root==null) {
			return;
		}
		//if leaf
		if(root.left == null && root.right == null) {
			//value and unique path(code)
			CodeMap.put(root.value, path);
		}
		
		GenerateCode(root.left, path + '0');
		GenerateCode(root.right, path + '1');
			
	}
	public void WriteCompressedFile() throws IOException {
		FileOutputStream fout=new FileOutputStream(output,true);     
	    BufferedOutputStream bout=new BufferedOutputStream(fout);    
		//The grouping value so in decompression we can map the unique values of the original file
		
	    ByteBuffer bb = ByteBuffer.allocate(4); 
	    bb.putInt(nbytes);
	    byte[] barr = bb.array();
	    //write int as 4 bytes
	    for (int i = 0; i < 4; i++) {
			bout.write(barr[i]);
		}
	   
		
		//now write the huffman tree as a header
		WriteHeader(bout, root);
		
		//write the codes
		WriteCodes(bout);
		bout.close();
		
	}
	//Method to write header
	public void WriteHeader(BufferedOutputStream fos, Node root ) throws IOException {
		//if leaf we mark it by one else by zero
		//We use Preorder traversal so we traverse the tree in sorted way 
		//so when decode we get the same sorted nodes by frequency
		if(root.left ==null && root.right == null) {
			
			//mark by one
			fos.write(1);
			//write the code 8 bits
			//System.out.println(Integer.parseInt(root.value));
			
			//byte bits = root.value.getBytes();
			//Write nbytes groped
			String OrgValue = root.value ;
			
			for (int i = 0; i < root.value.length(); i++) {
			
				char c = OrgValue.charAt(i);
				int x = c ;
				fos.write(x);
			}
			if(OrgValue.length() < nbytes) {
				
				//remaining bytes 
				int m = nbytes - OrgValue.length();
				for (int i = 0; i < m; i++) {
					
					fos.write(0);
				}				
			}		
		}
		else {//not leaf
			
			fos.write(0);
			WriteHeader(fos, root.left);
			WriteHeader(fos, root.right); 
			
		}
	}
	//Write codes
	public void WriteCodes(BufferedOutputStream fos) throws IOException {
		String buffer = "" ;
		FileInputStream fin=new FileInputStream(infilepath);    
		BufferedInputStream bin=new BufferedInputStream(fin);   
		int i = 0 ;
		//read n grouped bytes from org file
		while((i=bin.read())!=-1){    
			//get code that maps to the n bytes
			
			String temp = "" ;
			temp = temp + (char) i ; 
			if(nbytes>1) { //read n bytes
		    	for (int j = 1; j < nbytes; j++) {
					
					i = bin.read();
					if(i == -1 ) {
						break ;
					}
					
					temp = temp + (char) i ; 
				}
		    }
			
			String code = CodeMap.get(temp) ;
			
			String currCode = buffer + code ;
			
			int len = currCode.length();
			//if the code is greater than 8 bits
			//if len = 16 , 16 - 16%8 = 16 so we need to write 2 bytes
			int mbytes = len - len%8 ;
			if(len<8) {
				buffer = currCode;
			}
			else {
				int l = 0;
				int r = 8 ;
				while(r <= mbytes) {
					//as all are 0 or 1
					//get 8 bits to write 1 byte so we dont write every bit as a byte
					byte bits = (byte) Integer.parseInt(currCode.substring(l, r),2) ;
					//update pointers to get next 8 bits
					l = r ;
					r+=8 ;
					
					fos.write(bits);
				}
				//to get any rest bits if len != factor of  8
				buffer = currCode.substring(mbytes, len);
			}
		}
		/*
	if(!buffer.isEmpty()) {
		int l = 0;
		int r = Math.min(buffer.length(), 8) ;
		while(r < buffer.length()) {
			//as all are 0 or 1
			byte bits = (byte) Integer.parseInt(buffer.substring(l, r),2) ;
			//update pointers to get next 8 bits
			l = r ;
			r+=8 ;
			fos.write(bits);
		}
	}
	*/
		bin.close();
	}
	
	
}















