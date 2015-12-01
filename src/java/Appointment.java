
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Appointment {
    private int seat;
    private Timestamp startTime;
    private Timestamp endTime;
    private Test test;
    private Student student;
    private boolean attended;
    
    public Appointment(int seatNumber, Timestamp start, Timestamp end, Test exam,
                     Student stud, boolean attend)
    {
        seat = seatNumber;
        startTime = start;
        endTime = end;
        test = exam;
        student = stud;
        attended = attend;
    }
    
	public static int addAppointment(Timestamp start, Timestamp end, Test exam, Student stud) throws SQLException {
            String query = "INSERT INTO Appointment (Seat,StartDate,EndDate,StudentId,TestId,Attendence) "
                + "VALUES ( ?, ?,?,?,?,false);";
            PreparedStatement statement = Connection.prepare(query);
            if(statement != null)
            {
                try
                {
                    statement.setInt(1,0);
                    statement.setTimestamp(2,start);
                    statement.setTimestamp(3, end);
                    statement.setString(4, stud.getID());
                    statement.setInt(5, exam.getId());
                }
                catch(SQLException e)
                {
                    Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, e);
                    throw e;
                }
            }
            else return -1;
        if(Connection.ExecUpdateQuery(statement) == 0)
            Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, "Could not Insert " + exam.getClassId() + " into "
                    + stud.Name + " Appointment");
        else Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, "Successfully Inserted " + exam.getClassId() + ""
                + " into " + stud.Name + " Appointment");
        return AssignOpenSeat(new Appointment(-1, start, end, exam, stud, false));
    }
    
    public static int AssignOpenSeat(Appointment app) throws SQLException
    {
        int seat = -1;
        String query = "SELECT A.Seat, T.Seats, T.setAsideSeats FROM Appointment A,"
                + " TestingCenter T WHERE A.StartDate >= ?"
                + " AND A.EndDate <= ?;";
        PreparedStatement statement = Connection.prepare(query);
        statement.setTimestamp(1, app.getStartTime());
        statement.setTimestamp(2, app.getEndTime());
        ResultSet result = Connection.ExecQuery(statement);
        try { 
            if(!result.next())
                return -1;
            int totalSeats = result.getInt("Seats");
            boolean [] forbiddenSeats = new boolean[totalSeats+1];
            int asideSeats = result.getInt("setAsideSeats");
            for(int x = 1; x <= asideSeats; x++)
            {
                forbiddenSeats[x] = true;
            }
            forbiddenSeats[result.getInt("Seat")] = true;
            while(result.next())
            {
                forbiddenSeats[result.getInt("Seat")] = true;
            }
            for(int x = 1; x < forbiddenSeats.length; x++)
            {
                if(!forbiddenSeats[x])
                {
                    seat = x;
                    x = forbiddenSeats.length;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, "Could not update seat number", ex);
        }
        String query2 = "UPDATE Appointment SET Seat = ? WHERE StudentId = ?"
                + " AND TestId = ?;";
        PreparedStatement statement2 = Connection.prepare(query2);
        statement2.setInt(1,seat);
        statement2.setString(2, app.getStudent().getID());
        statement2.setInt(3,app.getTest().getId());
        Connection.ExecUpdateQuery(statement2);
        return seat;
    }
        
    public static void removeAppointment(Student stud, Test exam) throws SQLException
    {
       /* String query = "DELETE FROM Appointment WHERE StudentId = " + stud.getID() +
                "AND TestId = " + exam.getId(); */
        String query = "DELETE FROM Appointment WHERE StudentId = ? AND TestId = ?;";
        PreparedStatement statement = Connection.prepare(query);
            if(statement != null)
            {
                try
                {
                    statement.setString(1, stud.getID());
                    statement.setInt(2, exam.getId());
                }
                catch(SQLException e)
                {
                    Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, e);
                    throw e;
                }
            }
            else return;
        if(Connection.ExecUpdateQuery(statement) != 0)
            Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, "Could not delete from Appointment "+ exam.getClassId() + " from "
                + "Student " + stud.getName() + " Appointment");
        else Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, "Successfully deleted " + exam.getClassId() + " from "
                + "Student " + stud.getName() + " Appointment");
    }
    
    public static Appointment[] studentGetAppointments(Student stud) throws SQLException
    {
        String testIds = "(";
        Test[] testList = stud.getTests();
        for (int i = 0; i < testList.length; i++) {
            if(i == 0)
                testIds += testList[i].getId();
            else testIds += "," + testList[i].getId() ;
        }
        
        testIds += ")";
        String query = "SELECT * FROM Appointment WHERE StudentId = ?";
        if(testList.length != 0)
            query += " AND TestId IN ?";
        query += ";";
        PreparedStatement statement = Connection.prepare(query);
        if(statement != null)
        {
            try
            {
                statement.setString(1, stud.getID());
                if(testList.length != 0)
                    statement.setString(2, testIds);
            }
            catch(SQLException e)
            {
                Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, e);
                throw e;
            }
        }
        else return null;
        ResultSet result = Connection.ExecQuery(statement);
        return parseResultSet(result);
    }
    
    public static boolean canHaveAppointment(Test exam)
    {
        return false;
    }
    
    public static Appointment[] instructorGetAppointments(Test exam) throws SQLException
    {
        String query = "SELECT * FROM Appointment WHERE TestId = ?;";
        PreparedStatement statement = Connection.prepare(query);
        if(statement != null)
        {
            try
            {
                statement.setInt(0, exam.getId());
            }
            catch(SQLException e)
            {
                Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, e);
                throw e;
            }
        }
        else return null;
        ResultSet result = Connection.ExecQuery(statement);
        return parseResultSet(result);
    }
    
    private static Appointment[] parseResultSet(ResultSet result)
    {
        ArrayList<Appointment> list = new ArrayList<>();
        try {
            while(result.next())
            {
                int seatNumber = result.getInt("Seat");
                Timestamp time1 = result.getTimestamp("StartDate");
                Timestamp  start;
                if(time1 != null)
                    start = new Timestamp(time1.getTime());
                else start = null;
                Timestamp time2 = result.getTimestamp("EndDate");
                Timestamp  end;
                if(time2 != null)
                    end = new Timestamp(time1.getTime());
                else end = null;
                Student resultStudent = Student.getStudent(result.getString("StudentId"));
                Test exam = Test.getTest(result.getInt("TestId"));
                boolean attend = result.getBoolean("Attendence");
                list.add(new Appointment(seatNumber,start,end,exam,resultStudent,attend));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list.toArray(new Appointment[0]);
    }

    /**
     * @return the seat
     */
    public int getSeat() {
        return seat;
    }

    /**
     * @return the startTime
     */
    public Timestamp  getStartTime() {
        return startTime;
    }

    /**
     * @return the endTime
     */
    public Timestamp  getEndTime() {
        return endTime;
    }

    /**
     * @return the test
     */
    public Test getTest() {
        return test;
    }

    /**
     * @return the Student
     */
    public Student getStudent() {
        return student;
    }

    /**
     * @return the attended
     */
    public boolean isAttended() {
        return attended;
    }

}
