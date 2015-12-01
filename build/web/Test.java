import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Test {
	int Id;
	String classId;
	Timestamp startDate;
	Timestamp endDate;
	int duration;
	String instructorId;
	boolean approved;
	
	public Test(int Id, String classId, Timestamp  startDate, Timestamp  endDate, int duration, String instructorId, boolean approved){
		this.Id = Id;
		this.classId = classId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.duration = duration;
		this.instructorId = instructorId;
		this.approved = approved;
	}
	
	public int getId(){
		return this.Id;
	}
	
	public String getClassId(){
		return this.classId;
	}
	
	public Timestamp  getStartDate(){
		return this.startDate;
	}
	
	public Timestamp  getEndDate(){
		return this.endDate;
	}
	
	public int getDuration(){
		return this.duration;
	}
        
        public int getDurationMillis(){
            return this.duration*60000;
        }
	
	public String instructorId(){
		return this.instructorId;
	}
	
	public boolean approved(){
		return this.approved;
	}
	
        public static void createTest(String classId, int sectionNumber, int term, Timestamp  start, Timestamp  end, int length, Instructor prof, int numStudents) throws SQLException
        {
            
            int id = Connection.generateId("Test", "Id");
            Test newTest = new Test(id,classId,start,end,length,prof.getId(),false);
            try {
		if(Scheduler.isSchedulable(newTest,numStudents)){
                    String Query = "Insert into Test (Id, classId,sectionNumber,term,startDate,endDate,duration,InstructorId,Approved) VALUES (?,?,?,?,?,?,?,?,false);";
                    PreparedStatement statement = Connection.prepare(Query);
                    statement.setInt(1, id);
                    statement.setString(2,classId);
                    statement.setInt(3,sectionNumber);
                    statement.setInt(4,term);
                    statement.setTimestamp(5,start);
                    statement.setTimestamp(6,end);
                    statement.setInt(7, length);
                    statement.setString(8, prof.getId());
                    Connection.ExecUpdateQuery(statement);
                    Logger.getLogger(Instructor.class.getName()).log(Level.SEVERE, ": Insert into Test success");
		}else{
			Logger.getLogger(Instructor.class.getName()).log(Level.SEVERE,": Insert into Test failed");
		}
            }
            catch (SQLException e)
            {
                Logger.getLogger(Instructor.class.getName()).log(Level.SEVERE,": Insert into Test failed");
                throw e;
            }
        }
        
        
        public static void approveDenyTest(boolean state,int id) throws SQLException
        {
            String query = "Update Test Set Approved= ? WHERE Id= ?;";
            PreparedStatement statement = Connection.prepare(query);
            if(statement != null)
            {
                try {
                    statement.setBoolean(0, state);
                    statement.setInt(1, id);
                }
                catch (SQLException ex) {
                    Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
                    throw ex;
                }
            }
            Connection.ExecUpdateQuery(statement);
        }
	public static Test getTest(int id) throws SQLException {
		Test exam = null;
		String query = "SELECT * FROM Test WHERE Id = ?;";
                PreparedStatement statement = Connection.prepare(query);
                if(statement != null)
                {
                    try
                    {
                        statement.setInt(1, id);
                    }
                    catch(SQLException e)
                    {
                        Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, e);
                        throw e;
                    }
                }
                else return null;
		ResultSet result = Connection.ExecQuery(statement);
		try {
                    result.next();
                    int Id = result.getInt("Id");
                    String classId = result.getString("classId");
                    Timestamp  startDate = result.getTimestamp("startDate");
                    Timestamp  endDate =  result.getTimestamp("endDate");
                    int duration = result.getInt("duration");
                    String instructorId = result.getString("InstructorId");
                    boolean approved = result.getBoolean("approved");
                    exam = new Test(Id, classId,startDate,endDate,duration,instructorId,approved);
                    Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, "Test " + id + " recieved.");
		}catch (SQLException ex) {
                    Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, null, ex);
                    throw ex;
                }
		return exam;	
	}

}
