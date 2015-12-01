import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Connection {
    static String mysJDBCDriver = "com.mysql.jdbc.Driver";
    static String url = "jdbc:mysql://localhost/testingcenter";
    static String username = "root";
    static String password = "109306074";
    static java.sql.Connection myConnection = null;
                   
    static PreparedStatement myPreparedStatement = null;
   
    public static ResultSet ExecQuery(String query){
        ResultSet myResultSet = null;
        try{
            if(myConnection == null || (myConnection !=null && !myConnection.isValid(0)))
            {
                Class.forName(mysJDBCDriver).newInstance();
                myConnection = DriverManager.getConnection(url,username,password);
            }
            myPreparedStatement = myConnection.prepareStatement(query);
            myResultSet = myPreparedStatement.executeQuery();
        } catch(ClassNotFoundException e)
        {
        }
        catch (SQLException e){
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, e.getMessage(),e);
        } catch (InstantiationException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return  myResultSet;
    }
    
    public static ResultSet ExecQuery(PreparedStatement query) throws SQLException{
        System.out.println(query.toString());
        ResultSet myResultSet = null;
        try{
            if(myConnection == null || (myConnection !=null && !myConnection.isValid(0)))
            {
                Class.forName(mysJDBCDriver).newInstance();
                myConnection = DriverManager.getConnection(url,username,password);
            }
            myPreparedStatement = query;
            myResultSet = myPreparedStatement.executeQuery();
        } catch(ClassNotFoundException e)
        {
        }
        catch (SQLException e){
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, e.getMessage(),e);
            throw e;
        } catch (InstantiationException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return  myResultSet;
    }
    
    public static int ExecUpdateQuery(String query){
        System.out.println(query);
       int retValue = 0;
        try{
            if(myConnection == null || (myConnection !=null && !myConnection.isValid(0)))
            {
                Class.forName(mysJDBCDriver).newInstance();
                myConnection = DriverManager.getConnection(url,username,password);
            }
            myPreparedStatement = myConnection.prepareStatement(query);
            retValue = myPreparedStatement.executeUpdate();
            
        } catch(ClassNotFoundException e)
        {
        }
        catch (SQLException e){
        } catch (InstantiationException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return  retValue;
    }
    
    public static int ExecUpdateQuery(PreparedStatement query) throws SQLException {
       int retValue = 0;
        try{
            if(myConnection == null || (myConnection !=null && !myConnection.isValid(0)))
            {
                Class.forName(mysJDBCDriver).newInstance();
                myConnection = DriverManager.getConnection(url,username,password);
            }
            myPreparedStatement = query;
            System.out.println(myPreparedStatement.toString());
            retValue = myPreparedStatement.executeUpdate();
            
        } catch(ClassNotFoundException e)
        {
        }
        catch (SQLException ex)
        {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        catch (InstantiationException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return  retValue;
    }
    
    public static PreparedStatement prepare(String query){
        PreparedStatement statement = null;
        try{
            if(myConnection == null || (myConnection !=null && !myConnection.isValid(0)))
            {
                Class.forName(mysJDBCDriver).newInstance();
                myConnection = DriverManager.getConnection(url,username,password);
            }
            statement = myConnection.prepareStatement(query);
        } catch(ClassNotFoundException e)
        {
        }
        catch (SQLException e){
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, e.getMessage(),e);
        } catch (InstantiationException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return statement;
    }
    
    public static int generateId(String table, String idName)
    {
        int id = -1;
        boolean unique = false;
        while(!unique)
        {
            id = (int)(Math.pow(10, 10)*Math.random());
            String query = "SELECT * FROM " + table + " WHERE " + idName + " = " + id + ";";
            ResultSet result = Connection.ExecQuery(query);
            try {
                if(!result.next())
                    unique = true;
            } catch (SQLException ex) {
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, "Problem generating Id.", ex);
            }
        }
        System.out.println("id: " + id);
        return id;
    }
}
