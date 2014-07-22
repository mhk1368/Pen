package net.kabulsoft.pen.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.ImageIcon;

import net.kabulsoft.pen.util.PenDiags;

import com.sahandrc.calendar.PersianCalendar;

public class StudentInfo extends DataBase {

	public int maxStudentId() {
		String q = "SELECT MAX(st_id) AS max_id FROM students";
		try {
			results = statement.executeQuery(q);
			if (results.next()) {
				int id = results.getInt(1);
				if (id < 20590)
					return 20590;
				else
					return id + 1;
			}
		} 
		catch (Exception e) {}
		return 0;
	}
	
	public boolean insertStudent(String data[]) 
	{
		String q = "INSERT INTO students (st_id,st_name,st_fname,st_lname,st_idcard,st_phone,st_province,st_address,"
				+ "st_class,st_gender,st_state,st_reg_year)VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s',%d)";
		int y = new PersianCalendar().getPersianYear();
		q = String.format(q, data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7], data[8], data[9], data[10], y);
		try {
			return (statement.executeUpdate(q) == 1);
		} 
		catch (SQLException e) 
		{
			if (e.getErrorCode() == 1062)
				PenDiags.showMsg("این کد قبلا ثبت شده است. لطفا یک کد دیگر وارد کنید!");
		}
		return false;
	}

	public Vector<Object> searchStudent(String col, String word, String sex, int grade, boolean active, int page) 
	{
		Vector<Object> students = new Vector<Object>();
		int start = (page * LIMIT) - LIMIT;

		String q = "SELECT st_id, st_name, st_fname, st_lname, st_idcard, st_province, st_class, st_reg_year, st_grad_year, st_state FROM students WHERE %s LIKE '%%%s%%' ";

		if(grade != 0) q += " AND st_class = " + grade;
		if(!sex.equals("b")) q += " AND st_gender = '" + sex + "'"; 
		if(active) q += " AND st_state = 'a' ";
		
		q += " ORDER BY st_id DESC LIMIT %d, %d";
		try {
			results = statement.executeQuery(String.format(q, col, word, start, LIMIT));
			while (results.next()) 
			{
				Vector<Object> row = new Vector<Object>();
				for (int i = 1; i < 10; i++) {
					row.add(results.getString(i));
				}
				String s = results.getString(10);
				if(s.equals("a")) row.add("برحال");
				if(s.equals("p")) row.add("غیر برحال");
				if(s.equals("g")) row.add("فارغ التحصیل");
				students.add(row);
			}
		} 
		catch (SQLException e) {}
		return students;
	}
	
	public double countStudents(String col, String word, String sex, int grade, boolean active)
	{
		String q = "SELECT COUNT(st_id) as sts FROM students WHERE %s like '%%%s%%'";

		if (grade != 0) q += " AND st_class = " + grade;
		if(!sex.equals("b")) q += " AND st_gender = '" + sex + "'"; 
		if(active) q += " AND st_state = 'a' ";
		try {
			results = statement.executeQuery(String.format(q, col, word));
			if (results.next()) {
				return results.getDouble("sts");
			}
		} catch (SQLException e) {}
		return 0;
	}

	public Vector<String> findStudent(int id) 
	{
		String q = "select * from students where st_id = " + id;
		Vector<String> v = new Vector<>();
		try {
			results = statement.executeQuery(q);
			if (results.next()) {
				for (int i = 1; i < 12; i++) {
					v.add(results.getString(i));
				}
				return v;
			}
		} 
		catch (SQLException e) {}
		return null;
	}
	
	public int findGrade(int id){
		String q = "SELECT st_class , st_state FROM students WHERE st_id = " + id;
		try{
			results = statement.executeQuery(q);
			if(results.next()){
				return (results.getString("st_state").equals("g"))? 13 : results.getInt("st_class");
			}
		}
		catch(SQLException e){}
		return 0;
	}
	
	public ImageIcon findImage(int id)
	{
		String q = "SELECT st_image FROM students WHERE st_id = " + id;
		try{
			results = statement.executeQuery(q);
			if(results.next()){
				byte [] data = results.getBytes("st_image");
				if(data != null){
					return new ImageIcon(data);
				}
			}
		}
		catch(SQLException e){}
		return null;
	}
	
	public Vector<Integer> marksYears(int g, int s){
		Vector<Integer> years = new Vector<>();
		String q = "SELECT DISTINCT mark_year FROM marks , subjects WHERE sub_id = mark_sub_id AND sub_class = %d AND mark_st_id = %d ORDER BY mark_year DESC";
		try{
			results = statement.executeQuery(q = String.format(q, g, s));
			while(results.next()){
				years.add(results.getInt("mark_year"));
			}
		}
		catch(SQLException e){}
		return years;
	}
	
	public int markYear(int g, int s, int cg){
		String q = "SELECT mark_year FROM marks , subjects WHERE mark_sub_id = sub_id AND sub_class = %d "
				+ "AND mark_st_id = %d ORDER BY mark_year DESC LIMIT 1";
		q = String.format(q, g, s);
		try{
			results = statement.executeQuery(q);
			if(results.next()){
				return results.getInt("mark_year");
			}
		}catch(SQLException e){}
		int y = new PersianCalendar().getPersianYear();
		return y - (cg - g);
	}
	
	public Vector<Object> studentMarks(int s, int y, int g)
	{
		Vector<Object> marks = new Vector<>();
		String q = "SELECT sub_id , sub_name , mark_half , mark_total , IFNULL(mark_half,0)+IFNULL(mark_total,0) AS total , mark_second FROM subjects LEFT JOIN marks "
				+ "ON sub_id = mark_sub_id AND mark_year = %d AND mark_st_id = %d WHERE sub_class = %d ORDER BY sub_id";
		try{
			results = statement.executeQuery(String.format(q, y, s, g));
			while(results.next()){
				Vector<Object> v = new Vector<>();
				v.add(results.getInt("sub_id"));
				v.add(results.getString("sub_name"));
				v.add(results.getObject("mark_half"));
				v.add(results.getObject("mark_total"));
				v.add(results.getObject("total"));
				v.add(results.getObject("mark_second"));
				marks.add(v);
			}
		}catch(SQLException e){}
		return marks;
	}
	
	public boolean isNew(int s){
		String q1 = "SELECT mark_st_id FROM marks WHERE mark_st_id = %d LIMIT 1";
		String q2 = "SELECT mem_st_id FROM membership WHERE mem_st_id = %d LIMIT 1";
		try{
			results = statement.executeQuery(String.format(q1, s));
			if(results.next()){
				return false;
			}
			results = statement.executeQuery(String.format(q2, s));
			if(results.next()){
				return false;
			}
		}catch(SQLException e){}
		return true;
	}
	
	public boolean saveMark(int st, int sub, float mark)
	{
		String q1 = "UPDATE marks SET mark_half = 40 , mark_total = %f WHERE mark_year = %d AND mark_st_id = %d AND mark_sub_id = %d";
		String q2 = "INSERT INTO marks (mark_st_id , mark_sub_id , mark_year , mark_half , mark_total) VALUES (%d, %d, %d, 40, %f)";
		int y = new PersianCalendar().getPersianYear();
		try{
			if (statement.executeUpdate(String.format(q1, mark-40, y, st, sub)) == 1){
				return true;
			}else{
				return statement.executeUpdate(String.format(q2, st, sub, y, mark-40)) == 1;
			}
		}
		catch(SQLException e){}
		return false;
	}
	
	public boolean upgradeStudent(int s, int g)
	{
		int c = (g > 12)? 12 : g;
		String q1 = "UPDATE students SET st_class = %d WHERE st_id = %d";
		try{
			return statement.executeUpdate(String.format(q1, c, s)) == 1;
		}
		catch(SQLException e){}
		return false;
	}

	public boolean editStudent(String data[]) 
	{
		String q = "UPDATE students SET st_name = '%s', st_fname = '%s', st_lname = '%s', st_idcard = '%s', st_phone = '%s',"
				+ " st_province = '%s', st_address = '%s', st_class = '%s', st_gender = '%s', st_state = '%s' WHERE st_id = %s";
		q = String.format(q, data[1], data[2], data[3], data[4], data[5], data[6], data[7], data[8], data[9], data[10], data[0]);
		try {
			return statement.executeUpdate(q) == 1;
		} 
		catch (Exception e) {}
		return false;
	}
	
	public boolean saveImage(byte[] image, int id)
	{
		if(image == null){
			return false;
		}
		String q = "UPDATE students SET st_image = ? WHERE st_id = ?";
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

	public boolean deleteStudent(String id) 
	{
		String q = "delete from students where st_id =" + id;
		try {
			return (statement.executeUpdate(q) == 1);
		} 
		catch (Exception e) {}
		return false;
	}
	
	public Vector<Object> transferredStudents(String word, int page)
	{
		int start = (page * LIMIT) - LIMIT;
		Vector<Object> data = new Vector<>();
		String q = "SELECT st_id, st_name, trans_grade, trans_year, trans_desc FROM students, student_transfer "
				+ "WHERE trans_st_id = st_id AND (st_id LIKE '%%%s%%' OR st_name LIKE '%%%s%%') ORDER BY trans_year DESC LIMIT %d, %d";
		try{
			results = statement.executeQuery(String.format(q, word, word, start, LIMIT));
			while(results.next()){
				Vector<String> rec = new Vector<>();
				rec.add(results.getString("st_id"));
				rec.add(results.getString("st_name"));
				rec.add(results.getString("trans_grade"));
				rec.add(results.getString("trans_year"));
				rec.add(results.getString("trans_desc"));
				data.add(rec);
			}
		}
		catch(SQLException e){}
		return data;
	}
	
	public int countTransform(String word)
	{
		String q = "SELECT COUNT(st_id) as num FROM students, student_transfer WHERE trans_st_id = st_id AND (st_id LIKE '%%%s%%' OR st_name LIKE '%%%s%%')";
		try{
			results = statement.executeQuery(String.format(q, word, word));
			if(results.next()){
				return results.getInt("num");
			}
		}
		catch(SQLException e){}
		return 0;
	}
	
	public boolean transferStudent(int id, String g, String desc)
	{
		String q1 = "INSERT INTO student_transfer (trans_st_id , trans_grade , trans_year , trans_desc) VALUES (%d, %s, %d ,'%s')";
		String q2 = "UPDATE students SET st_state = 'p' WHERE st_id = " + id;
		int y = new PersianCalendar().getPersianYear();
		try{
			if(editTransformDescr(String.valueOf(id), String.valueOf(y), desc)){
				return statement.executeUpdate(q2) == 1;
			}
			else if(statement.executeUpdate(String.format(q1, id, g, y, desc)) == 1){
				return statement.executeUpdate(q2) == 1;
			}
		}
		catch(SQLException e){}
		return false;
	}
	
	public boolean editTransformDescr(String id, String y, String desc)
	{
		String q = "UPDATE student_transfer SET trans_desc = '%s' WHERE trans_st_id = %s AND trans_year = %s";
		try{
			return statement.executeUpdate(String.format(q, desc, id, y)) == 1;
		}
		catch(SQLException e){}
		return false;
	}
	
	public boolean deleteTransform(String id, String y)
	{
		String q = "DELETE FROM student_transfer WHERE trans_st_id = %s AND trans_year = %s";
		try{
			return statement.executeUpdate(String.format(q, id, y)) == 1;
		}
		catch(SQLException e) {}
		return false;
	}
	
	public ResultSet marks(int id, int g, int y)
	{
		String q = "SELECT sub_id , sub_name , IFNULL(mark_half,0) AS half , IFNULL(mark_total,0) AS total , IFNULL(mark_half+mark_total,0) AS second FROM subjects "
				+ "LEFT JOIN marks ON sub_id = mark_sub_id AND mark_year = %d AND mark_st_id = %d WHERE sub_class = %d ORDER BY sub_id";
		try {
			return statement.executeQuery(String.format(q, y, id, g));
		} 
		catch (SQLException e) {}
		return null;
	}
}
