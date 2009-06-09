package fi.hut.soberit.agilefant.util;

public class Pair<First, Second> {

    public static final Pair<?, ?> EMPTY = create(null, null);
    
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((first == null) ? 0 : first.hashCode());
        result = prime * result + ((second == null) ? 0 : second.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof Pair)) return false;
        Pair<?, ?> other = (Pair<?, ?>) obj;
        if (first == null) {
            if (other.first != null) return false;
        } else if (!first.equals(other.first)) return false;
        if (second == null) {
            if (other.second != null) return false;
        } else if (!second.equals(other.second)) return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{[");
        builder.append(first.toString());
        builder.append("], [");
        builder.append(second.toString());
        builder.append("]}");
        return builder.toString();
    }

    public static <First, Second> Pair<First, Second> create(First first,
            Second second) {
        return new Pair<First, Second>(first, second);
    }

}
