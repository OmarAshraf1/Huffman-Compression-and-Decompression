import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.swing.filechooser.FileSystemView;

public class main2 {
	public static void main(String[] args) throws IOException {
		String cOrd = args[0] ;
		if(cOrd.charAt(0) == 'c' ) {
			String FilePath = args[1] ;
			int n =  Integer.parseInt(args[2]) ;
			//get the directory path		
			
			File f = new File(FilePath);
			long orgsize = f.length();
			
			String parent = f.getParent();
			String filename = f.getName() ;
			String outputpath = "" ;
			if(parent != null) {
				outputpath = parent + "/18011111." + args[2] +"."+ filename + ".hc";
			}
			else {
				outputpath ="18011111." + args[2] +"."+ filename + ".hc";
			}
			
			 
			
			Compress compress = new Compress();
			
			long startTime = System.nanoTime();
			compress.Compress(FilePath,outputpath, n);	
			long stopTime = System.nanoTime();
			long time = stopTime - startTime ;
			long seconds = TimeUnit.SECONDS.
                    convert(time, 
                            TimeUnit.NANOSECONDS);
			long min = seconds / 60 ;
			
			long  encodedsize = new File(outputpath).length();
			double ratio =  ((double) orgsize /(double) encodedsize) ;
			System.out.println("Compression ratio :" + ratio);
			System.out.println("Time in nano second: " + time + " /Seconds:" + seconds + "/ Minutes: " + min);
			
		}
		if(cOrd.charAt(0) == 'd') {
			String FilePath = args[1] ;
			File f = new File(FilePath);
			String parent = f.getParent();
			File path = new File(parent) ;
			String filename = f.getName() ;
			String output = "" ;	
		
			if(parent != null) {
				int tmp = filename.lastIndexOf('.');
				String s = filename.substring(0, tmp) ;
				output = parent + "/extracted." + s ;
			}
			else {
				int tmp = filename.lastIndexOf('.');
				String s = filename.substring(0, tmp) ;
				output = "extracted." + s ;
			}
			Decompress decompress = new Decompress();
			
			long startTime = System.nanoTime();
			decompress.Decompress(FilePath,output) ;	
			long stopTime = System.nanoTime();
			long time = stopTime - startTime ;
			long seconds = TimeUnit.SECONDS.
                    convert(time, 
                            TimeUnit.NANOSECONDS);
			long min = seconds / 60 ;
			System.out.println("Time in nano second: " + time + " /Seconds:" + seconds + "/ Minutes: " + min);
			
		}
		
		
		

	}
}











