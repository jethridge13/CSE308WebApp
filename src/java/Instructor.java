//import static database.Student.logger;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Instructor {
	String id;
	String name;
	String email;
	
	public Instructor(String id, String name, String email){
		this.id = id;
		this.name = name;
		this.email = email;
	}
	
	public Appointment[] viewAppointments(Test exam) throws SQLException{
		return Appointment.instructorGetAppointments(exam);
	}
	
	public void requestTest(String classId, int sectionNumber, int term, Timestamp  start, Timestamp  end, int duration,int numStudents) throws SQLException{
		Test.createTest(classId, sectionNumber, term, start, end, duration, this,numStudents);
	}
	
	public String getId()
        {
            return id;
        }
        
        public static Instructor getInstructor(String instrId) throws SQLException {
		String query = "Select * FROM Instructor WHERE id= ?";
                PreparedStatement statement = Connection.prepare(query);
                if(statement != null)
                {
                    try
                    {
                        statement.setString(1, instrId);
                    }
                    catch(SQLException e)
                    {
                        Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, e);
                        throw e;
                    }
                }
                else return null;
		ResultSet result = Connection.ExecQuery(statement);
		ArrayList<Instructor> insList = Parse(result);
		return insList.get(0);
	}
        
        public static ArrayList<Instructor> Parse(ResultSet result){
        ArrayList<Instructor> list = new ArrayList<>();
        try {
            while(result.next())
            {
            	String id = result.getString("Id");
            	String name = result.getString("Name");
            	String email = result.getString("Email");
            	list.add(new Instructor(id, name, email));
            }
            //logger.log(Level.FINE, "Student result set parsed");
        } catch (SQLException ex) {
            //logger.log(Level.SEVERE, null, ex);
        }
        return list;
	}
	

	
	public void cancelTestRequest(int id){
		String Query = "DELETE FROM Test WHERE Id ="+id +";";
		if(Connection.ExecUpdateQuery(Query) != 0)
                    Logger.getLogger(Instructor.class.getName()).log(Level.SEVERE, "Could not delete exam " + id + " from Instructor ");
                else Logger.getLogger(Instructor.class.getName()).log(Level.SEVERE, "Successfully deleted exam " + id);
	}
}
