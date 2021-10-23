package ex0.algo;

import ex0.CallForElevator;
import ex0.Elevator;

import java.util.ArrayList;
import java.util.Comparator;

public class SSTFDs implements CustomDataStructure {
    private final static int UP = 1, DOWN = -1;
    private ArrayList<Integer> activeCalls;
    private Elevator elev;
    private int direction;
    private int goTo;

    public SSTFDs(Elevator elev) {
        this.elev = elev;
        activeCalls = new ArrayList<>();
    }


    @Override
    public int getNext() {
        if (activeCalls.isEmpty())
            throw new RuntimeException();

        int next = activeCalls.remove(getClosest());
        goTo = next;

        if (elev.getPos() - next > 0)
            direction = DOWN;
        else
            direction = UP;
        return next;
    }


    private int dist(int val) {
        return Math.abs(elev.getPos() - val);
    }

    private int getClosest() {
        int p = -1;

        for (int i = 0; i < activeCalls.size(); i++) {
            if (p == -1)
                p = i;
            else if (dist(activeCalls.get(p)) > dist(activeCalls.get(i)))
                p = i;
        }
        return p;
    }

    //sort by distance
    @Override
    public void add(CallForElevator c) {
        activeCalls.add(c.getSrc());
        activeCalls.add(c.getDest());
    }

    private void sortedInsert(int v) {
        int i = 0;
        int d = dist(v);
        while (i < activeCalls.size() && d > dist(activeCalls.get(i))) {
            i++;
        }
        activeCalls.add(i, v);
    }

    public void updateDistance() {
        for (int i = 0; i < activeCalls.size(); i++) {
            activeCalls.set(i, dist(activeCalls.get(i)));
        }
        activeCalls.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return Integer.compare(o1, o2);
            }
        });
    }


    @Override
    public void stopped() {

    }

    @Override
    public int getLast() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getFirst() {
        if (activeCalls.isEmpty())
            throw new RuntimeException();
        return activeCalls.get(0);
    }

    @Override
    public int popFirst() {
        if (activeCalls.isEmpty())
            throw new RuntimeException();

        return activeCalls.remove(0);
    }

    @Override
    public int popLast() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean hasActiveCalls() {
        return !activeCalls.isEmpty();
    }

    @Override
    public int getDirection() {
        return direction;
    }

    @Override
    public int numberOfCalls() {
        return activeCalls.size();
    }
}
