package net.kabulsoft.pen.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class FinanceInfo extends DataBase 
{
	public Vector<Object> searchCash(String w) 
	{
		String q = "SELECT * FROM cash WHERE cash_name LIKE '%" + w + "%'";
		Vector<Object> cashes = new Vector<>();
		try{
			results = statement.executeQuery(q);
			while(results.next()){
				Vector<Object> v = new Vector<>();
				v.add(results.getInt("cash_id"));
				v.add(results.getString("cash_name"));
				v.add(results.getString("cash_cashier"));
				cashes.add(v);
			}
		}
		catch(SQLException e){}
		return cashes;
	}
	
	public Vector<Cash> allCash()
	{
		Vector<Cash> cashes = new Vector<>();
		try{
			results = statement.executeQuery("SELECT * FROM cash ORDER BY cash_id");
			while(results.next()){
				Cash cash = new Cash(results.getInt("cash_id"), results.getString("cash_name"), results.getString("cash_cashier"));
				cashes.add(cash);
			}
		}
		catch(SQLException e){}
		return cashes;
	}
	
	public boolean insertCash(String n, String c)
	{
		String q = "INSERT INTO cash (cash_name , cash_cashier) VALUES ('%s','%s')";
		try{
			return (statement.executeUpdate(String.format(q, n, c)) == 1);
		}
		catch(SQLException e){}
		return false;
	}
	
	public boolean editCash(String n, String c, String id)
	{
		String q = "UPDATE cash SET cash_name = '%s' , cash_cashier = '%s' WHERE cash_id = %s";
		try{
			return (statement.executeUpdate(String.format(q, n, c, id)) == 1);
		}
		catch(SQLException e){}
		return false;
	}
	
	public boolean deleteCash(String id)
	{
		String q = "DELETE FROM cash WHERE cash_id = " + id;
		try{
			return (statement.executeUpdate(q) == 1);
		}
		catch(SQLException e){}
		return false;
	}
	
	public Vector<Object> searchAccount(String w, int type)
	{
		String q = "SELECT * FROM account WHERE account_name LIKE '%" + w + "%' ";
		if(type != 2){
			q += "AND account_type = " + type;
		}
		Vector<Object> accounts = new Vector<>();
		try{
			results = statement.executeQuery(q);
			while(results.next()){
				Vector<Object> v = new Vector<>();
				v.add(results.getInt("account_id"));
				v.add(results.getString("account_name"));
				v.add((results.getInt("account_type") == 1)? "عاید":"مصرف");
				accounts.add(v);
			}
		}
		catch(SQLException e){}
		return accounts;
	}
	
	public Vector<Account> allAccount(int type)
	{
		Vector<Account> accounts = new Vector<>();
		try{
			results = statement.executeQuery(String.format("SELECT * FROM account WHERE account_type = %d ORDER BY account_id", type));
			while(results.next()){
				Account account = new Account(results.getInt("account_id"), results.getInt("account_type"), results.getString("account_name"));
				accounts.add(account);
			}
		}
		catch(SQLException e){}
		return accounts;
	}
	
	public boolean insertAccount(String n, String t)
	{
		String q = "INSERT INTO account (account_name , account_type) VALUES ('%s','%s')";
		try{
			return (statement.executeUpdate(String.format(q, n, t)) == 1);
		}
		catch(SQLException e){}
		return false;
	}
	
	public boolean editAccount(String n, String id)
	{
		String q = "UPDATE account SET account_name = '%s' WHERE account_id = %s";
		try{
			return (statement.executeUpdate(String.format(q, n, id)) == 1);
		}
		catch(SQLException e){}
		return false;
	}
	
	public Vector<Object> studentCosts(int y)
	{
		String q = "SELECT cost_id , cost_name , price_year , IFNULL(price_amount,0) AS amount FROM student_cost LEFT JOIN price ON cost_id = price_cost_id AND price_year = %d ORDER BY cost_id DESC";
		Vector<Object> costs = new Vector<>();
		try{
			results = statement.executeQuery(String.format(q, y));
			while(results.next()){
				Vector<Object> v = new Vector<>();
				v.add(results.getInt("cost_id"));
				v.add(results.getString("cost_name"));
				v.add(results.getString("amount"));
				costs.add(v);
			}
		}
		catch(SQLException e){}
		return costs;
	}
	
	public Vector<Cost> studentCosts(int y, boolean desc){
		Vector<Cost> costs = new Vector<>();
		String q = "SELECT cost_id , cost_name , price_year , IFNULL(price_amount,0) AS amount FROM student_cost LEFT JOIN price ON cost_id = price_cost_id AND price_year = %d ORDER BY cost_id DESC";
		try{
			results = statement.executeQuery(String.format(q, y));
			while(results.next()){
				int id = results.getInt("cost_id");
				int v = results.getInt("amount");
				String n = results.getString("cost_name");
				costs.add(new Cost(id, v, n));
			}
		}
		catch(SQLException e){}
		return costs;
	}
	
	public Vector<Object> stCostCalc(int cid, int y, int m, String word)
	{
		String q = "SELECT st_id , st_name , st_fname , st_lname , IFNULL(cost_code,0) AS code , IFNULL(cost_amount,0) AS amount "
				+ "FROM membership , students  LEFT JOIN cost_calc ON st_id = cost_st_id AND cost_year = %d AND cost_month = %d "
				+ "WHERE st_id = mem_st_id AND mem_class_id = %d AND (st_id LIKE '%%%s%%' OR st_name LIKE '%%%s%%')";

		Vector<Cost> costs = studentCosts(y, true);
		Vector<Object> students = new Vector<>();	
		try{
			results = statement.executeQuery(String.format(q, y, m, cid, word, word));
			while(results.next()){
				Vector<Object> v = new Vector<>();
				v.add(results.getInt("st_id"));
				v.add(results.getString("st_name"));
				v.add(results.getString("st_fname"));
				v.add(results.getString("st_lname"));
				int code = results.getInt("code");

				for(Object cost : costs){
					int a = ((Cost)cost).id;
					double b = Math.pow(2d, (double)a);
					if(code >= b){
						v.add(true);
						code -= b;
					}else{
						v.add(false);
					}
				}
				v.add(results.getInt("amount"));
				students.add(v);
			}
		}
		catch(SQLException e){}
		return students;
	}
	
	public Vector<Object> studentsPayable(int cid, int y, int g, String word, boolean debtors)
	{
		Vector<Object> students = new Vector<>();
		String q = "SELECT st_id, st_name, st_fname, st_lname , (IFNULL(fees_amount,0) + IFNULL(cost,0)) - (IFNULL(discount_amount,0) + IFNULL(pay_value,0)) AS payable FROM students "
				+ "LEFT JOIN student_fees ON fees_level = %d AND fees_year = %d "
				+ "LEFT JOIN discount ON st_id = discount_st_id AND discount_year = %d "
				+ "LEFT JOIN student_total_cost ON st_id = cost_st_id AND cost_year = %d "
				+ "LEFT JOIN student_total_payed ON st_id = pay_st_id AND pay_year = %d, "
				+ "membership WHERE st_id = mem_st_id AND mem_class_id = %d AND (st_id LIKE '%%%s%%' OR st_name LIKE '%%%s%%') ";
		if(debtors){
			q += "HAVING payable > 0 ";
		}
		q += "ORDER BY st_id";
		
		try{
			results = statement.executeQuery(String.format(q, g, y, y, y, y, cid, word, word));
			while(results.next()){
				Vector<Object> v = new Vector<>();
				v.add(results.getInt("st_id"));
				v.add(results.getString("st_name"));
				v.add(results.getString("st_fname"));
				v.add(results.getString("st_lname"));
				v.add(results.getInt("payable"));
				students.add(v);
			}
		}
		catch(SQLException e){}
		return students;
	}
	
	public boolean insertCost(String name)
	{
		String q = "INSERT INTO student_cost (cost_name) VALUES ('%s')";
		try{
			return (statement.executeUpdate(String.format(q, name)) == 1);
		}
		catch(SQLException e){}
		return false;
	}
	
	public boolean editCost(String name, String id)
	{
		String q = "UPDATE student_cost SET cost_name = '%s' WHERE cost_id = %s";
		try{
			return (statement.executeUpdate(String.format(q, name, id)) == 1);
		}
		catch(SQLException e){}
		return false;
	}
	
	public boolean savePrice(String cost, String year, String amount)
	{
		String q1 = "UPDATE price SET price_amount = %s WHERE price_cost_id = %s AND price_year = %s";
		String q2 = "INSERT INTO price(price_cost_id , price_year , price_amount ) VALUES (%s , %s , %s)";
		String q3 = "SELECT MAX(cost_id) AS max_id FROM student_cost";
		try{
			if(cost.equals("0")){
				results = statement.executeQuery(q3);
				if(results.next()){
					String cid = results.getString("max_id");
					return (statement.executeUpdate(String.format(q2, cid, year, amount)) == 1);
				}
			}
			if(statement.executeUpdate(String.format(q1, amount, cost, year)) == 1){
				return true;
			}
			return (statement.executeUpdate(String.format(q2, cost, year, amount)) == 1);
		}
		catch(SQLException e){}
		return false;
	}
	
	public boolean saveStudentCost(int sid, int y, int m, int c, int v)
	{
		String q1 = "INSERT INTO cost_calc (cost_st_id , cost_year , cost_month , cost_code , cost_amount ) VALUES (%d , %d , %d , %d , %d)";
		String q2 = "UPDATE cost_calc SET cost_code = %d , cost_amount = %d WHERE cost_st_id = %d AND cost_year = %d AND cost_month = %d";
		q1 = String.format(q1, sid, y, m, c, v);
		q2 = String.format(q2, c, v, sid, y, m);
		try{
			if(statement.executeUpdate(q2) == 1){
				return true;
			}
			return (statement.executeUpdate(q1) == 1);
		}
		catch(SQLException e){}
		return false;
	}
	
	public Vector<Object> studentFees(int y)
	{
		String q = "SELECT * FROM student_fees WHERE fees_year = %d ORDER BY fees_level";
		Vector<Object> fees = new Vector<>();
		try{
			results = statement.executeQuery(String.format(q, y));
			boolean go = results.next();
			for(int i=1; i<13; i++){
				Vector<Object> v = new Vector<>();
				v.add(i);
				if(go){
					if(results.getInt("fees_level") == i){
						v.add(results.getInt("fees_amount"));
						go = results.next();
					}
					else{
						v.add(0);
					}
				}else{
					v.add(0);
				}
				fees.add(v);
			}
		}
		catch(SQLException e){}
		return fees;
	}
	
	public boolean saveFees(int y, int c, int v)
	{
		String q1 = "UPDATE student_fees SET fees_amount = %d WHERE fees_year = %d AND fees_level = %d";
		String q2 = "INSERT INTO student_fees (fees_year , fees_level , fees_amount) VALUES (%d , %d , %d)";
		try{
			if(statement.executeUpdate(String.format(q1, v, y, c)) == 1){
				return true;
			}
			return statement.executeUpdate(String.format(q2, y, c, v)) == 1;
		}
		catch(SQLException e){}
		return false;
	}
	
	public int findFees(int s, int y)
	{
		String q = "SELECT fees_amount FROM student_fees , membership , classes WHERE fees_year = class_year "
				+ "AND fees_level = class_level AND class_id = mem_class_id AND mem_st_id = %d AND fees_year = %d";
		try{
			results = statement.executeQuery(String.format(q, s, y));
			if(results.next()){
				return results.getInt("fees_amount");
			}
		}
		catch(SQLException e){}
		return 0;
	}
	
	public int stTotalCost(int s, int y)
	{
		String q = "SELECT SUM(cost_amount) AS total FROM cost_calc WHERE cost_st_id = %d AND cost_year = %d";
		try{
			ResultSet resp = statement.executeQuery(String.format(q, s, y));
			if(resp.next()){
				return resp.getInt("total");
			}
		}
		catch(SQLException e){}
		return 0;
	}
	
	public int stTotalPayed(int s, int y)
	{
		String q = "SELECT SUM(pay_value) AS total FROM student_payment WHERE pay_st_id = %d AND pay_year = %d";
		try{
			ResultSet resp = statement.executeQuery(String.format(q, s, y));
			if(resp.next()){
				return resp.getInt("total");
			}
		}
		catch(SQLException e){}
		return 0;
	}
	
	public Vector<Object> costsOfStudent(int s, int y, int m)
	{
		Vector<Object> theCosts = new Vector<>();
		Vector<Cost> costs = studentCosts(y, true);
		String q = "SELECT cost_code , cost_amount FROM cost_calc WHERE cost_st_id = %d AND cost_year = %d AND cost_month = %d";
		try{
			results = statement.executeQuery(String.format(q, s, y, m));
			int code = (results.next()) ? results.getInt("cost_code") : 0;
			for(Object cost : costs){
				Vector<Object> row = new Vector<>();
				row.add(cost);
				row.add(((Cost)cost).value);
				int a = ((Cost)cost).id;
				double b = Math.pow(2d, (double)a);
				if(code >= b){
					row.add(true);
					code -= b;
				}else{
					row.add(false);
				}
				theCosts.add(row);
			}
		}
		catch(SQLException e){}
		return theCosts;	
	}
	
	public int findDiscount(int id, int y){
		String q = "SELECT discount_amount FROM discount WHERE discount_st_id = %d AND discount_year = %d";
		try{
			results = statement.executeQuery(String.format(q, id, y));
			if(results.next()){
				return results.getInt("discount_amount");
			}
		}
		catch(SQLException e){}
		return 0;
	}
	
	public boolean saveDiscount(int id, int y, int v)
	{
		String q1 = "UPDATE discount SET discount_amount = %d WHERE discount_st_id = %d AND discount_year = %d";
		String q2 = "INSERT INTO discount(discount_st_id , discount_year , discount_amount ) VALUES (%d , %d , %d)";
		
		try{
			if(statement.executeUpdate(String.format(q1, v, id, y)) == 1){
				return true;
			}
			return statement.executeUpdate(String.format(q2, id, y, v)) == 1;
		}
		catch(SQLException e){}
		return false;
	}
	
	public boolean saveEmployeeReceipt(int id, int val, int cash, String date, String descr)
	{
		String q = "INSERT INTO employee_receipt (emp_rec_emp_id , emp_rec_cash_id , emp_rec_value , emp_rec_date, emp_rec_desc, emp_rec_period_id ) VALUES (%d , %d , %d , '%s', '%s', %d)";
		try{
			return statement.executeUpdate(String.format(q, id, cash, val, date, descr, PERIOD)) == 1;
		}
		catch(SQLException e){}
		return false;
	}
	
	public Vector<Object> employeeReceipts(int id, String sDate, String eDate)
	{
		Vector<Object> receipts = new Vector<>();
		String q = "SELECT emp_rec_id , emp_rec_value , pdate(emp_rec_date) AS emp_rec_date , cash_name , emp_rec_desc FROM employee_receipt , cash "
				+ "WHERE cash_id = emp_rec_cash_id AND emp_rec_emp_id = %d AND emp_rec_date BETWEEN '%s' AND '%s' ORDER BY emp_rec_date DESC";
		try{
			results = statement.executeQuery(String.format(q, id, sDate, eDate));
			while(results.next()){
				Vector<Object> rec = new Vector<>();
				rec.add(results.getInt("emp_rec_id"));
				rec.add(results.getInt("emp_rec_value"));
				rec.add(results.getString("cash_name"));
				rec.add(results.getString("emp_rec_date"));
				rec.add(results.getString("emp_rec_desc"));
				receipts.add(rec);
			}
		}
		catch(SQLException e){}
		return receipts;
	}
	
	public boolean saveTeacherReceipt(int id, int val, int cash, String date, String descr)
	{
		String q = "INSERT INTO teacher_receipt (rec_tc_id , rec_cash_id , rec_value , rec_date, rec_desc, rec_period_id ) VALUES (%d , %d , %d , '%s', '%s', %d)";
		try{
			return statement.executeUpdate(String.format(q, id, cash, val, date, descr, PERIOD)) == 1;
		}
		catch(SQLException e){}
		return false;
	}
	
	public Vector<Object> teacherReceipts(int id, String sDate, String eDate)
	{
		Vector<Object> receipts = new Vector<>();
		String q = "SELECT rec_id , rec_value , pdate(rec_date) as rec_date , cash_name , rec_desc FROM teacher_receipt , cash "
				+ "WHERE cash_id = rec_cash_id AND rec_tc_id = %d AND rec_date BETWEEN '%s' AND '%s' ORDER BY rec_date DESC";
		try{
			results = statement.executeQuery(String.format(q, id, sDate, eDate));
			while(results.next()){
				Vector<Object> rec = new Vector<>();
				rec.add(results.getInt("rec_id"));
				rec.add(results.getInt("rec_value"));
				rec.add(results.getString("cash_name"));
				rec.add(results.getString("rec_date"));
				rec.add(results.getString("rec_desc"));
				receipts.add(rec);
			}
		}
		catch(SQLException e){}
		return receipts;
	}
	
	public Vector<Object> teacherTransactions(String sDate, String eDate)
	{
		Vector<Object> transactions = new Vector<>();
		String q = "SELECT rec_id, tc_name, tc_id, rec_value, cash_name, pdate(rec_date) AS rec_date, rec_desc FROM teachers, teacher_receipt, cash "
				+ "WHERE tc_id = rec_tc_id AND cash_id = rec_cash_id AND rec_date BETWEEN '%s' AND '%s' ORDER BY rec_date DESC";
		try{
			results = statement.executeQuery(String.format(q, sDate, eDate));
			while(results.next()){
				Vector<Object> rec = new Vector<>();
				rec.add(results.getInt("rec_id"));
				rec.add(results.getString("tc_name"));
				rec.add(results.getInt("tc_id"));
				rec.add(results.getInt("rec_value"));
				rec.add(results.getString("cash_name"));
				rec.add(results.getString("rec_date"));
				rec.add(results.getString("rec_desc"));
				transactions.add(rec);
			}
		}
		catch(SQLException e){}
		return transactions;
	}
	
	public Vector<Object> employeeTransactions(String sDate, String eDate)
	{
		Vector<Object> transactions = new Vector<>();
		String q = "SELECT emp_rec_id, emp_name, emp_id, emp_rec_value, cash_name, pdate(emp_rec_date) AS emp_rec_date, emp_rec_desc FROM employee, employee_receipt, cash "
				+ "WHERE emp_id = emp_rec_emp_id AND cash_id = emp_rec_cash_id AND emp_rec_date BETWEEN '%s' AND '%s' ORDER BY emp_rec_date DESC";
		try{
			results = statement.executeQuery(String.format(q, sDate, eDate));
			while(results.next()){
				Vector<Object> rec = new Vector<>();
				rec.add(results.getInt("emp_rec_id"));
				rec.add(results.getString("emp_name"));
				rec.add(results.getInt("emp_id"));
				rec.add(results.getInt("emp_rec_value"));
				rec.add(results.getString("cash_name"));
				rec.add(results.getString("emp_rec_date"));
				rec.add(results.getString("emp_rec_desc"));
				transactions.add(rec);
			}
		}
		catch(SQLException e){}
		return transactions;
	}
	
	public boolean saveStudentPayment(int id, int val, int cash, int y, String date, String descr)
	{
		String q = "INSERT INTO student_payment (pay_st_id, pay_cash_id, pay_value, pay_year, pay_date, pay_desc, pay_period_id ) VALUES (%d , %d , %d , %d, '%s', '%s', %d)";
		try{
			return statement.executeUpdate(String.format(q, id, cash, val, y, date, descr, PERIOD)) == 1;
		}
		catch(SQLException e){}
		return false;
	}
	
	public Vector<Object> studentPyaments(int id, String sDate, String eDate)
	{
		Vector<Object> payments = new Vector<>();
		String q = "SELECT pay_id , pay_value , pdate(pay_date) AS pay_date , cash_name, pay_desc FROM student_payment , cash "
				+ "WHERE pay_cash_id = cash_id AND pay_st_id = %d AND pay_date BETWEEN '%s' AND '%s'";
		try{
			results = statement.executeQuery(String.format(q, id, sDate, eDate));
			while(results.next()){
				Vector<Object> rec = new Vector<>();
				rec.add(results.getInt("pay_id"));
				rec.add(results.getInt("pay_value"));
				rec.add(results.getString("cash_name"));
				rec.add(results.getString("pay_date"));
				rec.add(results.getString("pay_desc"));
				payments.add(rec);
			}
		}
		catch(SQLException e){}
		return payments;
	}
	
	public boolean editStPayment(int val, int id)
	{
		String q = "UPDATE student_payment SET pay_value = %d WHERE pay_id = %d";
		try{
			return statement.executeUpdate(String.format(q, val, id)) == 1;
		}
		catch(SQLException e){}
		return false;
	}
	
	public boolean editTcReceipt(int val, int id)
	{
		String q = "UPDATE teacher_receipt SET rec_value = %d WHERE rec_id = %d";
		try{
			return statement.executeUpdate(String.format(q, val, id)) == 1;
		}
		catch(SQLException e){}
		return false;
	}
	
	public boolean editEmpReceipt(int val, int id)
	{
		String q = "UPDATE employee_receipt SET emp_rec_value = %d WHERE emp_rec_id = %d";
		try{
			return statement.executeUpdate(String.format(q, val, id)) == 1;
		}
		catch(SQLException e){}
		return false;
	}
	
	public boolean deleteStPayment(int id)
	{
		String q = "DELETE FROM student_payment WHERE pay_id = " + id;
		try{
			return statement.executeUpdate(q) == 1;
		}
		catch(SQLException e){}
		return false;
	}
	
	public boolean deleteTcReceipt(int id)
	{
		String q = "DELETE FROM teacher_receipt WHERE rec_id = " + id;
		try{
			return statement.executeUpdate(q) == 1;
		}
		catch(SQLException e){}
		return false;
	}
	
	public boolean deleteEmpReceipt(int id)
	{
		String q = "DELETE FROM employee_receipt WHERE emp_rec_id = " + id;
		try{
			return statement.executeUpdate(q) == 1;
		}
		catch(SQLException e){}
		return false;
	}
	
	public Vector<Object> financialPeriods(String word)
	{
		Vector<Object> data = new Vector<>();
		String q = "SELECT period_id, period_name, pdate(period_start_date) AS sdate , pdate(period_end_date) AS edate, "
				+ "period_active FROM financial_period WHERE period_name LIKE '%%%s%%' ORDER BY period_id DESC";
		try{
			results = statement.executeQuery(String.format(q, word));
			while (results.next()){
				Vector<Object> row = new Vector<>();
				row.add(results.getString("period_id"));
				row.add(results.getString("period_name"));
				row.add(results.getString("sdate"));
				row.add(results.getString("edate"));
				row.add((results.getInt("period_active") == 1)? true:false);
				data.add(row);
			}
		}
		catch(SQLException e){}
		return data;
	}
	
	public boolean newFinancialPeriod(String name)
	{
		String q1 = "INSERT INTO financial_period (period_name, period_start_date, period_active) VALUES ('%s','%s', 1)";
		String q2 = "UPDATE financial_period SET period_end_date = '%s', period_active = 0 WHERE period_id = %d";
		Date date = Calendar.getInstance().getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String today = df.format(date);
		try{
			if(statement.executeUpdate(String.format(q1, name, today)) == 1){
				return statement.executeUpdate(String.format(q2, today, PERIOD)) == 1;
			}
		}
		catch(SQLException e){}
		return false;
	}
	
	public boolean saveCashTrans(int type, int cash, int value, String date, String descr)
	{
		String q = "INSERT INTO cash_transaction (cash_trans_type, cash_trans_cash_id, cash_trans_value, "
				+ "cash_trans_date, cash_trans_desc, cash_trans_period) VALUES (%d, %d, %d, '%s', '%s', %d)";
		try{
			return statement.executeUpdate(String.format(q, type, cash, value, date, descr, PERIOD)) == 1;
		}
		catch(SQLException e){}
		return false;
	}
	
	public Vector<Object> cashTransactions(String sdate, String edate, int type)
	{
		Vector<Object> data = new Vector<>();
		String q = "SELECT cash_trans_id, cash_name, cash_trans_value, pdate(cash_trans_date) AS trans_date , cash_trans_type, cash_trans_desc "
				+ "FROM cash_transaction , cash WHERE cash_id = cash_trans_cash_id AND cash_trans_date BETWEEN '%s' AND '%s' ";
		if(type != 2) q += "AND cash_trans_type = " + type;	
		q +=  " ORDER BY cash_trans_date DESC";
		try{
			results = statement.executeQuery(String.format(q, sdate, edate));
			while(results.next()){
				Vector<Object> row = new Vector<>();
				row.add(results.getInt("cash_trans_id"));
				row.add(results.getInt("cash_trans_value"));
				row.add(results.getString("trans_date"));
				row.add(results.getString("cash_name"));
				row.add((results.getInt("cash_trans_type") == 0)? "برداشت" : "واریز");
				row.add(results.getString("cash_trans_desc"));
				data.add(row);
			}
		}
		catch(SQLException e){}
		return data;
	}
	
	public boolean editCashTrans(int val, int id)
	{
		String q = "UPDATE cash_transaction SET cash_trans_value = %d WHERE cash_trans_id = %d";
		try{
			return statement.executeUpdate(String.format(q, val, id)) == 1;
		}
		catch(SQLException e){}
		return false;
	}
	
	public boolean deleteCashTrans(int id)
	{
		String q = "DELETE FROM cash_transaction WHERE cash_trans_id = " + id;
		try{
			return statement.executeUpdate(q) == 1;
		}
		catch(SQLException e){}
		return false;
	}
	
	public boolean saveAccountTrans(int ac, int cash, int val, String date, String descr)
	{
		String q = "INSERT INTO account_transaction(ac_trans_ac_id, ac_trans_cash_id, ac_trans_value, ac_trans_date, "
				+ "ac_trans_desc, ac_trans_period) VALUES (%d, %d, %d, '%s', '%s', %d)";
		try{
			return statement.executeUpdate(String.format(q, ac, cash, val, date, descr, PERIOD)) == 1;
		}
		catch(SQLException e){}
		return false;
	}
	
	public Vector<Object> accountTransactions(String sdate, String edate, int type)
	{
		Vector<Object> data = new Vector<>();
		String q = "SELECT ac_trans_id, account_name, cash_name, ac_trans_value, pdate(ac_trans_date) AS trans_date, ac_trans_desc, account_type "
				+ "FROM account_transaction , account, cash WHERE account_id = ac_trans_ac_id AND cash_id = ac_trans_cash_id AND ac_trans_date BETWEEN '%s' AND '%s' ";
		if(type != 2) q += "AND account_type = " + type;
		q += " ORDER BY ac_trans_date DESC";
		try{
			results = statement.executeQuery(String.format(q, sdate, edate));
			while(results.next()){
				Vector<Object> row = new Vector<>();
				row.add(results.getInt("ac_trans_id"));
				row.add(results.getInt("ac_trans_value"));
				row.add(results.getString("trans_date"));
				row.add(results.getString("account_name"));
				row.add(results.getString("cash_name"));
				row.add((results.getInt("account_type") == 0)? "مصارف" : "عایدات");
				row.add(results.getString("ac_trans_desc"));
				data.add(row);
			}
		}
		catch(SQLException e){}
		return data;
	}
	
	public boolean editAccountTrans(int val, int id)
	{
		String q = "UPDATE account_transaction SET ac_trans_value = %d WHERE ac_trans_id = %d";
		try{
			return statement.executeUpdate(String.format(q, val, id)) == 1;
		}
		catch(SQLException e){}
		return false;
	}
	
	public boolean deleteAccountTrans(int id)
	{
		String q = "DELETE FROM account_transaction WHERE ac_trans_id = " + id;
		try{
			return statement.executeUpdate(q) == 1;
		}
		catch(SQLException e){}
		return false;
	}
}






