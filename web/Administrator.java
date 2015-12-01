import java.sql.Timestamp;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Administrator {
	
	
	//APPOINTMENT SECTION FOR ADMINS
	
	//Remove Appointment
	public static void removeAppointment(Student stud, Test exam) throws SQLException{
		Appointment.removeAppointment(stud, exam);
	}
	
	//Returns All Appointments for an exam
	public Appointment[] getAllAppointments(Test[] exam) throws SQLException{
		Appointment[] all = null;
		Collection<Appointment> collection = new ArrayList<Appointment>();
		for (Test test : exam) {
			Appointment[] holder = all.clone();
			all = Appointment.instructorGetAppointments(test);
			collection.addAll(Arrays.asList(holder));
			collection.addAll(Arrays.asList(all));
			all = (Appointment[]) collection.toArray();
		}
		return all;
	}
	
	//Edit/Adds Appointments
	public static void addEditAppointment(int seatNumber, Timestamp start, Timestamp end,
			Test exam, Student stud) throws SQLException {
		Appointment.addAppointment(start, end, exam, stud);
	}
	//APPOINTMENTS SECTION FOR ADMIN ENDS
	
	public static void ApproveDenyTest(boolean state, Test exam) throws SQLException{
		Test.approveDenyTest(state,exam.getId());
	}
	
	
	
}
