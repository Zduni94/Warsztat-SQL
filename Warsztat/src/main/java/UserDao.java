import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.Arrays;

public class UserDao {

    private static final String DROP_DATABASE_IF_EXISTS = "DROP DATABASE IF EXISTS workshop2";
    private static final String CREATE_DATABASE_IF_NOT_EXISTS = "CREATE DATABASE IF NOT EXISTS workshop2 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
    private static final String CREATE_USERS_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS users (\n" +
            "   id INT NOT NULL AUTO_INCREMENT,\n" +
            "   username VARCHAR(255) NOT NULL UNIQUE,\n" +
            "   email VARCHAR(255) NOT NULL UNIQUE,\n" +
            "   password VARCHAR(255) NOT NULL,\n" +
            "   PRIMARY KEY (id)\n" +
            ");";
    private static final String CREATE_QUERY =
            "INSERT INTO users(username, email, password) VALUES (?, ?, ?)";
    private static final String READ_USER_QUERY =
            "SELECT * FROM users where id = ?";
    private static final String UPDATE_USER_QUERY =
            "UPDATE users SET username = ?, email = ?, WHERE = id = ?";
    private static final String UPDATE_USER_PASSWORD_QUERY =
            "UPDATE users SET password = ? WHERE id = ?";
    private static final String DELETE_USER_QUERY =
            "DELETE FROM users WHERE id = ?";
    private static final String FIND_ALL_USERS_QUERY =
            "SELECT * FROM users";
    private static final String DELETE_ALL_USERS_QUERY =
            "DELETE FROM users";

    public void dropDatabase() {
        try(Connection conn = DBUtil.connect()) {
            conn.createStatement().executeUpdate(DROP_DATABASE_IF_EXISTS);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DaoException("Blad usuwania bazy danych", ex);
        }
    }

    public void createDatabase() {
        try (Connection conn = DBUtil.connect()) {
            conn.createStatement().executeUpdate(CREATE_DATABASE_IF_NOT_EXISTS);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DaoException("Blad usuwania bazy danych", ex);
        }
    }

    public void createUserTable() {
        try (Connection conn = DBUtil.connect()) {
            conn.createStatement().executeUpdate(CREATE_USERS_TABLE_IF_NOT_EXISTS);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DaoException("Blad usuwania bazu danych", ex);
        }
    }

    public User create(User user) {
        try (Connection conn = DBUtil.connect("workshop2")) {
            PreparedStatement statement =
                    conn.prepareStatement(CREATE_QUERY, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, hashPassword(user.getPassword()));
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                user.setId(resultSet.getInt(1));
            }
            return user;
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DaoException("Blad tworzenia uzytkownika", ex);
        }
    }

    public User read(int userId) {
        try (Connection conn = DBUtil.connect("workshop2")) {
            PreparedStatement statement = conn.prepareStatement(READ_USER_QUERY);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setUsername(resultSet.getString("username"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                return user;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DaoException("Blad pobierania uzytkownika", ex);
        }
        return null;
    }

    public void update(User user) {
        try (Connection conn = DBUtil.connect("workshop2")) {
            PreparedStatement statement = conn.prepareStatement(UPDATE_USER_QUERY);
            statement.setString(1,user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setInt(3, user.getId());
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DaoException("Blad edycji uzytkownika", ex);
        }
    }

    public void updatePassword(User user, String password) {
        try (Connection conn = DBUtil.connect("workshop2")) {
            PreparedStatement statement = conn.prepareStatement(UPDATE_USER_PASSWORD_QUERY);
            statement.setString(1, hashPassword(password));
            statement.setInt(2, user.getId());
            statement. executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DaoException("Blad zmiany hasla uzytkownika", ex);
        }
    }

    public User[] findAll() {
        try (Connection conn = DBUtil.connect("workshop2")) {
            User[] users = new User[0];
            PreparedStatement statement = conn.prepareStatement(FIND_ALL_USERS_QUERY);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setUsername(resultSet.getString("username"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                users = addToArray(user, users);
            }
            return users;
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DaoException("Blad pobierania uzytkownikow", ex);
        }
    }

    public void delete(int userId) {
        try (Connection conn = DBUtil.connect("workshop2")) {
            PreparedStatement statement = conn.prepareStatement(DELETE_USER_QUERY);
            statement.setInt(1, userId);
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DaoException("Blad usuwania uzytkownika", ex);
        }
    }

    public void deleteAll() {
        try (Connection conn = DBUtil.connect("workshop2")) {
            Statement statement = conn.createStatement();
            statement.executeUpdate(DELETE_ALL_USERS_QUERY);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DaoException("Blad usuwania uzytkownikow", ex);
        }
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private User[] addToArray(User u, User[] users) {
        User[] tmpUsers = Arrays.copyOf(users, users.length + 1);
        tmpUsers[users.length] = u;
        return tmpUsers;
    }
}
