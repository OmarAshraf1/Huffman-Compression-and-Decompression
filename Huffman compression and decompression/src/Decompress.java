import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class Decompress {
	//file as bytes
	
	//Huffman tree rebuilded
	Node root ;
	int nbytes;
	
	String encodedfile ;
	String outputfile;
	//takes the encoded file
	public void Decompress(String filename,String output) throws IOException {
		encodedfile = filename ;
		
		outputfile = output ;
		/*
	   File sourceFile = new File(filename);
       bytes = new byte[(int) sourceFile.length()];
       FileInputStream fis = new FileInputStream(sourceFile);
       fis.read(bytes);
       nbytes = bytes[0] ;
        */
		//read encoded file
		
        //get nbytes 
		
	
		//read header
       
		FileInputStream fin=new FileInputStream(filename);    
		BufferedInputStream bin=new BufferedInputStream(fin);
        //read n bytes
		
		byte[] barr = new byte[4] ;
		for (int i = 0; i < 4; i++) {
			barr[i] = (byte) bin.read();
		}
		
		nbytes = ByteBuffer.wrap(barr).getInt();
		//System.out.println(nbytes);
		int countHeaderBytes=0 ;
		//System.out.println(nbytes);
		//build tree
		root = ReadHeader(bin) ;
		
		//get codes from tree and write the decoded file
		GenerateOrgCode(root, bin);
		bin.close();
		//read from the file then use the data from file to get the codes from the map
		//write them in the decoded file
		//use regex by . to get the org file extension 
		

		

	}
	
	

	
	//Method to read header

	public Node ReadHeader(BufferedInputStream fis ) throws IOException {
		
		int bits = fis.read() ;
	
		if(bits == 0) {
			Node left = ReadHeader(fis) ;
			Node right = ReadHeader(fis);
			return new Node(left, right, null, 0) ;
		}
		if(bits==1)  { //leaf
			String scode = "" ;
			//read n grouped bytes
			for(int i = 0;i< nbytes;i++) {
				int r = fis.read() ;
				
				
				char code = (char) r;
				scode += code ;
				
			}
			/*
		if(countHeadBytes == HeaderByte) {
			return null ;
		}
			*/
			//System.out.println(scode); //ok
			return new Node(null, null,scode , 0) ; 
		}
		return null ;
	}
	public void GenerateOrgCode(Node root,BufferedInputStream bin) throws IOException {
		FileOutputStream fout=new FileOutputStream(outputfile,true);    
	    BufferedOutputStream bout=new BufferedOutputStream(fout); 
	    Node temp = root ;
	    int bytes;
	    //read bytes from encoded file and traverse the tree to find the original code 
	    while((bytes = bin.read()) != -1 ) {
	    	//get 1 byte -> 8 bits
	    	String binarycode = Integer.toBinaryString(bytes);
	    	//System.out.println(binarycode); ok
	    	//check if it is not 8 bits
	    	if(binarycode.length() != 8) {
	    		int remlen = 8 - binarycode.length();
	    		String rembits = "" ;
	    		while(remlen>0) {
	    			//add preceeding zeros
	    			rembits += "0" ;
	    			remlen--;
	    		}
	    		//contact with rest string
	    		binarycode = rembits + binarycode ;
	    	}
	    	//now we have 8 bits so we traverse the tree built until we reach the leaf
	    	//then we got the original code 
	    	for (int i = 0; i < binarycode.length(); i++) {
				//if 0 go left
	    		
	    		if(binarycode.charAt(i) == '0') {
					temp = temp.left ;
				}
	    		else {
	    			temp = temp.right;
	    		}
	    		//if we reach leaf so it the org code 
	    		if(temp ==null) {
	    			temp = root ;
	    			continue ;
	    		}
	    		if(temp.left==null && temp.right==null) {
	    			//write it in the decoded file
	    			//System.out.println(temp.value); 
	    			
	    			String OrgValue = temp.value ;
	    			for (int j = 0; j < OrgValue.length(); j++) {
	    				char c = OrgValue.charAt(j);
	    				int x = c ;
	    				//System.out.println(c);
		    			bout.write(x);
	    			}
	    			
	    			//repeat again for the rest of the bits
	    			temp = root ;
	    		}
			}
	    }
			bout.close();
	}


}












