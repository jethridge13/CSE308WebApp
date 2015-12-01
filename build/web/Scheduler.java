import java.sql.Array;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;

/**
 *
 * @author Robert
 */
public class Scheduler {
    public static void main(String[] args)
    {
        
    }
    
    public static boolean isSchedulable(Test t, int numStudents) throws SQLException
    {
        ArrayList<Test> overlap = getTests(t.getStartDate(),t.getEndDate());

        ArrayList<Integer> students = studentsLeftToAppoint(overlap);
        overlap.add(t);

        students.add(numStudents);
        for(Integer otherLoop: students)
        {
            System.out.println("Pool " + otherLoop);
        }
        for(int x = 0; x < overlap.size(); x++)
        {
            if(students.get(x) == 0)
            {
                overlap.remove(x);
                students.remove(x);
                x--;
            }
        }
        
        for(Test tempLoop: overlap)
        {
            System.out.println("ExamId = " + tempLoop.getId());
        }
        
        int firstIndex = getFirstTest(overlap);
        int lastIndex = getLastTest(overlap);
        if(firstIndex == -1 || lastIndex == -1)
            return false;
        Timestamp  leftStart = overlap.get(firstIndex).getStartDate();
        System.out.println("start " + leftStart.toString() + " " + overlap.get(firstIndex).getId());
        Timestamp  leftEnd = new Timestamp(leftStart.getTime()+overlap.get(firstIndex).getDurationMillis());
        Timestamp  rightEnd = overlap.get(lastIndex).getEndDate();
        Timestamp  rightStart = new Timestamp(rightEnd.getTime()-overlap.get(lastIndex).getDurationMillis());
        int innerLeft = -1;
        int innerRight = -1;
        int leftIndex = firstIndex;
        int rightIndex = lastIndex;
        System.out.println("INITIAL LEFT START AND END " + overlap.get(leftIndex).getStartDate().toString() + " " + overlap.get(leftIndex).getEndDate().toString());
        TestingCenter center = new TestingCenter();
        int gapTime = center.getGapTime();
        gapTime = gapTime*60000;
        System.out.println("HERE2!");
        while(overlap.size() != 0)
        {
            System.out.println("LOOP ");
            if(leftStart.compareTo(overlap.get(firstIndex).getStartDate()) < 0)
            {
                Timestamp temp = leftStart;
                leftStart = overlap.get(firstIndex).getStartDate();
                leftEnd = new Timestamp(leftStart.getTime() + (leftEnd.getTime() - temp.getTime()));
            }
            if(rightEnd.compareTo(overlap.get(lastIndex).getEndDate()) > 0)
            {
                Timestamp temp = rightEnd;
                rightEnd = overlap.get(lastIndex).getEndDate();
                rightStart = new Timestamp(rightEnd.getTime() - (temp.getTime() - rightStart.getTime()));
            }
            boolean changeDates= true;
            
            if(!isCenterOpen(leftStart,leftEnd))
            {
                leftStart = getNextOpenTime(leftStart,leftEnd);
                leftEnd = new Timestamp(leftStart.getTime()+overlap.get(leftIndex).getDurationMillis());
            }
            if(!isCenterOpen(rightStart,rightEnd))
            {
                rightEnd = getLastCloseTime(rightStart,rightEnd);
                rightStart = new Timestamp(rightEnd.getTime()-overlap.get(rightIndex).getDurationMillis());
            }
            
            if(leftIndex == rightIndex && leftEnd.compareTo(rightStart) > 0
                    && leftStart.compareTo(rightEnd) < 0 && overlap.size() == 1)
            {
                System.out.println("ONE MORE GO!");
                int studentsLost = openSeats(leftStart,leftEnd);
                students.set(leftIndex, students.get(leftIndex)-studentsLost);
                if(students.get(leftIndex) <= 0)
                {
                    return true;
                }
                else return false;
            }
            if(leftEnd.compareTo(rightStart) > 0)
            {
                System.out.append("OVERLAPPED");
                return false;
            }

            if(!dateInRange(leftStart, leftEnd, overlap.get(leftIndex))
                    || !dateInRange(rightStart, rightEnd, overlap.get(rightIndex))
                    || (innerLeft != -1 && !dateInRange(leftStart, leftEnd, overlap.get(innerLeft)))
                    || (innerRight != -1 && !dateInRange(rightStart, rightEnd, overlap.get(innerRight))))
            {
                if(!dateInRange(leftStart, leftEnd, overlap.get(leftIndex)))
                    System.out.println("LEFT " + overlap.get(leftIndex).getId());
                if(!dateInRange(rightStart, rightEnd, overlap.get(rightIndex)))
                    System.out.println("RIGHT " + overlap.get(rightIndex).getId());
                System.out.println("innerRight " + innerRight);
                System.out.println("innerLeft " + innerLeft);
                System.out.println("OUT OF TESTING RANGE");
                return false;
            }
            System.out.println("CONFLICTS?");
            boolean testsOnSameDates = true;
            ArrayList<Integer> leftConflict = testsOnSameDates(leftStart,leftEnd,overlap);
            if(leftConflict.size() == 1)
            {
                testsOnSameDates = false;
                leftIndex = firstIndex;
                int studentsLost = openSeats(leftStart,leftEnd);
                students.set(firstIndex, students.get(firstIndex)-studentsLost);
                if(students.get(firstIndex) <= 0)
                {
                    students.remove(firstIndex);
                    overlap.remove(firstIndex);
                    firstIndex = getFirstTest(overlap);
                    lastIndex = getLastTest(overlap);
                    leftIndex = firstIndex;
                    rightIndex = lastIndex;
                }
            }
            ArrayList<Integer> rightConflict = testsOnSameDates(rightStart,rightEnd,overlap);
            if(rightConflict.size() == 1)
            {
                testsOnSameDates = false;
                rightIndex = lastIndex;
                int studentsLost = openSeats(rightStart,rightEnd);
                students.set(lastIndex, students.get(lastIndex)-studentsLost);
                if(students.get(lastIndex) <= 0)
                {
                    students.remove(lastIndex);
                    overlap.remove(lastIndex);
                    firstIndex = getFirstTest(overlap);
                    lastIndex = getLastTest(overlap);
                    leftIndex = firstIndex;
                    rightIndex = lastIndex;
                }
            }
            
            if(testsOnSameDates)
            {
                System.out.println("yep");
            }
            else System.out.println("NOPE!");
            
            if(testsOnSameDates && innerLeft != -1 && innerRight != -1)
            {
                int studentsLost = openSeats(leftStart,leftEnd);
                students.set(innerLeft, students.get(innerLeft)-studentsLost);
                if(students.get(innerLeft) <= 0)
                {
                    students.remove(innerLeft);
                    overlap.remove(innerLeft);
                    firstIndex = getFirstTest(overlap);
                    lastIndex = getLastTest(overlap);
                    innerLeft = -1;
                    innerRight = -1;
                    leftIndex = firstIndex;
                    rightIndex = lastIndex;
                }
                if(innerRight != -1)
                {
                    studentsLost = openSeats(rightStart,rightEnd);
                    students.set(innerRight, students.get(innerRight)-studentsLost);
                    if(students.get(innerRight) <= 0)
                    {
                        students.remove(innerRight);
                        overlap.remove(innerRight);
                        firstIndex = getFirstTest(overlap);
                        lastIndex = getLastTest(overlap);
                        innerLeft = -1;
                        innerRight = -1;
                        leftIndex = firstIndex;
                        rightIndex = lastIndex;
                    }
                }
            }
            else if(testsOnSameDates && innerLeft == -1 && innerRight == -1)
            {
                changeDates = false;
                innerLeft = closestEndingConflict(leftConflict,overlap);
                if(leftStart.compareTo(overlap.get(innerLeft).getStartDate()) < 0)
                    leftStart = overlap.get(innerLeft).getStartDate();
                leftEnd = new Timestamp(leftStart.getTime() + overlap.get(innerLeft).getDurationMillis());
                innerRight = closestEndingConflict(rightConflict,overlap);
                if(rightEnd.compareTo(overlap.get(innerRight).getEndDate()) > 0)
                    rightEnd = overlap.get(innerRight).getEndDate();
                rightStart = new Timestamp(rightEnd.getTime() - overlap.get(innerRight).getDurationMillis());
                rightIndex = innerRight;
                leftIndex = innerLeft;
            }
            
            if(changeDates)
            {
                System.out.println("OLE LEFT " + leftStart.toString() + " " + leftEnd.toString());
                Timestamp  temp = leftStart;
                leftStart = new Timestamp(leftEnd.getTime()+gapTime);
                leftEnd = new Timestamp(leftStart.getTime()+ (leftEnd.getTime()-temp.getTime()) );
                temp = rightEnd;
                rightEnd = new Timestamp(rightStart.getTime()+gapTime);
                rightStart = new Timestamp(rightEnd.getTime()- (temp.getTime()-rightStart.getTime()) );
            }
        }
        
        return true;
    }
    
