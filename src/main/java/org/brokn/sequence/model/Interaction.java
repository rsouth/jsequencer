package org.brokn.sequence.model;

public class Interaction {

    private final Lane fromLane;
    private final Lane toLane;
    private final String message;
    private final int index;

    public Interaction(Lane fromLane, Lane toLane, String message, int index) {
        this.fromLane = fromLane;
        this.toLane = toLane;
        this.message = message;
        this.index = index;
    }

    public Lane getFromLane() {
        return fromLane;
    }

    public Lane getToLane() {
        return toLane;
    }

    public String getMessage() {
        return message;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "Interaction{" +
                "fromLane=" + fromLane +
                ", toLane=" + toLane +
                ", message='" + message + '\'' +
                ", index=" + index +
                '}';
    }

}