package experiments;

import model.Place;

public class Test {

	public static void main(String[] args) {
		System.out.println((char)42);
		int i = 242;
		System.out.println(i/100);
		System.out.println(i/10 - (i/100)*10);
		System.out.println(i - (i/100)*100 - ((i/10)*10 - (i/100)*100));
	}
	
	public boolean connected(Place[] places) {
		boolean result = false;
		for(Place p: places) {
			result = result || ((!board.isEmpty(p.getRow() - 1, p.getColumn())) ||
					(!board.isEmpty(p.getRow() + 1, p.getColumn())) ||
					(!board.isEmpty(p.getRow(), p.getColumn() - 1)) ||
					(!board.isEmpty(p.getRow(), p.getColumn() + 1)));
		return result;
	}
}
