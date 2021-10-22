package ex0.algo;

import ex0.CallForElevator;

interface CustomDataStructure {
    public int getNext();

    public void add(CallForElevator c);

    public void stopped();

    public int getLast();

    public int getFirst();

    public int popFirst();

    public int popLast();

    public boolean hasActiveCalls();

    public int getDirection();

    public int numberOfCalls();
}
