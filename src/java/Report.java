
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Report {
    private int appointments;
    private int [] terms;
    private String [] courses;
    private String notes;
    
    public Report(int app, int[] term, String[] course, String note)
    {
        appointments = app;
        terms = term;
        courses = course;
        notes = note;
    }
    
    public static File dailyReport(Timestamp time)
    {
        Timestamp  endTime = new Timestamp(time.getTime()+86700000L); // endTime is the start time plus 1 day in milliseconds
        String query = "SELECT COUNT(*) AS NumAppointments FROM Appointment WHERE StartDate <= '"
                + endTime.toString() + "' AND EndDate >= '" + time.toString() + "'";
        File f = new File("dailyReport_" + time.toString() + ".csv");
        ResultSet result = Connection.ExecQuery(query);
        try
        {
            result.next();
            int appointments = result.getInt("NumAppointments");
            PrintStream writer = new PrintStream(f);
            writer.println("Appiontments");
            writer.println("" + appointments);
            writer.close();
        }
        catch(IOException e)
        {
            Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, "Could not read File dailyReport_" + time.toString() +".csv");
            return null;
        }
        catch(SQLException ex)
        {
            Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, "Could not count Appointments on day " + time.toString());
            return null;
        }
        Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, "created file  " + f.getPath());
        return f;
    }
    
    public static File weeklyReport(Timestamp time)
    {
        Timestamp  endTime = new Timestamp(time.getTime()+86700000L*8L); // endTime is the start time plus 8 days in milliseconds
        String query = "SELECT COUNT(*) as NumAppointments, T.classId FROM Appointment A, Test T "
                + "WHERE A.StartDate <= '" + endTime.toString() + "' AND "
                + "A.EndDate >= '" + time.toString() + "' AND A.TestId = T.Id "
                + "GROUP BY T.classId;";
        File f = new File("weeklyReport_" + time.toString() + ".csv");
        ResultSet result = Connection.ExecQuery(query);
        try
        {
            PrintStream writer = new PrintStream(f);
            writer.println("Appiontments,Class Id");
            int total = 0;
            while(result.next())
            {
                int appointments = result.getInt("NumAppointments");
                String classId = result.getString("classId");
                writer.println(appointments + "," + classId);
                total += appointments;
            }
            writer.println("Total," + total);
            writer.close();
        }
        catch(IOException e)
        {
            Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, "Could not read File weeklyReport_" + time.toString() +".csv");
            return null;
        }
        catch(SQLException ex)
        {
            Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, "Could not count Appointments for week " + time.toString());
            return null;
        }
        Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, "created file  " + f.getPath());
        return f;
    }
    
    public static File termReport(int term)
    {
        String query = "Select classID From Test where term = " + term + " AND Approved = true;";
        File f = new File("TermReport_" + term + ".csv");
        ResultSet result = Connection.ExecQuery(query);
        try
        {
            PrintStream writer = new PrintStream(f);
            writer.println("Courses For Term " + term);
            int total = 0;
            while(result.next())
            {
                String classId = result.getString("classId");
                writer.println(classId);
                total++;
            }
            writer.println("Total," + total);
            writer.close();
        }
        catch(IOException e)
        {
            Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, "Could not read File TermReport_" + term +".csv");
            return null;
        }
        catch(SQLException ex)
        {
            Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, "Could not count Appointments for term " + term);
            return null;
        }
        Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, "created file  " + f.getPath());
        return f;
    }
    
    public static File multiTermReport(int[] terms)
    {

        File f = new File("MultiTermReport" + ".csv");
        for(int i = 0; i< terms.length; i++){
        
        	
            String query = "SELECT COUNT(*) as NumAppointments, T.classId FROM Appointment A, Test T "
                    + "WHERE A.TestId = T.Id  AND T.term = " + terms[i]
                    + "GROUP BY T.term;";
        ResultSet result = Connection.ExecQuery(query);
        try
        {
            PrintStream writer = new PrintStream(f);
            writer.println("Appiontments,term");
            int total = 0;
            while(result.next())
            {
                int appointments = result.getInt("NumAppointments");
                String classId = result.getString("term");
                writer.println(appointments + "," + classId);
            }
            writer.close();
        }
        catch(IOException e)
        {
            Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, "Could not read File multiTermReport" +".csv");
            return null;
        }
        catch(SQLException ex)
        {
            Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, "Could not count Appointments for term " + terms[i]);
            return null;
        }
        }
        Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, "created file  " + f.getPath());
        return f;
    }

}