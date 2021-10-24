package ex0.algo;

import ex0.CallForElevator;
import ex0.Elevator;

import java.util.ArrayList;
import java.util.Arrays;

public class CLookDs implements CustomDataStructure {
    private final int WAITING = 1, ACTIVE = 0;

    public final static int ONLY_UP = 1;
    public final static int ONLY_DOWN = -1;


    private Elevator elevator;
    private ArrayList<Integer> activeCalls;
    private ArrayList<Integer> waitingCalls;
    private int direction;
    private boolean isGoingToEnd;
    private int goingTo;


    public CLookDs(Elevator elevator, int direction) {
        this.elevator = elevator;
        this.direction = direction;
        activeCalls = new ArrayList<>();
        waitingCalls = new ArrayList<>();
    }


    @Override
    public void stopped() {
        if (elevator.getPos() != goingTo)
            sortedInsert(goingTo, ACTIVE);
    }

    @Override
    public int getFirst() {
        if (activeCalls.isEmpty())
            throw new RuntimeException("No active calls");
        return activeCalls.get(0);
    }

    @Override
    public int getLast() {
        if (activeCalls.isEmpty())
            throw new RuntimeException("No active calls");
        return activeCalls.get(activeCalls.size() - 1);
    }

    @Override
    public int popFirst() {
        if (activeCalls.isEmpty())
            throw new RuntimeException("No active calls");
        return activeCalls.remove(0);
    }

    @Override
    public int popLast() {
        if (activeCalls.isEmpty())
            throw new RuntimeException("No active calls");
        return activeCalls.remove(activeCalls.size() - 1);
    }

    @Override
    public int numberOfCalls() {
        return activeCalls.size() + waitingCalls.size();
    }

    public boolean hasCalls() {
        if (activeCalls.isEmpty() && waitingCalls.isEmpty())
            return false;
        return true;
    }

    @Override
    public boolean hasActiveCalls() {
        return !activeCalls.isEmpty();
    }

    private int dist(int floor1, int floor2) {
        return Math.abs(floor1 - floor2);
    }


    public int estimatedTimeToGet(int floor) {
        int time = 0;
        if (direction == ONLY_UP && !activeCalls.isEmpty()) {
            if (elevator.getState() == Elevator.LEVEL) {
                time += elevator.getTimeForClose() + elevator.getStartTime();
            }

            if (goingTo != Integer.MAX_VALUE)
                time += dist(goingTo, elevator.getPos()) / elevator.getSpeed();


            for (int i = 0; i < activeCalls.size() && activeCalls.get(i) < floor; i++) {
                if (i == 0) {
                    time += dist(goingTo, activeCalls.get(i)) / elevator.getSpeed();

                } else {
                    time += dist(activeCalls.get(i - 1), activeCalls.get(i)) / elevator.getSpeed();

                }
                time += elevator.getStopTime() + elevator.getTimeForOpen() + elevator.getTimeForClose() + elevator.getStartTime();


            }

        } else if (direction == ONLY_DOWN) {

            if (elevator.getState() == Elevator.LEVEL) {
                time += elevator.getTimeForClose() + elevator.getStartTime();
            }

            if (goingTo != Integer.MAX_VALUE)
                time += dist(goingTo, elevator.getPos()) / elevator.getSpeed();


            for (int i = activeCalls.size() - 1; i >= 0 && activeCalls.get(i) > floor; i--) {
                if (i == activeCalls.size() - 1) {
                    time += dist(goingTo, activeCalls.get(i)) / elevator.getSpeed();

                } else {
                    time += dist(activeCalls.get(i + 1), activeCalls.get(i)) / elevator.getSpeed();
                }
                time += elevator.getStopTime() + elevator.getTimeForOpen() + elevator.getTimeForClose() + elevator.getStartTime();
            }
        }

        return time;
    }


    @Override
    public int getNext() {
        feedCalls();

        System.out.println("  " + elevator.getID() + "  Active" + Arrays.toString(activeCalls.toArray()));
        System.out.println(Arrays.toString(activeCalls.toArray()));
        System.out.println(Arrays.toString(waitingCalls.toArray()));

        if (activeCalls.isEmpty())
            return Integer.MAX_VALUE;

        if (direction == ONLY_UP) {
            goingTo = activeCalls.remove(0);
            return goingTo;
        }
        goingTo = activeCalls.remove(activeCalls.size() - 1);
        return goingTo;
    }


    @Override
    public void add(CallForElevator c) {
        System.out.println((direction == ONLY_UP ? "onlyup  " : "onlydown  ") +
                "  " + elevator.getID() + "  " + "incoming call   " + c.getSrc() + "--->" + c.getDest() + "  " + c.getType());


        //ELEVATOR IS RESTING
        if (direction == ONLY_UP && elevator.getState() == Elevator.DOWN || direction == ONLY_DOWN && elevator.getState() == Elevator.UP) {
            //add to active
            sortedInsert(c.getSrc(), ACTIVE);
            sortedInsert(c.getDest(), ACTIVE);
            return;
        }


        //ON THE WAY
        if ( direction == ONLY_UP && c.getSrc() >= elevator.getPos() ||
                 direction == ONLY_DOWN && c.getSrc() <= elevator.getPos()) {
            sortedInsert(c.getSrc(), ACTIVE);
            sortedInsert(c.getDest(), ACTIVE);
            return;
        }


        // NOT ON THE WAY -> WAITING LIST
        sortedInsert(c.getSrc(), WAITING);
        sortedInsert(c.getDest(), WAITING);

    }


    private void sortedInsert(int val, int type) {
        int i = 0;

        if (type == ACTIVE) {
            while (i < activeCalls.size() && val >= activeCalls.get(i)) {
                if (val == activeCalls.get(i))
                    return;
                i++;
            }
            activeCalls.add(i, val);
        } else {
            while (i < waitingCalls.size() && val >= waitingCalls.get(i)) {
                if (val == waitingCalls.get(i))
                    return;
                i++;
            }
            waitingCalls.add(i, val);
        }
    }


    private void feedCalls() {
        if (activeCalls.isEmpty()) {
            activeCalls.addAll(waitingCalls);
            waitingCalls.clear();
        }
    }


    public int getDirection() {
        return direction;
    }
}
