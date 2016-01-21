package experiments;



public class Test {

	public static void main(String[] args) {
		System.out.println((char) 42);
		int i = 242;
		System.out.println(i / 100);
		System.out.println(i / 10 - (i / 100) * 10);
		System.out.println(i - (i / 100) * 100 - ((i / 10) * 10 - (i / 100) * 100));
	}
	
}
