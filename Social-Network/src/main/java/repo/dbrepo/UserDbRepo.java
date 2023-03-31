package repo.dbrepo;

import domain.RepoException;
import domain.User;
import domain.ValidationException;
import domain.Validator;
import repo.Repository;

import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UserDbRepo implements Repository<Long, User> {
    private String url;
    private String username;
    private String password;
    private Validator<User> validator;

    public UserDbRepo(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public User findOne(String emailText) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE email= ?"))
        {
             ps.setString(1,emailText);
             ResultSet result = ps.executeQuery();
             if (result.isBeforeFirst())
             {
                 result.next();
                 Long id = result.getLong("id");
                 String firstName = result.getString("first_name");
                 String lastName = result.getString("last_name");
                 User user = new User(firstName, lastName, emailText);
                 user.setId(id);
                 return user;
             }
             return null;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");

                User user = new User(firstName, lastName);
                user.setId(id);
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public Optional<User> save(User entity) {
        String sql = "insert into users (first_name, last_name) values (?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            validator.validate(entity);
            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());
            ps.executeUpdate();
        } catch (SQLException e) {
            //e.printStackTrace();
            return Optional.ofNullable(entity);
        }catch (ValidationException validationException){
            throw validationException;
        }catch (RepoException repoException){
            throw new RepoException("Email in use");
        }        return Optional.empty();
    }

    @Override
    public Optional<User> delete(String email) {
        String sql = "DELETE FROM users WHERE email=" + email;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql);
        ) {
            User user = this.findOne(email);
            ps.executeUpdate();
            return Optional.of(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> update(Long id, User entity) {
        String sql = "UPDATE users SET first_name = '" + entity.getFirstName() + "', last_name = '" + entity.getLastName() + "' WHERE id = " + id;
        validator.validate(entity);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql);
        ) {
            ps.executeUpdate();

        } catch (SQLException e) {
        }
        return Optional.of(entity);
    }
}
