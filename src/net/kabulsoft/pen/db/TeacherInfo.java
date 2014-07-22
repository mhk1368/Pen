package net.kabulsoft.pen.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.ImageIcon;

import com.sahandrc.calendar.PersianCalendar;

public class TeacherInfo extends DataBase{
	
	public int maxTeacherId() {
		String q = "select max(tc_id) as maxid from teachers";
		try {
			results = statement.executeQuery(q);
			if (results.next()) {
				int id = results.getInt(1);
				if (id < 1010)
					return 1010;
				else
					return id + 1;
			}
		} catch (Exception e) {
		} finally {
		}
		return 0;
	}

	public boolean insertTeacher(String data[]) 
	{
		String q = "INSERT INTO teachers (tc_id, tc_name, tc_fname, tc_idcard, tc_phone, tc_address) VALUES ('%s','%s','%s','%s','%s','%s')";
		q = String.format(q, data[0], data[1], data[2], data[3], data[4], data[5]);
		try {
			return (statement.executeUpdate(q) == 1);
		} 
		catch (SQLException e) {} 
		return false;
	}

	public Vector<Object> searchTeacher(String col, String word, int page)
	{
		int start = (page * 3) - 3;
		Vector<Object> result = new Vector<Object>();
		String q = "SELECT * FROM teachers WHERE %s LIKE '%%%s%%' ORDER BY tc_id DESC LIMIT %d, 3";
		try {
			results = statement.executeQuery(String.format(q, col, word, start));
			while (results.next()) {
				Vector<Object> record = new Vector<Object>();
				for (int i = 1; i < 6; i++) {
					record.add(results.getString(i));
				}
				result.add(record);
			}
		} 
		catch (SQLException e) {}
		return result;
	}
	
	public double countTeachers(String col, String word)
	{
		String q = "SELECT COUNT(tc_id) AS num FROM teachers WHERE " + col + " LIKE '%" + word + "%'";
		try{
			results = statement.executeQuery(q);
			if(results.next()){
				return results.getDouble("num");
			}
		}
		catch(SQLException e){}
		return 0;
	}

	public boolean deleteTeacher(String id) {
		String q = "delete from teachers where tc_id = " + id;
		try {
			return (statement.executeUpdate(q) == 1);
		} catch (SQLException e) {
		}
		return false;
	}

	public String[] findTeacher(int id) {
		String values[] = new String[6];
		String q = "select * from teachers where tc_id = " + id;
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

	public boolean editTeacher(String data[]) 
	{
		String q = "UPDATE teachers SET tc_name = '%s', tc_fname = '%s', tc_idcard = '%s', tc_phone = '%s', tc_address = '%s' WHERE tc_id = %s";
		q = String.format(q, data[1], data[2], data[3], data[4], data[5], data[0]);
		try {
			return (statement.executeUpdate(q) == 1);
		} catch (SQLException e) {
		}
		return false;
	}
	
	public boolean saveImage(byte[] image, int id)
	{
		if(image == null){
			return false;
		}
		String q = "UPDATE teachers SET tc_image = ? WHERE tc_id = ?";
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
	
	public ImageIcon findImage(int id)
	{
		String q = "SELECT tc_image FROM teachers WHERE tc_id = " + id;
		try{
			results = statement.executeQuery(q);
			if(results.next()){
				byte [] data = results.getBytes("tc_image");
				if(data != null){
					return new ImageIcon(data);
				}
			}
		}
		catch(SQLException e){}
		return null;
	}

	public Vector<Teacher> allTeachers() 
	{
		Vector<Teacher> teachers = new Vector<>();
		String q = "SELECT tc_id , tc_name FROM teachers";
		try {
			results = statement.executeQuery(q);
			while (results.next()) {
				teachers.add(new Teacher(results.getInt(1), results.getString(2)));
			}
		} catch (SQLException e) {
		}
		return teachers;
	}
	
	public Vector<Teacher> searchByName(String word)
	{
		Vector<Teacher> data = new Vector<>();
		
		String q = "SELECT tc_id, tc_name FROM teachers WHERE tc_name LIKE '%%%s%%' LIMIT 15";
		
		try{
			results = statement.executeQuery(String.format(q, word));
			while(results.next()){
				Teacher tc = new Teacher(results.getInt("tc_id"), results.getString("tc_name"));
				data.add(tc);
			}
		}
		catch(SQLException e){}
		return data;
	}

	public Teacher subjTeacher(int subj, int level, int year, String name) 
	{
		String q = "SELECT tc_id, tc_name FROM classes, course_teacher, teachers "
				+ " WHERE class_id = course_class_id AND course_tc_id = tc_id "
				+ " AND class_year = %d  AND class_level = %d  AND course_sub_id = %d  AND class_name = '%s'";
		try {
			results = statement.executeQuery(String.format(q, year, level, subj, name));
			if (results.next()) {
				return new Teacher(results.getInt(1), results.getString(2));
			}
		} catch (SQLException e) {
		}
		return null;
	}

	public boolean teacherAssign(int t, int c, int s) {

		String q1 = "UPDATE course_teacher SET course_tc_id =%d WHERE course_class_id =%d  AND course_sub_id =%d ";
		String q2 = "INSERT INTO course_teacher (course_class_id, course_sub_id, course_tc_id) VALUES (%d,%d,%d)";

		try {
			if (statement.executeUpdate(String.format(q1, t, c, s)) == 0) {
				return (statement.executeUpdate(String.format(q2, c, s, t)) == 1);
			} else {
				return true;
			}
		} catch (SQLException e) {
		}
		return false;
	}
	
	public Vector<String> schedule(int tc, int day, int sh)
	{
		int y = new PersianCalendar().getPersianYear();
		Vector<String> items = new Vector<>();
		String q = "SELECT class_id , class_level , class_name , sch_time FROM classes , course_schedule , course_teacher "
				+ "WHERE class_id = course_class_id AND class_id = sch_course_id AND sch_sub_id = course_sub_id "
				+ "AND course_tc_id = %d AND sch_day = %d AND class_year = %d AND class_shift = %d ORDER BY sch_time";
		try{
			results = statement.executeQuery(String.format(q, tc, day, y, sh));
			if(!results.next()){
				return items;
			}
			for(int i=1; i<8; i++){
				if(results.getInt("sch_time") == i){
					String item = results.getString("class_level") + " - " + results.getString("class_name");
					items.add(item);
					if(!results.next()){
						return items;
					}
				}
				else{
					items.add(null);
				}
			}
		}
		catch(SQLException e){}
		return items;
	}
}