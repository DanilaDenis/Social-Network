package domain;

import java.time.LocalDateTime;

public class Friendship extends Entity<Tuple<Long, Long>> {

    private final Tuple<Long, Long> relation;
    private String status;

    public Friendship(long user1, long user2) {
        this.relation = new Tuple<>(user1, user2);
        this.status = "pending";
    }

    public Friendship(long user1, long user2, LocalDateTime friendsFrom) {
        this.relation = new Tuple<>(user1, user2);
        relation.setRight(user2);
        this.status = "accepted";
    }

    public long getUser1() {
        return relation.getLeft();
    }

    public long getUser2() {
        return relation.getRight();
    }

    public void setStatus(String t) {
        status = t;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Friendship that)) return false;
        return (getUser1() == that.getUser1() &&
                getUser2() == that.getUser2()) ||
                (getUser1() == that.getUser2() &&
                        getUser2() == that.getUser1());
    }
}
