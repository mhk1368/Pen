package net.kabulsoft.pen.db;

public class Teacher {
	public int id;
	public String name;
	public String fname;

	public Teacher() {
		this.id = 0;
		this.name = "";
	}

	public Teacher(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public String toString() {
		return String.valueOf(this.name);
	}
	
	public boolean equals(Object o)
	{
		if(o instanceof Teacher){
			return ((Teacher)o).id == this.id;
		}
		return false;
	}
}
