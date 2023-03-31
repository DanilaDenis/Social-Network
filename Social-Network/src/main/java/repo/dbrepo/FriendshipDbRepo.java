package repo.dbrepo;

import domain.Friendship;
import domain.FriendshipValidator;
import domain.RepoException;
import domain.User;
import repo.RepoFriendship;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Vector;

public class FriendshipDbRepo implements RepoFriendship {
    private final String url;
    private final String username;
    private final String password;

    public FriendshipDbRepo(String url, String username, String password, FriendshipValidator validator) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public void delete(Friendship f) {
        long id = search(f);
        String sql = "DELETE FROM friendship WHERE id=" + id;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public int search(Friendship f) throws RepoException {
        String sql = "SELECT * FROM friendship WHERE (sender= " + f.getUser1() + " AND receiver=" + f.getUser2() + " ) OR (sender=" + f.getUser2() + " AND receiver=" + f.getUser1() + ")";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql);
             var result = ps.executeQuery()) {
            result.next();
            return result.getInt("id");
        } catch (SQLException e) {
            //e.printStackTrace();
        }
        return -1;
    }

    @Override
    public Vector<User> allFriends(long id) {
        long friend = 0;
        Vector<User> friends = new Vector<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("SELECT * from friendship where status ='accepted' ");
             var result = ps.executeQuery()) {
            while (result.next()) {
                long user1 = result.getLong("sender");
                long user2 = result.getLong("receiver");
                if (user1 == id) {
                    friend = user2;
                }
                if (user2 == id) {
                    friend = user1;
                }
                String sql = "SELECT * FROM users WHERE id=" + friend;
                PreparedStatement statement = connection.prepareStatement(sql);
                var res = statement.executeQuery();
                if (res.next()) {
                    String firstName = res.getString("first_name");
                    String lastName = res.getString("last_name");
                    User user = new User(firstName, lastName);
                    user.setId(friend);
                    friends.add(user);
                }
            }
            return friends;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }

    @Override
    public Vector<User> pending(long id) {
        Vector<User> friends = new Vector<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("SELECT * from friendship where status !='accepted' ");
             var result = ps.executeQuery()) {
            while (result.next()) {
                long sender = result.getLong("sender");
                long receiver = result.getLong("receiver");
                if (receiver == id) {
                    String sql = "SELECT * FROM users WHERE id=" + sender;
                    PreparedStatement statement = connection.prepareStatement(sql);
                    var res = statement.executeQuery();
                    res.next();
                    String firstName = res.getString("first_name");
                    String lastName = res.getString("last_name");
                    User user = new User(firstName, lastName);
                    user.setId(sender);
                    friends.add(user);
                }
            }
            return friends;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }

    @Override
    public void save(Friendship f) {

        String sql = "insert into friendship (sender, receiver,status) values (?,?,?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            if(search(f) != -1)
                throw new RepoException("Friendship already exists");
            if(f.getUser1() == f.getUser2())
                throw new RepoException("Cannot befriend yourself");
            ps.setLong(1, f.getUser1());
            ps.setLong(2, f.getUser2());
            Timestamp t = Timestamp.valueOf(LocalDateTime.now());
            //ps.setTimestamp(3, t);
            ps.setString(3, "pending");
            ps.executeUpdate();
        } catch (SQLException e) {
            // e.printStackTrace();
        } catch (RepoException repoException){
            throw repoException;

        }    }

    @Override
    public void update(Friendship f, String state) {
        String sql = "update friendship set status= ? where id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, state);
            ps.setLong(2, search(f));
            ps.executeUpdate();
        } catch (SQLException e) {
            // e.printStackTrace();
        }
    }
}