    public static ArrayList<Double> getActualUtilization(Timestamp start, Timestamp end)throws SQLException{
    	ArrayList<Double> result = new ArrayList<Double>();
    	
    	int days = (int)(end.getTime()-start.getTime())/(1000*60*60*24);
    	
    	int seats = numberOfSeats();
    	
    	for(int i=0; i<days; i++){
	    		Calendar cal = Calendar.getInstance();
	    		cal.setTime(start);
	    		cal.add(Calendar.DATE, i);
	    		Timestamp currentDate = (Timestamp) cal.getTime();
	    		Timestamp OpenTime = getOpenTime(currentDate);
	    		Timestamp CloseTime = getCloseTime(currentDate);
	    		int Operating = (int) (CloseTime.getTime() -OpenTime.getTime());
	    		int duration = durationDay(currentDate);
	    		
	    		result.add(calcUtilizationActual(duration, seats, Operating));
    	}
    	
    	
    	return result;
    }
    
    public static ArrayList<Double> getEstimatedUtilization(Timestamp start, Timestamp end) throws SQLException{
    	ArrayList<Double> result = new ArrayList<Double>();
    	ArrayList<Double> actual = getActualUtilization(start, end);
    	Iterator<Double> i = actual.iterator();
    	int count = 0;
    	while(i.hasNext()){
    		double currA = i.next();
    		int days = actual.size();
    		Calendar cal = Calendar.getInstance();
    		cal.setTime(start);
    		cal.add(Calendar.DATE, count);
    		count++;
    		Timestamp currentDate = (Timestamp) cal.getTime();
    		int[] totals = getTotalStudentsAppointments(currentDate);
    		calcUtilizationEstimated(currA, getExamDuration(currentDate), totals[0], totals[1], days);
    	}
    	
    	
    	return result;
    }
    
