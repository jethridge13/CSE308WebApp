
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Student {
	String id;
	String Name;
	String email;
	static final Logger logger = Logger.getLogger("Student_Log");

	public Student(String id, String name, String email){
		this.id = id;
		this.Name = name;
		this.email = email;
                
                FileHandler fh;  
                try {  
                        fh = new FileHandler("C:/Homework/Student_Log.log");  
                        logger.addHandler(fh);
                        SimpleFormatter formatter = new SimpleFormatter();  
                        fh.setFormatter(formatter);                          
                    } catch (SecurityException | IOException e) {
                    }
	}
	
	public String getID(){
		return this.id;
	}
	
	public String getName(){
		return this.Name;
	}
        
	public Appointment[] getAppointments() throws SQLException {
		return Appointment.studentGetAppointments(this);
	}
	
	public int reserveSeat(Test exam, Timestamp start) throws SQLException{
            Timestamp end = new Timestamp(start.getTime()+exam.getDuration()*60000);
            return Appointment.addAppointment(start, end, exam, this); //Test Class
	}
	
	public void cancel(Test exam) throws SQLException{
		 Appointment.removeAppointment(this,exam);
	}

	public static Student getStudent(String studId) throws SQLException {
		String query = "Select * FROM student WHERE id= ?";
                PreparedStatement statement = Connection.prepare(query);
                if(statement != null)
                {
                    try
                    {
                        statement.setString(1, studId);
                    }
                    catch(SQLException e)
                    {
                        Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, e);
                        throw e;
                    }
                }
                else return null;
		ResultSet result = Connection.ExecQuery(statement);
		ArrayList<Student> stulist = Parse(result);
		return stulist.get(0);
	}
	
	public static ArrayList<Student> Parse(ResultSet result){
        ArrayList<Student> list = new ArrayList<>();
        try {
            while(result.next())
            {
            	String id = result.getString("Id");
            	String name = result.getString("Name");
            	String email = result.getString("Email");
            	list.add(new Student(id, name, email));
            }
            logger.log(Level.FINE, "Student result set parsed");
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
            return list;
	}

	public Test[] getTests() {
		String query = "Select T.* FROM studenttests S, test T WHERE StudentId="+this.getID()+" AND "
                        + "S.TestId = T.Id;";
		ResultSet result = Connection.ExecQuery(query);
		ArrayList<Test> list = new ArrayList<>();
                try {
                    while(result.next()){
			
				int Id = result.getInt("Id");
				String classId = result.getString("classId");
				Timestamp startDate = result.getTimestamp("startDate");
				Timestamp endDate =  result.getTimestamp("endDate");
				int duration = result.getInt("duration");
				String instructorId = result.getString("InstructorId");
				boolean approved = result.getBoolean("approved");
				Test Exam = new Test(Id, classId,startDate,endDate,duration,instructorId,approved);
				list.add(Exam);
                                logger.log(Level.FINE, "List of Test Made");
                    }
                }catch (SQLException ex) {
                    Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, ex.getMessage(),ex);
	            
	        }
		return list.toArray(new Test[0]);
	}
	
	public void addCourseToStudent(int studentId, int classId){
		String query = "INSERT INTO StudentCourses VALUES (" + studentId + "," + classId + ");";
		Connection.ExecQuery(query);
	}
	
	public ArrayList<Student> getStudentsOfCourse(String CourseId) throws SQLException{
		ArrayList<Student> student = new ArrayList();
		
		String query = "Select StudentCourses.stuId, Student.Name, Student.Email FROM StudentCourses Inner Join Student On StudentCourses.courseId = "+ CourseId+";";
		ResultSet result = Connection.ExecQuery(query);
		
		while(result.next()){
			String id = result.getString("stuId");
			String name = result.getString("Name");
			String Email = result.getString("Email");
			Student st = new Student(id, name, Email);
			student.add(st);
		}
		
		return student;
	}
	
	
}