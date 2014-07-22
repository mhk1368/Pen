package net.kabulsoft.pen.db;

public class Account {
	
	public int id;
	public int type;
	public String name;
	
	public Account(int id, int type, String name)
	{
		this.id = id;
		this.type = type;
		this.name = name;
	}
	
	public String toString()
	{
		return this.name;
	}
	
	public boolean equals(Object obj)
	{
		if(obj instanceof Account){
			return ((Account)obj).id == this.id;
		}
		return false;
	}
}
