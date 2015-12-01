
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestingCenter {
	//Edit Testing Center Information
	
	private int seats;
	private int setAsideSeats;
	private int gapTime;
	private int reminderInterval;
	
	public TestingCenter() throws SQLException{
		String query = "SELECT * FROM TestingCenter;";
		ResultSet result = Connection.ExecQuery(query);
                result.next();
		this.setSeats(result.getInt("seats"));
		this.setSetAsideSeats(result.getInt("setAsideSeats"));
		this.setGapTime(result.getInt("GapTime"));
		this.setReminderInterval(result.getInt("ReminderInterval"));
	}
	
	public static void ChangeTestingCenter(int seats, int setAsideSeats, int GapTime, int ReminderInterval) throws SQLException{
		String query = "UPDATE TestingCenter SET Seats = ?, setAsideSeats= ?, GapTime = ?,ReminderInterval = ?;";
		PreparedStatement statement = Connection.prepare(query);
                if(statement != null)
                {
                    try {
                        statement.setInt(0, seats);
                        statement.setInt(1,setAsideSeats);
                        statement.setInt(2,GapTime);
                        statement.setInt(3, ReminderInterval);
                    } 
                    catch (SQLException ex) {
                        Logger.getLogger(TestingCenter.class.getName()).log(Level.SEVERE, null, ex);
                        throw ex;
                    }
                }
                else return;
                Connection.ExecUpdateQuery(statement);
	}

	public int getSeats() {
		return seats;
	}

	public void setSeats(int seats) {
		this.seats = seats;
	}

	public int getSetAsideSeats() {
		return setAsideSeats;
	}

	public void setSetAsideSeats(int setAsideSeats) {
		this.setAsideSeats = setAsideSeats;
	}

	public int getGapTime() {
		return gapTime;
	}

	public void setGapTime(int gapTime) {
		this.gapTime = gapTime;
	}

	public int getReminderInterval() {
		return reminderInterval;
	}

	public void setReminderInterval(int reminderInterval) {
		this.reminderInterval = reminderInterval;
	}
	
	//String accepts a day value (ie monday) or "all" for all days open
	public int[] getHours(String day) throws SQLException{
		ResultSet r = Connection.ExecQuery("Select * From Hours");
		int[] hours;
		if(day.equalsIgnoreCase("all")){
			hours = new int[10];
			for(int i = 1; i<11; i++){
				hours[i-1] = r.getInt(i);
			}
			
		}else{
			hours = new int[2];
			hours[0]=r.getInt(day.toLowerCase()+"Open");
			hours[1] = r.getInt(day.toLowerCase()+"Close");
		}
		return hours;
	}
	
	
}
