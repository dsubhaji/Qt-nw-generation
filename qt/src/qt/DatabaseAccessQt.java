package qt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.omg.CORBA.PUBLIC_MEMBER;

public class DatabaseAccessQt {

	public Connection con;
	public ResultSet rs, rs2;
	public Statement s;
	public Statement s2;
	
	private NetworkBuilder nb= new NetworkBuilder();
	
	private String fileContent;
	private String fileName;
	private String dbName;
	
	private int num;
	
	public DatabaseAccessQt() {

		fileContent ="";
		fileName="";
		
	}
	public String getFileContent()
	{
		return fileContent;
	}
	
	public String getFileName()
	{
		return fileName;
	}
	
	public String getDBName()
	{
		return dbName;
	}
	
	
	public boolean openConnection(String databaseName, String mysqlUser, String password) throws Exception
	{
		dbName = databaseName;
		Class.forName("com.mysql.jdbc.Driver"); //load mysql driver
		try
		{
			con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/" + databaseName + "?user=" + mysqlUser + "&password=" + password); //set-up connection with database
			s = con.createStatement(); //Statements to issue sql queries
			s2 = con.createStatement();
		} catch (SQLException e) 
		{
			con.close();
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public String[]  getMinMax() throws Exception {
		 
		System.out.println("Calculating Min and Max date From The database");
		String[] list=null;
		rs= s.executeQuery("select min(date),max(date) from messages");
		boolean status;
		status=rs.next();
		if(status)
		{
		 list=new String[2];
		 list[0]=rs.getString(1);
		 list[1]=rs.getString(2);
		}
		return list;
		
	}
	public String[]  getMinMaxReview() throws Exception {
		 
		System.out.println("Calculating Min and Max date From The database");
		String[] list=null;
		rs= s.executeQuery("select min(created),max(updated) from review");
		boolean status;
		status=rs.next();
		if(status)
		{
		 list=new String[2];
		 list[0]=rs.getString(1);
		 list[1]=rs.getString(2);
		}
		return list;
		
	}
	public String getAccountList() throws Exception {
		System.out.println("Extracting Account List");
		rs=s.executeQuery("select _account_id from person");
		boolean status;
		status =rs.next();
		String acccountList=null;
		while(status)
		{
			acccountList=acccountList+"\'"+rs.getString(1)+"\'"+",";
			status=rs.next();
		}
		
		return acccountList.substring(0,acccountList.length()-1);
	
		
	}
	/*
	 *  
	 * 
	 * 
	 * */
	public void  generateDRMN(String accountList, String reviewList, String startDate, String endDate) throws SQLException {
		
		ArrayList<String> developers = new ArrayList<String>();
		ArrayList<String> developers2= new ArrayList<String>();
		ArrayList<String> developers3= new ArrayList<String>();
		ArrayList<Integer> edges     = new ArrayList<Integer>();
		//String acccountList1=acccountList.replaceAll("'", "");

		String[] acccountListExcel=accountList.split(",");
		String[] reviewListExcel=reviewList.split(",");
		

		ArrayList<String> excelList=new ArrayList<String>(Arrays.asList(acccountListExcel));
		
		ArrayList<String> excelList1=new ArrayList<String>(Arrays.asList(reviewListExcel));
		
		System.out.println("");
		System.out.println("Calculating the Total Number of Distinct Developers...");
		String qry;
		
		if(accountList!=null && reviewList!=null)
		{
			qry="select distinct _account_id  from messages where _account_id in ("+accountList+") and review_id in ("+reviewList+") and date>='"+startDate+ "' and date<='"+endDate+"'";

		}
		else
		{
			 qry="select distinct _account_id from messages where  date>='"+startDate+ "' and date<='"+endDate+"'";	
		}
	
		System.out.println(qry);
		rs = s.executeQuery(qry);
			
		while(rs.next())
		{
			developers.add(rs.getString("_account_id"));
		}
		
		System.out.println("Developers="+developers);
		
		excelList.removeAll(developers);
		developers.addAll(excelList);
		
	    if(accountList!=null && reviewList!=null)
	    	qry="SELECT a._account_id as account_id1,  b._account_id as account_id2, count(distinct b.review_id) as total FROM messages a, messages b WHERE a.review_id = b.review_id AND a._account_id != b._account_id AND a.date>='"+startDate+"' AND a.date<='"+endDate+"' AND b.date>='"+startDate+"' AND b.date<='"+endDate+"' AND a._account_id in ("+accountList+") AND b._account_id in ("+accountList+")  and a.review_id in ("+reviewList+") and b.review_id in ("+reviewList+") GROUP BY a._account_id,b._account_id";
	    else
	    	qry="SELECT a._account_id as account_id1,  b._account_id as account_id2, count(distinct b.review_id) as total FROM messages a, messages b WHERE a.review_id = b.review_id AND a._account_id != b._account_id AND a.date>='"+startDate+"' AND a.date<='"+endDate+"' AND b.date>='"+startDate+"' AND b.date<='"+endDate+"' GROUP BY a._account_id,b._account_id";
	    
		System.out.println(qry);		
		rs = s.executeQuery(qry);
		Map<Integer,String> map=new HashMap<Integer,String>();
	    int i=1;
		while(rs.next())
		{
			String dev1=rs.getString("account_id1");
			String dev2=rs.getString("account_id2");
			int total=rs.getInt("total");
			String dev=dev1+","+dev2;
			String devReverse=dev2+","+dev1;
			if(map!=null)
			{
				if(map.containsValue(devReverse))
				{
					
				}
				else
				{
					map.put(i,dev);
					i++;
					developers2.add(dev1);
					developers3.add(dev2);
					edges.add(total);
				}
				}
			}
		  System.out.println("developers2="+developers2);
		  System.out.println("developers3="+developers3);
		  System.out.println("total="+edges);
		  num=developers.size();
	      fileContent = nb.networkBuilder(developers, developers2, developers3, edges, num);
	        	
		  
	}
	 
	
	public void generateDRCN(String accountList, String reviewList,String startDate, String endDate) throws SQLException {
	
		ArrayList<String> developers = new ArrayList<String>();
		ArrayList<String> developers2= new ArrayList<String>();
		ArrayList<String> developers3= new ArrayList<String>();
		ArrayList<Integer> edges     = new ArrayList<Integer>();
		//String acccountList1=acccountList.replaceAll("'", "");

		String[] acccountListExcel=accountList.split(",");
		String[] reviewListExcel=reviewList.split(",");

		ArrayList<String> excelList=new ArrayList<String>(Arrays.asList(acccountListExcel));
		ArrayList<String> excelList1=new ArrayList<String>(Arrays.asList(reviewListExcel));
		
		System.out.println("");
		System.out.println("Calculating the Total Number of Distinct Developers...");
		String qry;
		
		if(accountList!=null&& reviewList!=null)
		{
			qry="select distinct _account_id  from review where _account_id in ("+accountList+")and review_id in ("+reviewList+") and created>='"+startDate+ "' and updated<='"+endDate+"'";

		}
		else
		{
			 qry="select distinct _account_id from review where  created>='"+startDate+ "' and updated<='"+endDate+"'";	
		}
	
		System.out.println(qry);
		rs = s.executeQuery(qry);
			
		while(rs.next())
		{
			developers.add(rs.getString("_account_id"));
		}
		
		System.out.println("Developers="+developers);
		
		excelList.removeAll(developers);
		developers.addAll(excelList);
		if(accountList!=null&& reviewList!=null)
			qry="select b._account_id as account_id1,a._account_id as account_id2,a.subject,count(b.id) as total from review a,messages b where a._account_id in ("+accountList+") and b._account_id in ("+accountList+") and a.review_id=b.review_id and a._account_id!=b._account_id and a.review_id in ("+reviewList+") and b.review_id in ("+reviewList+") and a.created>='"+startDate+"' and a.updated <='"+endDate+"' and b.date>='"+startDate+"' and b.date <='"+endDate+"' group by a._account_id,b._account_id,a.project,a.branch,a.subject";
	    	
	    else
	    	qry="select b._account_id as account_id1,a._account_id as account_id2,a.subject,count(b.id) as total from review a,messages b where a.review_id=b.review_id and a._account_id!=b._account_id and a.created>='"+startDate+"' and a.updated <='"+endDate+"' and b.date>='"+startDate+"' and b.date <='"+endDate+"' group by a._account_id,b._account_id,a.project,a.branch,a.subject";
				
		rs = s.executeQuery(qry);
		
		while(rs.next())
		{
			developers2.add(rs.getString("account_id1"));
			developers3.add(rs.getString("account_id2"));
			edges.add(rs.getInt("total"));
		}
		excelList.removeAll(developers2);
		developers.addAll(excelList);
		num=developers.size();
		
		fileContent = nb.networkBuilder(developers, developers2, developers3, edges, num);
	}

public void  generateDCRN(String accountList, String reviewList, String startDate, String endDate) throws SQLException {
		
		ArrayList<String> developers = new ArrayList<String>();
		ArrayList<String> developers2= new ArrayList<String>();
		ArrayList<String> developers3= new ArrayList<String>();
		ArrayList<Integer> edges     = new ArrayList<Integer>();
		//String acccountList1=acccountList.replaceAll("'", "");

		String[] acccountListExcel=accountList.split(",");
		String[] reviewListExcel=reviewList.split(",");
		

		ArrayList<String> excelList=new ArrayList<String>(Arrays.asList(acccountListExcel));
		
		ArrayList<String> excelList1=new ArrayList<String>(Arrays.asList(reviewListExcel));
		
		System.out.println("");
		System.out.println("Calculating the Total Number of Distinct Developers...");
		String qry;
		
		if(accountList!=null && reviewList!=null)
		{
			qry="select distinct _account_id  from messages where _account_id in ("+accountList+") and review_id in ("+reviewList+") and date>='"+startDate+ "' and date<='"+endDate+"'";

		}
		else
		{
			 qry="select distinct _account_id from messages where  date>='"+startDate+ "' and date<='"+endDate+"'";	
		}
	
		System.out.println(qry);
		rs = s.executeQuery(qry);
			
		while(rs.next())
		{
			developers.add(rs.getString("_account_id"));
		}
		
		System.out.println("Developers="+developers);
		
		excelList.removeAll(developers);
		developers.addAll(excelList);
		
	    if(accountList!=null && reviewList!=null)
	    	qry="SELECT a._account_id as account_id1,  b._account_id as account_id2, count(distinct b.id) as total FROM messages a, messages b WHERE a.review_id = b.review_id AND a._account_id != b._account_id AND a.date>='"+startDate+"' AND a.date<='"+endDate+"' AND b.date>='"+startDate+"' AND b.date<='"+endDate+"' AND a._account_id in ("+accountList+") AND b._account_id in ("+accountList+")  and a.review_id in ("+reviewList+") and b.review_id in ("+reviewList+") GROUP BY a._account_id,b._account_id";
	    else
	    	qry="SELECT a._account_id as account_id1,  b._account_id as account_id2, count(distinct b.id) as total FROM messages a, messages b WHERE a.review_id = b.review_id AND a._account_id != b._account_id AND a.date>='"+startDate+"' AND a.date<='"+endDate+"' AND b.date>='"+startDate+"' AND b.date<='"+endDate+"' GROUP BY a._account_id,b._account_id";
	    
		System.out.println(qry);		
		rs = s.executeQuery(qry);
		Map<Integer,String> map=new HashMap<Integer,String>();
	    int i=1;
		while(rs.next())
		{
			String dev1=rs.getString("account_id1");
			String dev2=rs.getString("account_id2");
			int total=rs.getInt("total");
			String dev=dev1+","+dev2;
			String devReverse=dev2+","+dev1;
			if(map!=null)
			{
				if(map.containsValue(devReverse))
				{
					
				}
				else
				{
					map.put(i,dev);
					i++;
					developers2.add(dev1);
					developers3.add(dev2);
					edges.add(total);
				}
				}
			}
		  System.out.println("developers2="+developers2);
		  System.out.println("developers3="+developers3);
		  System.out.println("total="+edges);
		  num=developers.size();
	      fileContent = nb.networkBuilder(developers, developers2, developers3, edges, num);
	        	
		  
	}
	 
	
}
	
	

