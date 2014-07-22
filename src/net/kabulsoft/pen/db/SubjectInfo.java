package net.kabulsoft.pen.db;

import java.sql.SQLException;
import java.util.Vector;

public class SubjectInfo extends DataBase {

	public boolean insertSubject(String name, String grade) 
	{
		String q = "insert into subjects(sub_name , sub_class)values('%s','%s')";
		q = String.format(q, name, grade);
		try {
			return (statement.executeUpdate(q) == 1);
		} catch (Exception e) {
		}
		return false;
	}

	public Vector<Subject> findSubjects(int c) 
	{
		String q = "select * from subjects where sub_class = " + c;
		Vector<Subject> list = new Vector<>();
		try {
			results = statement.executeQuery(q);
			while (results.next()) {
				list.add(new Subject(results.getInt(1), results.getString(2), results.getInt(3)));
			}
		} catch (Exception e) {}
		return list;
	}

	public Vector<Object> searchSubject(String word, String grade) 
	{
		Vector<Object> recordSet = new Vector<>();
		String q = "SELECT * FROM subjects WHERE sub_name LIKE '%%%s%%' AND sub_class = %s";
		try {
			results = statement.executeQuery(String.format(q, word, grade));
			while (results.next()) {
				Vector<String> record = new Vector<String>();
				for (int i = 1; i < 4; i++) {
					record.add(results.getString(i));
				}
				recordSet.add(record);
			}
		} catch (SQLException e) {
		}
		return recordSet;
	}

	public boolean deleteSubject(String id) 
	{
		String q = "delete from subjects where sub_id = " + id;
		try {
			return (statement.executeUpdate(q) == 1);
		} catch (SQLException e) {
		}
		return false;
	}

	public boolean editSubject(String id, String name) 
	{
		String q = "update subjects set sub_name = '%s' WHERE sub_id = %s";
		q = String.format(q, name, id);
		try {
			return (statement.executeUpdate(q) == 1);
		} catch (SQLException e) {
		}
		return false;
	}
}
