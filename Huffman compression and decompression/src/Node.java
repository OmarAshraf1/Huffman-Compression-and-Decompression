
public class Node {
	//children
	Node left = null, right = null ;
	//parsed char to be compressed
	String value ;
	//number of occurrence
	int frequency ;
	
public Node( String value, int frequency) {
		
		this.left = null;
		this.right = null;
		this.value = value;
		this.frequency = frequency;
	}
	
public Node(Node left, Node right, String value, int frequency) {
		
		this.left = left;
		this.right = right;
		this.value = value;
		this.frequency = frequency;
	}
	
}
