package net.kabulsoft.pen.db;

import java.sql.SQLException;
import java.util.Vector;

import net.kabulsoft.pen.util.PenDiags;

import com.sahandrc.calendar.PersianCalendar;

public class CourseInfo extends DataBase {

	public Vector<Object> searchCourse(int y, int g) {
		Vector<Object> classes = new Vector<>();

		String q = "select * from classes where class_year = %d and class_level = %d";
		try {
			results = statement.executeQuery(String.format(q, y, g));
			while (results.next()) {
				Vector<Object> record = new Vector<>();
				record.add(results.getInt("class_id"));
				record.add(results.getInt("class_year"));
				record.add(results.getInt("class_level"));
				record.add(results.getString("class_name"));
				record.add(results.getInt("class_shift"));
				classes.add(record);
			}
		} catch (SQLException e) {}

		return classes;
	}
	
	public Vector<Course> searchClass(int y, int g) 
	{
		Vector<Course> classes = new Vector<>();

		String q = "select * from classes where class_year = %d and class_level = %d";
		try {
			results = statement.executeQuery(String.format(q, y, g));
			while (results.next()) 
			{
				int id = results.getInt("class_id");
				int year = results.getInt("class_year");
				int grade = results.getInt("class_level");
				String name = results.getString("class_name");
				Course group = new Course(id, year, grade, name);
				classes.add(group);
			}
		} 
		catch (SQLException e) {}
		return classes;
	}

	public Course findCourse(int cid) {
		String q = "select * from classes where class_id = " + cid;
		try {
			results = statement.executeQuery(q);
			if (results.next()) {
				int id = results.getInt("class_id");
				int year = results.getInt("class_year");
				int grade = results.getInt("class_level");
				int shift = results.getInt("class_shift");
				String name = results.getString("class_name");
				return new Course(id, year, grade, shift, name);
			}
		} catch (SQLException e) {}

		return null;
	}
	
	public Course studentCourse(int s, int g, int y){
		String q = "SELECT classes.* FROM classes , membership WHERE class_id = mem_class_id AND class_year = %d AND class_level = %d AND mem_st_id = %d";
		q = String.format(q, y, g, s);
		try{
			results = statement.executeQuery(q);
			if(results.next()){
				int id = results.getInt("class_id");
				int year = results.getInt("class_year");
				int grade = results.getInt("class_level");
				String name = results.getString("class_name");
				return new Course(id, year, grade, name);
			}
		}
		catch(SQLException e){}
		return null;
	}

	public Course lastCourse() {
		String q = "SELECT MAX(class_id) FROM classes";
		try {
			results = statement.executeQuery(q);
			if (results.next()) {
				return findCourse(results.getInt(1));
			}
		} catch (SQLException e) {
		}
		return null;
	}

	public boolean addCourse(String y, String g, String n, String s) {
		String q = "INSERT INTO classes (class_year, class_level, class_name, class_shift) VALUES (%s, %s, '%s', %s)";
		try {
			if (statement.executeUpdate(String.format(q, y, g, n, s)) == 1) {
				return true;
			}
		} catch (SQLException e) {
		}
		return false;
	}
	
	public boolean editCourse(String id, String name)
	{
		String q = "UPDATE classes SET class_name = '%s' WHERE class_id = %s";
		try{
			return statement.executeUpdate(String.format(q, name, id)) == 1;
		}
		catch(SQLException e){}
		return false;
	}

	public boolean deleteCourse(String id) {
		String q = "DELETE FROM classes WHERE class_id = " + id;
		try {
			if (statement.executeUpdate(q) == 1) {
				return true;
			}
		} catch (SQLException e) {
		}
		return false;
	}
	
	public Vector<Object> allMembers(int cid, int sub, int y, String word)
	{
		String q = "SELECT st_id , st_name, st_fname, st_lname, mem_attendance, mark_half, mark_total, IFNULL(mark_half,0)+IFNULL(mark_total,0) AS total, mark_second FROM students , membership LEFT JOIN marks "
				+ "ON mem_st_id = mark_st_id AND mark_sub_id = %d AND mark_year = %d WHERE st_id = mem_st_id AND mem_class_id = %d AND (st_id LIKE '%%%s%%' OR st_name LIKE '%%%s%%') ORDER BY st_id";
		Vector<Object> members = new Vector<>();
		try{
			results = statement.executeQuery(String.format(q, sub, y, cid, word, word));
			while(results.next()){
				Vector<Object> v = new Vector<>();
				v.add(results.getInt("st_id"));
				v.add(results.getString("st_name"));
				v.add(results.getString("st_fname"));
				v.add(results.getString("st_lname"));
				v.add(results.getObject("mem_attendance"));
				v.add(results.getObject("mark_half"));
				v.add(results.getObject("mark_total"));
				v.add(results.getObject("total"));
				v.add(results.getObject("mark_second"));
				members.add(v);
			}
		}catch(SQLException e){}
		return members;
	}
	
