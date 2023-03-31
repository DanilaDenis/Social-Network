package repo;

import domain.Friendship;
import domain.User;
import domain.ValidationException;

import java.util.Vector;

public interface RepoFriendship {
    /**
     * removes the entity with the specified id
     *
     * @param f f must be not null
     */
    void delete(Friendship f);

    /**
     * search a friendship
     *
     * @param f f must be not null
     */
    int search(Friendship f);

    /**
     * searches for all friendships of an id
     *
     * @param id id must be not null
     */

    Vector<User> allFriends(long id);

    Vector<User> pending(long id);

    /**
     * @param entity entity must be not null
     * @throws ValidationException      if the entity is not valid
     * @throws IllegalArgumentException if the given entity is null.
     */
    void save(Friendship entity);

    void update(Friendship friendship, String state);

}

