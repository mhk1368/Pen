package net.kabulsoft.pen.db;

public class Cash {
	
	public int id;
	public String name;
	public String cashier;
	
	public Cash(int id, String name, String cashier){
		
		this.id = id;
		this.name = name;
		this.cashier = cashier;
	}
	
	public String toString(){
		return this.name;
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof Cash){
			return ((Cash)obj).id == this.id;
		}
		return false;
	}
}