	public Vector<Object> unpassedMembers(int cid, int sub, int y, String word)
	{
		String q = "SELECT st_id , st_name, st_fname, st_lname, mem_attendance, mark_half, mark_total, IFNULL(mark_half,0)+IFNULL(mark_total,0) AS total, mark_second FROM students , membership LEFT JOIN marks "
				+ "ON mem_st_id = mark_st_id AND mark_sub_id = %d AND mark_year = %d WHERE st_id = mem_st_id AND mem_class_id = %d AND (mark_half+mark_total) < 40 AND (st_id LIKE '%%%s%%' OR st_name LIKE '%%%s%%') ORDER BY st_id";
		Vector<Object> members = new Vector<>();
		try{
			results = statement.executeQuery(String.format(q, sub, y, cid, word, word));
			while(results.next()){
				Vector<Object> v = new Vector<>();
				v.add(results.getInt("st_id"));
				v.add(results.getString("st_name"));
				v.add(results.getString("st_fname"));
				v.add(results.getString("st_lname"));
				v.add(results.getObject("mem_attendance"));
				v.add(results.getObject("mark_half"));
				v.add(results.getObject("mark_total"));
				v.add(results.getObject("total"));
				v.add(results.getObject("mark_second"));
				members.add(v);
			}
		}catch(SQLException e){}
		return members;
	}
	
	public Vector<Object> failedMembers(int cid, int sub, int y, String word)
	{
		String q = "SELECT st_id , st_name, st_fname, st_lname, mem_attendance, mark_half, mark_total, IFNULL(mark_half,0)+IFNULL(mark_total,0) AS total, mark_second FROM students , membership LEFT JOIN marks "
				+ "ON mem_st_id = mark_st_id AND mark_sub_id = %d AND mark_year = %d WHERE st_id = mem_st_id AND mem_class_id = %d AND "
				+ "(st_id IN (SELECT mark_st_id FROM membership , marks WHERE mem_class_id = %d AND mem_st_id = mark_st_id "
				+ "AND mark_year = %d AND (mark_half+mark_total) < 40 GROUP BY mark_st_id HAVING COUNT(mark_st_id) > 2 ) "
				+ "OR st_id IN (SELECT mark_st_id FROM membership , marks WHERE mem_class_id = %d AND mark_year = %d "
				+ "AND mem_st_id = mark_st_id AND (mark_half+mark_total) < 40 AND mark_second < 40)) AND (st_id LIKE '%%%s%%' OR st_name LIKE '%%%s%%') ORDER BY st_id";
		
		q = String.format(q, sub, y, cid, cid, y, cid, y, word, word);
		Vector<Object> data = new Vector<>();
		try{
			results = statement.executeQuery(q);	
			while(results.next()){
				Vector<Object> v = new Vector<>();
				v.add(results.getInt("st_id"));
				v.add(results.getString("st_name"));
				v.add(results.getString("st_fname"));
				v.add(results.getString("st_lname"));
				v.add(results.getObject("mem_attendance"));
				v.add(results.getObject("mark_half"));
				v.add(results.getObject("mark_total"));
				v.add(results.getObject("total"));
				v.add(results.getObject("mark_second"));
				data.add(v);
			}
		}
		catch(SQLException e){}
		return data;
	}
	
