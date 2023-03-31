package domain;

public class FriendshipValidator implements Validator<Friendship> {

    public void validate(Friendship entity) throws ValidationException {
        String errMsg = "";
        long user1 = entity.getUser1();
        long user2 = entity.getUser2();

        if (user1 == user2) {
            errMsg += "ID's cannot be the same\n";
        }

        if (!errMsg.equals("")) {
            throw new ValidationException(errMsg);
        }
    }
}
