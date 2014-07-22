package net.kabulsoft.pen.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.ImageIcon;

public class EmployeeInfo extends DataBase {

	public int maxEmployeeId()
	{
		try{
			results = statement.executeQuery("SELECT MAX(emp_id) AS maxid FROM employee");
			if(results.next()){
				int id = results.getInt("maxid");
				if(id < 2020){
					return 2020;
				}
				else{
					return id + 1;
				}
			}
		}
		catch(SQLException e){}
		return 0;
	}
	
	public boolean insertEmployee(String [] data)
	{
		String q = "INSERT INTO employee (emp_id, emp_name, emp_fname, emp_idcard, emp_phone, emp_address) VALUES (%s, '%s', '%s', '%s', '%s', '%s')";
		q = String.format(q, data[0], data[1], data[2], data[3], data[4], data[5]);
		try{
			return statement.executeUpdate(q) == 1;
		}
		catch(SQLException e){}
		return false;
	}
	
	public boolean saveImage(byte[] image, int id)
	{
		if(image == null){
			return false;
		}
		String q = "UPDATE employee SET emp_image = ? WHERE emp_id = ?";
		try 
		{
			PreparedStatement pst = connection.prepareStatement(q);
			pst.setBytes(1, image);
			pst.setInt(2, id);
			return (pst.executeUpdate() == 1);
		} 
		catch (SQLException e) {}
		return false;
	}
	
	public Vector<Object> searchEmployee(String col, String word, int page)
	{
		Vector<Object> employees = new Vector<>();
		int start = (page * LIMIT) - LIMIT;
		String q = "SELECT * FROM employee WHERE %s LIKE '%%%s%%' ORDER BY emp_id DESC LIMIT %d , %d";
		try{
			results = statement.executeQuery(String.format(q, col, word, start, LIMIT));
			while(results.next()){
				Vector<String> row = new Vector<>();
				row.add(results.getString("emp_id"));
				row.add(results.getString("emp_name"));
				row.add(results.getString("emp_fname"));
				row.add(results.getString("emp_idcard"));
				row.add(results.getString("emp_phone"));
				employees.add(row);
			}
		}
		catch(SQLException e){}
		return employees;
	}
	
	public double countEmployee(String col, String word)
	{
		String q = "SELECT COUNT(emp_id) AS num FROM employee WHERE %s LIKE '%%%s%%'";
		try{
			results = statement.executeQuery(String.format(q, col, word));
			if(results.next()){
				return results.getDouble(1);
			}
		}
		catch(SQLException e){}
		return 0;
	}
	
	public String[] findEmployee(int id) {
		String values[] = new String[6];
		String q = "select * from employee where emp_id = " + id;
		try {
			results = statement.executeQuery(q);
			if (results.next()) {
				for (int i = 0; i < values.length; i++) {
					values[i] = results.getString(i + 1);
				}
			}
		} 
		catch (SQLException e) {}
		return values;
	}
	
	public ImageIcon findImage(int id)
	{
		String q = "SELECT emp_image FROM employee WHERE emp_id = " + id;
		try{
			results = statement.executeQuery(q);
			if(results.next()){
				byte [] data = results.getBytes("emp_image");
				if(data != null){
					return new ImageIcon(data);
				}
			}
		}
		catch(SQLException e){}
		return null;
	}
	
	public boolean editEmployee(String data[]) 
	{
		String q = "UPDATE employee SET emp_name = '%s', emp_fname = '%s', emp_idcard = '%s', emp_phone = '%s', emp_address = '%s' WHERE emp_id = %s";
		q = String.format(q, data[1], data[2], data[3], data[4], data[5], data[0]);
		try {
			return (statement.executeUpdate(q) == 1);
		} catch (SQLException e) {
		}
		return false;
	}
	
	public boolean deleteEmployee(String id) 
	{
		String q = "DELETE FROM employee WHERE emp_id = " + id;
		try {
			return (statement.executeUpdate(q) == 1);
		} catch (SQLException e) {
		}
		return false;
	}
	
	public Vector<Employee> searchByName(String word)
	{
		Vector<Employee> data = new Vector<>();
		
		String q = "SELECT emp_id, emp_name FROM employee WHERE emp_name LIKE '%%%s%%' LIMIT 15";
		
		try{
			results = statement.executeQuery(String.format(q, word));
			while(results.next()){
				Employee emp = new Employee(results.getInt("emp_id"), results.getString("emp_name"));
				data.add(emp);
			}
		}
		catch(SQLException e){}
		return data;
	}
}




