	public Vector<Object> passedMembers(int cid, int sub, int y, String word)
	{
		String q = "SELECT st_id , st_name, st_fname, st_lname, mem_attendance, mark_half, mark_total, IFNULL(mark_half,0)+IFNULL(mark_total,0) AS total, mark_second FROM students , membership LEFT JOIN marks "
				+ "ON mem_st_id = mark_st_id AND mark_sub_id = %d AND mark_year = %d WHERE st_id = mem_st_id AND mem_class_id = %d AND "
				+ "st_id NOT IN (SELECT mark_st_id FROM membership , marks WHERE mem_class_id = %d AND mem_st_id = mark_st_id "
				+ "AND mark_year = %d AND (mark_half+mark_total) < 40 GROUP BY mark_st_id HAVING COUNT(mark_st_id) > 2 ) "
				+ "AND st_id NOT IN (SELECT mark_st_id FROM membership , marks WHERE mem_class_id = %d AND mark_year = %d "
				+ "AND mem_st_id = mark_st_id AND (mark_half+mark_total) < 40 AND mark_second < 40) AND (st_id LIKE '%%%s%%' OR st_name LIKE '%%%s%%') ORDER BY st_id";
		
		q = String.format(q, sub, y, cid, cid, y, cid, y, word, word);
		Vector<Object> data = new Vector<>();
		try{
			results = statement.executeQuery(q);	
			while(results.next()){
				Vector<Object> v = new Vector<>();
				v.add(results.getInt("st_id"));
				v.add(results.getString("st_name"));
				v.add(results.getString("st_fname"));
				v.add(results.getString("st_lname"));
				v.add(results.getObject("mem_attendance"));
				v.add(results.getObject("mark_half"));
				v.add(results.getObject("mark_total"));
				v.add(results.getObject("total"));
				v.add(results.getObject("mark_second"));
				data.add(v);
			}
		}
		catch(SQLException e){}
		return data;
	}
	
	public boolean saveMark(int st, int sub, int y, String h, String t, String ch)
	{
		String q1 = "UPDATE marks SET mark_half = %s , mark_total = %s , mark_second = %s WHERE mark_st_id = %d AND mark_sub_id = %d AND mark_year = %d";
		String q2 = "INSERT INTO marks (mark_st_id, mark_sub_id, mark_year, mark_half, mark_total, mark_second) VALUES (%d, %d, %d, %s, %s, %s)";

		try{
			if(statement.executeUpdate(String.format(q1, h, t, ch, st, sub, y)) == 1){
				return true;
			}
			else if(statement.executeUpdate(String.format(q2, st, sub, y, h, t, ch)) == 1){	
				return true;
			}
		}catch(SQLException e){}
		return false;
	}
	
	public boolean saveAttendance(int s, int c, String a)
	{
		String q = "UPDATE membership SET mem_attendance = %s WHERE mem_st_id = %d AND mem_class_id = %d";
		try{
			return (statement.executeUpdate(String.format(q, a, s, c)) == 1);
		}catch(SQLException e){}
		return false;
	}
	
	public boolean deleteMark(int st, int sub, int y)
	{
		String q = "DELETE FROM marks WHERE mark_st_id = %d AND mark_sub_id = %d AND mark_year = %d";
		try{
			return (statement.executeUpdate(String.format(q, st, sub, y)) == 1);
		}catch(SQLException e){}
		return false;
	}
	
	public boolean assignStudent(int s, int c, int g)
	{	
		String q = "INSERT INTO membership (mem_class_id, mem_st_id) VALUES (%d, %d)";
		
		if(!isAssigned(s, g) && !isPassed(s, g) && studentGrade(s) == g){
			try{
				return (statement.executeUpdate(String.format(q, c, s)) == 1);
			}
			catch(SQLException e){}
		}else{
			PenDiags.showWarn(String.format("کد متعلم: %d. متعلم واجد شرایط عضویت در این صنف نیست!", s));
		}
		return false;
	}
	
	public boolean upgradeStudent(int s, int c, int g)
	{	
		if(isPassed(s, g-1) && !isAssigned(s, g) && studentGrade(s) < g)
		{
			String q1 = "INSERT INTO membership (mem_class_id, mem_st_id) VALUES (%d, %d)";
			String q2 = "UPDATE students SET st_class = %d WHERE st_id = %d";
			try{
				if(statement.executeUpdate(String.format(q1, c, s)) == 1)
				{
					return statement.executeUpdate(String.format(q2, g, s)) == 1;
				}
			}catch(SQLException e){}
		}
		else{
			PenDiags.showWarn(String.format("کد متعلم: %d. متعلم واجد شرایط ارتقاء نیست!", s));
		}
		return false;
	}
	
	public boolean graduateStudent(int id)
	{
		int y = new PersianCalendar().getPersianYear();
		String q = "UPDATE students SET st_state = 'g' , st_grad_year = %d WHERE st_id = %d";
		try{
			if(isPassed(id, 10) && isPassed(id, 11) && isPassed(id, 12))
			{
				return statement.executeUpdate(String.format(q, y, id)) == 1;
			}
			else{
				PenDiags.showWarn(String.format("کد متعلم: %d. متعلم واجد شرایط فراغت نیست!", id));
			}
		}
		catch(SQLException e){}
		return false;
	}
	
