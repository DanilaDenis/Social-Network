package srv;

import domain.RepoException;
import domain.User;
import repo.Repository;
import utils.events.ChangeEventType;
import utils.events.UserEntityChangeEvent;
import utils.observer.Observable;
import utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserService implements Observable<UserEntityChangeEvent> {
    private final Repository<Long, User> repo;
    private final List<Observer<UserEntityChangeEvent>> observers = new ArrayList<>();

    public UserService(Repository<Long, User> repo) {
        this.repo = repo;
    }


    public User addUser(User user) {
        if (repo.save(user).isEmpty()) {
            UserEntityChangeEvent event = new UserEntityChangeEvent(ChangeEventType.ADD, user);
            notifyObservers(event);
            return null;
        }
        return user;
    }

    public User deleteUser(String email) {
        Optional<User> user = repo.delete(email);
        if (user.isPresent()) {
            notifyObservers(new UserEntityChangeEvent(ChangeEventType.DELETE, user.get()));
            return user.get();
        }
        return null;
    }
    public User findUser(String email){
        try{
            return repo.findOne(email);
        }catch (RepoException repoException){
            throw repoException;

        }
    }
    public void updateUser(Long id, User user) {
        repo.update(id, user);
        notifyObservers(new UserEntityChangeEvent(ChangeEventType.UPDATE, user));
    }

    @Override
    public void addObserver(Observer<UserEntityChangeEvent> e) {
        observers.add(e);

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
