package net.kabulsoft.pen.db;

public class Employee {
	public int id;
	public String name;
	public String fname;

	public Employee() {
		this.id = 0;
		this.name = "";
	}

	public Employee(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public String toString() {
		return String.valueOf(this.name);
	}
	
	public boolean equals(Object o)
	{
		if(o instanceof Employee){
			return ((Employee)o).id == this.id;
		}
		return false;
	}
}
