package org.brokn.sequence.model;

public class Lane {

    private final int index;
    private final String name;

    public Lane(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Lane{" +
                "index=" + index +
                ", name='" + name + '\'' +
                '}';
    }
}