	public boolean switchStudent(int s, int o, int n)
	{
		String q = "UPDATE membership SET mem_class_id = %d WHERE mem_class_id = %d AND mem_st_id = %d";
		q = String.format(q, n, o, s);
		
		try{
			return (statement.executeUpdate(q) == 1);
		}catch(SQLException e){}
		
		return false;
	}
	
	public boolean detachStudent(int s, int c)
	{
		String q = "DELETE FROM membership WHERE mem_st_id = %d AND mem_class_id = %d";	
		q = String.format(q, s, c);
		try{
			if (statement.executeUpdate(q) == 1){
				return true;
			}
		}
		catch(SQLException e){}
		return false;
	}
	
	public boolean isPassed(int s, int g)
	{
		String q1 = "SELECT COUNT(sub_id) AS subs FROM subjects WHERE sub_class = " + g;
		String q2 = "SELECT COUNT(mark_st_id) AS passed FROM marks , subjects WHERE mark_sub_id = sub_id "
				+ "AND sub_class = %d AND mark_st_id = %d AND (IFNULL(mark_half,0)+IFNULL(mark_total,0) >= 40 OR mark_second >= 40) GROUP BY mark_year";
		int subs = 0;
		try{
			results = statement.executeQuery(q1);
			if(results.next()){
				subs = results.getInt("subs");
			}
			results = statement.executeQuery(String.format(q2, g, s));
			while(results.next()){
				if (results.getInt("passed") == subs){
					return true;
				}
			}
		}catch(SQLException e){}
		return false;
	}
	
	public boolean isAssigned(int s, int g)
	{
		int y = new PersianCalendar().getPersianYear();
		String q = "SELECT class_id FROM classes , membership WHERE class_id = mem_class_id "
				+ "AND class_level = %d AND class_year = %d AND mem_st_id = %d";
		q = String.format(q, g, y, s);
		try{
			results = statement.executeQuery(q);
			if(results.next()){
				return true;
			}
		}catch(SQLException e){}
		return false;
	}
	
	public int studentGrade(int id)
	{
		String q = "SELECT st_class FROM students WHERE st_id = " + id;
		try{
			results = statement.executeQuery(q);
			if(results.next()){
				return results.getInt("st_class");
			}
		}
		catch(SQLException e){}
		return 0;
	}
	
	public boolean saveSchedule(int cid, int day, int time, int sub)
	{
		String q1 = "UPDATE course_schedule SET sch_sub_id = %d WHERE sch_course_id = %d AND sch_day = %d AND sch_time = %d";
		String q2 = "INSERT INTO course_schedule (sch_course_id , sch_day , sch_time , sch_sub_id ) VALUES (%d , %d , %d , %d)";
		try{
			if(statement.executeUpdate(String.format(q1, sub, cid, day, time)) == 1){
				return true;
			}
			return statement.executeUpdate(String.format(q2, cid, day, time, sub)) == 1;
		}
		catch(SQLException e){}
		return false;
	}
	
	public boolean isTeacherFree(int cid, int day, int time, int sub, int y, int sh)
	{
		String q = "SELECT sch_course_id FROM course_schedule , course_teacher WHERE course_class_id = sch_course_id "
				+ "AND sch_course_id != %d AND sch_day = %d AND sch_time = %d AND course_tc_id = "
				+ "( SELECT course_tc_id FROM course_teacher WHERE course_class_id = %d AND course_sub_id = %d ) "
				+ "AND sch_course_id IN ( SELECT class_id FROM classes WHERE class_year = %d AND class_shift = %d )";
		q = String.format(q, cid, day, time, cid, sub, y, sh);
		try{
			results = statement.executeQuery(q);
			if(results.next()){
				return false;
			}
		}
		catch(SQLException e){}
		return true;
	}
	
	public Vector<Object> sechedule(int cid, int day)
	{
		Vector<Object> items = new Vector<>();
		String q = "SELECT sub_id , sub_name , sub_class , sch_time FROM subjects , course_schedule "
				+ "WHERE sub_id = sch_sub_id AND sch_course_id = %d AND sch_day = %d ORDER BY sch_time";
		try{
			results = statement.executeQuery(String.format(q, cid, day));
			if(!results.next()){
				return items;
			}
			for(int i=1; i<8; i++){
				if(results.getInt("sch_time") == i){
					Subject subj = new Subject(results.getInt("sub_id"), results.getString("sub_name"), results.getInt("sub_class"));
					items.add(subj);
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
