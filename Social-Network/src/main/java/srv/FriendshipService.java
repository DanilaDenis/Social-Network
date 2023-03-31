package srv;

import domain.Friendship;
import domain.RepoException;
import domain.User;
import repo.RepoFriendship;
import utils.events.UserEntityChangeEvent;
import utils.observer.Observable;
import utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;

public class FriendshipService implements Observable<UserEntityChangeEvent> {

    private final RepoFriendship repo;
    private final List<Observer<UserEntityChangeEvent>> observers = new ArrayList<>();

    public FriendshipService(RepoFriendship friendRepository) {
        repo = friendRepository;
    }

    public Iterable<User> allFriends(long id) {
        return repo.allFriends(id);
    }

    public Iterable<User> pending(long id) {
        return repo.pending(id);
    }

    @Override
    public void addObserver(Observer<UserEntityChangeEvent> e) {
        observers.add(e);

    }

    public void addFriend(Long id1, Long id2) {
        Friendship friendship = new Friendship(id1, id2);
        try {
            repo.save(friendship);
        } catch (RepoException repoException){
            throw repoException;
        }
    }
    public void deleteFriend(Long id1, Long id2) {
        Friendship friendship = new Friendship(id1, id2);
        repo.delete(friendship);
    }

    public void updateFriend(Long id1, Long id2, String state) {
        Friendship friendship = new Friendship(id1, id2);
        repo.update(friendship, state);
    }

    @Override
    public void removeObserver(Observer<UserEntityChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(UserEntityChangeEvent t) {

        observers.forEach(x -> x.update(t));
    }
}