    public static int getExamDuration(Timestamp day) throws SQLException{
    	String Query = "Select duration FROM Test WHERE DATE_FORMAT(StartDate, '%m-%d') = DATE_FORMAT('"+day.toString()+"','%m-%d');";
    	ResultSet result = Connection.ExecQuery(Query);
    	int total = 0;
    	while(result.next()){
    		total = total+ result.getInt("duration") + gapTime();
    	}
    	return total;
    }
    
    public static int[] getTotalStudentsAppointments(Timestamp day) throws SQLException{
    	String Query = "Select Id FROM Test WHERE DATE_FORMAT(StartDate, '%m-%d') = DATE_FORMAT('"+day.toString()+"','%m-%d');";
    	ResultSet result = Connection.ExecQuery(Query);
    	int totalS = 0;
    	int totalA = 0;
    	while(result.next()){
    		Query = "Select * from StudentTests where TestId = " + result.getInt("Id")+";";
    		ResultSet result2 = Connection.ExecQuery(Query);
    		while(result2.next()){
    			++totalS;
    		}
    		Query = "Select * from Appointment where TestId = " + result.getInt("Id")+";";
    		ResultSet result3 = Connection.ExecQuery(Query);
    		while(result3.next()){
    			++totalA;
    		}
    		
    	}
    	int[] a = new int[2];
    	a[0] =totalS;
    	a[1] = totalA;
    	return a;
    }
    
    public static int numberOfSeats() throws SQLException{
    	String Query = "Select Seats from TestingCenter";
    	ResultSet result = Connection.ExecQuery(Query);
    	return result.getInt(1);
    }
    public static int gapTime() throws SQLException{
    	String Query = "Select GapTime from TestingCenter";
    	ResultSet result = Connection.ExecQuery(Query);
    	return result.getInt(1);
    }
    
    public static int durationDay(Timestamp day) throws SQLException{
    	int dur = 0;
    	String Query = "Select StartDate, EndDate FROM Appointments WHERE DATE_FORMAT(StartDate, '%m-%d') = DATE_FORMAT('"+day.toString()+"','%m-%d');";
    	ResultSet result = Connection.ExecQuery(Query);
    	while(result.next()){
    		Timestamp start  = result.getTimestamp("StartDate");
    		Timestamp end = result.getTimestamp("EndDate");
    		
    		dur = (int)(end.getTime() - start.getTime()) +gapTime()*60000;
    	}
    	return dur;
    	
    }
    
    
    public static double calcUtilizationActual(int durationSum, int numOfSeats, int openTime) 
    {
        return durationSum/(numOfSeats*openTime);
    }
    
