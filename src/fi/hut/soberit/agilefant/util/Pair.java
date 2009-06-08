package fi.hut.soberit.agilefant.util;

public class Pair<First, Second> {

    public final First first;

    public final Second second;

    public Pair(First first, Second second) {
        this.first = first;
        this.second = second;
    }

    public First getFirst() {
        return first;
    }

    public Second getSecond() {
        return second;
    }

    public static <First, Second> Pair<First, Second> create(First first, Second second) {
        return new Pair<First, Second>(first, second);
    }

}
