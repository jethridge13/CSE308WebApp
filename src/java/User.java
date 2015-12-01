import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Robert
 */
public class User {
    
    private String id;
    private String password;
    private String name;
    private String email;
    private boolean isStudent;
    private boolean isInstructor;
    private boolean isAdministrator;
    
    public User (String id, String password, String name, String email, boolean isStudent, 
            boolean isInstructor, boolean isAdministrator)
    {
        this.id = id;
        this.password = password;
        this.name = name;
        this.email = email;
        this.isStudent = isStudent;
        this.isInstructor = isInstructor;
        this.isAdministrator = isAdministrator;
    }
    
    public static User getUser(String id, String password) throws SQLException
    {
        String query = "SELECT * FROM Role WHERE Id = ? AND password = ?;";
        PreparedStatement statement = Connection.prepare(query);
        statement.setString(1, id);
        statement.setString(2, password);
        ResultSet result = Connection.ExecQuery(statement);
        if(result.next())
        {
            String n = result.getString("name");
            String e = result.getString("email");
            boolean s = result.getBoolean("isStudent");
            boolean i = result.getBoolean("isInstructor");
            boolean a = result.getBoolean("isAdministrator");
            return new User(id,password,n,e,s,i,a);
        }
        return null;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return the isStudent
     */
    public boolean isStudent() {
        return isStudent;
    }

    /**
     * @return the isInstructor
     */
    public boolean isInstructor() {
        return isInstructor;
    }

    /**
     * @return the isAdministrator
     */
    public boolean isAdministrator() {
        return isAdministrator;
    }
    
}