    public static double calcUtilizationEstimated(double UtilizationActual, int duration, int students, int appointments, int days){
    	return UtilizationActual + (duration) *(students-appointments)/days;
    }
    
    public static int closestEndingConflict(ArrayList<Integer> indexList, ArrayList<Test> testList)
    {
        int closest = indexList.get(0);
        Timestamp  closestEnd = testList.get(closest).getEndDate();
        System.out.println("indices " + indexList.size() + " tests " + testList.size());
        for(int x = 1; x < indexList.size(); x++)
        {
            Timestamp  checkEnd = testList.get(indexList.get(x)).getEndDate();
            if(closestEnd.compareTo(checkEnd) > 0)
                closest = indexList.get(x);
        }
        return closest;
    }
    
    public static boolean dateInRange(Timestamp start, Timestamp  end, Test exam)
    {
        System.out.println("start " + start.toString() + " end " + end.toString() + " examStart " + exam.getStartDate() + " examEnd " + exam.getEndDate());
        if(start.compareTo(exam.getStartDate()) >= 0
                && end.compareTo(exam.getEndDate()) <= 0)
            return true;
        return false;
    }
    
    public static ArrayList<Integer> testsOnSameDates(Timestamp start, Timestamp  end, ArrayList<Test> list)
    {
        ArrayList<Integer> indexList = new ArrayList<Integer>();
        for(int x = 0; x < list.size(); x++)
        {
            Timestamp  begins = list.get(x).getStartDate();
            Timestamp  ends = list.get(x).getEndDate();
            if(end.compareTo(begins) > 0 && start.compareTo(ends) < 0)
                indexList.add(x);
        }
        return indexList;
    }
    
    public static int getFirstTest(ArrayList<Test> list)
    {
        Timestamp  examStart = null;
        int examIndex = -1;
        for(int x = 0; x < list.size(); x++)
        {
            Timestamp  testStart = list.get(x).getStartDate();
            if(examStart == null || examStart.compareTo(testStart) > 0)
            {
                examStart = testStart;
                examIndex = x;
            }
        }
        return examIndex;
    }
    
    public static int getLastTest(ArrayList<Test> list)
    {
        Timestamp  examEnd = null;
        int examIndex = -1;
        for(int x = 0; x < list.size(); x++)
        {
            Timestamp  testEnd = list.get(x).getEndDate();
            if(examEnd == null || examEnd.compareTo(testEnd) < 0)
            {
                examEnd = testEnd;
                examIndex = x;
            }
        }
        return examIndex;
    }
    
    public static int openSeats(Timestamp start, Timestamp  end) throws SQLException
    {
        int seats = 0;
        String query = "SELECT openSeats(?,?);";
        PreparedStatement statement = Connection.prepare(query);
        statement.setTimestamp(1, start);
        statement.setTimestamp(2, end);
        ResultSet result = Connection.ExecQuery(statement);
        if(result.next())
            seats = result.getInt(1);
        else seats = 0;
        return seats;
    }
    
    public static boolean isCenterOpen(Timestamp start, Timestamp  end) throws SQLException
    {
        boolean retValue = false;
        String query = "SELECT isCenterOpen(?,?);";
        PreparedStatement statement = Connection.prepare(query);
        statement.setTimestamp(1,start);
        statement.setTimestamp(2,end);
        ResultSet result = Connection.ExecQuery(statement);
        if(result.next())
            retValue = result.getBoolean(1);
        return retValue;
    }
    
    public static Timestamp getOpenTime(Timestamp time) throws SQLException
    {
        String query = "SELECT getOpenTime(?);";
        PreparedStatement statement = Connection.prepare(query);
        statement.setTimestamp(1, time);
        ResultSet result = Connection.ExecQuery(statement);
        if(result.next())
            return result.getTimestamp(1);
        return null;
    }
    
    public static Timestamp  getCloseTime(Timestamp time) throws SQLException
    {
        String query = "SELECT getCloseTime(?);";
        PreparedStatement statement = Connection.prepare(query);
        statement.setTimestamp(1, time);
        ResultSet result = Connection.ExecQuery(statement);
        if(result.next())
            return result.getTimestamp(1);
        return null;
    }
    
