package Group3.JobsMadeEasy.authentication.user.dao.registration;

import Group3.JobsMadeEasy.authentication.user.model.User;
import Group3.JobsMadeEasy.authentication.user.model.UserMapper;
import Group3.JobsMadeEasy.authentication.user.querygenerator.registration.IUserRegistrationQueryGenerator;
import Group3.JobsMadeEasy.database.repository.DatabaseSetup;
import Group3.JobsMadeEasy.util.GenerateIdUtil;
import Group3.JobsMadeEasy.util.JobsMadeEasyException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Component
public class UserRegistrationDaoImp implements IUserRegistrationDao {
    private final IUserRegistrationQueryGenerator userRegistrationQueryGenerator;
    private final DatabaseSetup databaseSetup;
    private final Connection connection;
    private final Statement statement;
    private final HttpSession session;

    public UserRegistrationDaoImp(IUserRegistrationQueryGenerator userRegistrationQueryGenerator, DatabaseSetup databaseSetup, HttpSession session) throws SQLException, IOException, ClassNotFoundException {
        this.userRegistrationQueryGenerator = userRegistrationQueryGenerator;
        this.databaseSetup = databaseSetup;
        this.connection = databaseSetup.getConnectionObject();
        this.statement = connection.createStatement();
        this.session = session;
    }

    @Override
    public String createUser(User user) throws JobsMadeEasyException {
        user.setUserId(GenerateIdUtil.Object().generateRandomId());
        user.setFirstName(user.getFirstName());
        user.setLastName(user.getLastName());
        user.setPhoneNumber(user.getPhoneNumber());
        user.setEmailId(user.getEmailId());
        user.setPassword(user.getPassword());
        user.setCity(user.getCity());
        user.setProvince(user.getProvince());
        user.setAddress(user.getAddress());
        user.setPostalCode(user.getPostalCode());
        user.setRoleId(1);
        user.setEmployee(false);
        user.setApproved(false);
        try {
            String createUserQuery = userRegistrationQueryGenerator.createUser(user);
            int updatedRows = statement.executeUpdate(createUserQuery, Statement.RETURN_GENERATED_KEYS);
            if (updatedRows > 0) {
                return "index";
            }
        } catch (
                SQLException e) {
            throw new JobsMadeEasyException(e.getMessage());
        } finally {
            databaseSetup.closeDatabaseConnection();
        }
        return "register";
    }

    @Override
    public Optional<User> getUserById(int id) throws SQLException, JobsMadeEasyException {
        ResultSet rs = null;
        try {
            String getUserByIdQuery = userRegistrationQueryGenerator.getUserById(id);
            rs = statement.executeQuery(getUserByIdQuery);
            if(rs.next()){
                return Optional.ofNullable(new UserMapper().mapRow(rs, rs.getRow()));
            }
        } catch (SQLException e) {
            throw new JobsMadeEasyException(e.getMessage());
        }finally {
            databaseSetup.closeDatabaseConnection();
            rs.close();
        }
        return null;
    }

    @Override
    public List<User> getUsers() throws JobsMadeEasyException, SQLException {
        ResultSet rs = null;
        try {
            String getUsersQuery = userRegistrationQueryGenerator.getUsers();
            rs = statement.executeQuery(getUsersQuery);
            List<User> users = new LinkedList<>();
            while(rs.next()){
                int i = rs.getInt("userId");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String phoneNumber = rs.getString("phoneNumber");
                String emailId = rs.getString("emailId");
                String password = rs.getString("password");
                String city = rs.getString("city");
                String province = rs.getString("province");
                String address = rs.getString("address");
                String postalCode = rs.getString("postalCode");
                int roleId = rs.getInt("roleId");
                boolean isEmployee = rs.getBoolean("isEmployee");
                boolean isApproved = rs.getBoolean("isApproved");
                User user = new User(i,firstName,lastName,phoneNumber,emailId,password,city,province,address,postalCode,
                        roleId,isEmployee,isApproved);
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            throw new JobsMadeEasyException(e.getMessage());
        }finally {
            databaseSetup.closeDatabaseConnection();
            rs.close();
        }
    }

    @Override
    public boolean deleteUserById(int id) throws JobsMadeEasyException {
        try {
            String deleteUserByIdQuery = userRegistrationQueryGenerator.deleteUserById(id);
            statement.executeUpdate(deleteUserByIdQuery);
            return true;
        } catch (SQLException e) {
            throw new JobsMadeEasyException(e.getMessage());
        }finally {
            databaseSetup.closeDatabaseConnection();
        }
    }
}