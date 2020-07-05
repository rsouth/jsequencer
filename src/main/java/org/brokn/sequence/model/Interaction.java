package org.brokn.sequence.model;

public class Interaction {
//    private String fromNode;
//    private String toNode;

    private final Lane fromLane;
    private final Lane toLane;
    private final String message;

    public Interaction(Lane fromLane, Lane toLane, String message) {
        this.fromLane = fromLane;
        this.toLane = toLane;
        this.message = message;
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

    @Override
    public String toString() {
        return "Interaction{" +
                "fromLane=" + fromLane +
                ", toLane=" + toLane +
                ", message='" + message + '\'' +
                '}';
    }
}