    public static Timestamp  getNextOpenTime(Timestamp start, Timestamp  end) throws SQLException
    {
        while(!isCenterOpen(start,end))
        {
            Timestamp currentOpen = getOpenTime(start);
            if(currentOpen.compareTo(start) > 0)
            {
                end = new Timestamp(currentOpen.getTime()+ (end.getTime()-start.getTime()) );
                start = currentOpen;
                System.out.println("CURRENT OPEN AND END " + start.toString() + end.toString());
            }
            else
            {
                Timestamp  tomorrow = new Timestamp(start.getTime() + 86400000);
                Timestamp  tomorrowOpen = getOpenTime(tomorrow);
                end = new Timestamp(tomorrowOpen.getTime()+ (end.getTime()-start.getTime()) );
                start = tomorrowOpen;
                System.out.println("NEXT OPEN AND END " + start.toString() + end.toString());
            }
        }
        return start;
    }
    
     public static Timestamp  getLastCloseTime(Timestamp start, Timestamp  end) throws SQLException
    {
        while(!isCenterOpen(start,end))
        {
            Timestamp  currentClose = getCloseTime(end);
            if(currentClose.compareTo(end) < 0)
            {
                start = new Timestamp(currentClose.getTime()- (end.getTime()-start.getTime()) );
                end = currentClose;
            }
            else
            {
                Timestamp  yesterday = new Timestamp(end.getTime() - 86400000);
                Timestamp  yesterdayClose = getCloseTime(yesterday);
                start = new Timestamp(yesterdayClose.getTime()- (end.getTime()-start.getTime()) );
                end = yesterdayClose;
            }
        }
        return end;
    }
    
    public static ArrayList<Test> getTests(Timestamp start, Timestamp  end) throws SQLException
    {
        ArrayList<Test> list = new ArrayList();
        String query = "SELECT * FROM Test T WHERE startDate < ? "
                + " AND endDate > ?;";
        PreparedStatement statement = Connection.prepare(query);
        statement.setTimestamp(1, end);
        statement.setTimestamp(2, start);
        ResultSet result = Connection.ExecQuery(statement);
        while(result.next())
        {
            int Id = result.getInt("Id");
            String classId = result.getString("classId");
            Timestamp  startDate = result.getTimestamp("startDate");
            Timestamp  endDate =  result.getTimestamp("endDate");
            int duration = result.getInt("duration");
            String instructorId = result.getString("instructorId");
            boolean approved = result.getBoolean("approved");
            if(approved)
                list.add( new Test(Id, classId,startDate,endDate,duration,instructorId,approved) );
        }
        
        return list;
    }
    
    public static ArrayList<Integer> studentsAppointed(ArrayList<Test> testList, Timestamp  start, Timestamp  end) throws SQLException
    {
        ArrayList<Integer> studentList = new ArrayList<Integer>();
        for(int x = 0; x < testList.size(); x++)
        {
            String query = "SELECT COUNT(*) FROM Appointment WHERE TestId = ? AND StartDate >= ?"
                    + " AND EndDate <= ?;";
            PreparedStatement statement = Connection.prepare(query);
            statement.setInt(1, testList.get(x).getId());
            statement.setTimestamp(2, start);
            statement.setTimestamp(3, end);
            ResultSet result = Connection.ExecQuery(statement);
            int appsMade;
            if(result.next())
            {
                appsMade = result.getInt(1);
            }
            else appsMade = 0;
        }
        return studentList;
    }
    
    public static ArrayList<Integer> studentsLeftToAppoint(ArrayList<Test> testList) throws SQLException
    {
        ArrayList<Integer> studentList = new ArrayList<Integer>();
        for(int x = 0; x < testList.size(); x++)
        {
            String query = "SELECT COUNT(*) FROM Appointment WHERE TestId = ? AND StartDate >= NOW();";
            PreparedStatement statement = Connection.prepare(query);
            statement.setInt(1, testList.get(x).getId());
            ResultSet result = Connection.ExecQuery(statement);
            int appsMade;
            if(result.next())
            {
                appsMade = result.getInt(1);
            }
            else appsMade = 0;
            System.out.println("appsMade " + appsMade);
            String query2 = "SELECT COUNT(*) FROM StudentTests WHERE TestId = ?;";
            PreparedStatement statement2 = Connection.prepare(query2);
            statement2.setInt(1, testList.get(x).getId());
            ResultSet result2 = Connection.ExecQuery(statement2);
            int totalApps;
            if(result2.next())
                totalApps = result2.getInt(1);
            else totalApps = 0;
            System.out.println("totalApps " + totalApps);
            studentList.add(totalApps-appsMade);
        }
        return studentList;
    }
    
}
