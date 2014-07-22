package net.kabulsoft.pen.db;

public class Subject {
	public int code;
	public String name;
	int level;

	public Subject(int code, String name, int level) {
		this.code = code;
		this.name = name;
		this.level = level;
	}

	public String toString() {
		return this.name;
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof Subject){
			return ((Subject)obj).code == this.code;
		}
		return false;
	}
}