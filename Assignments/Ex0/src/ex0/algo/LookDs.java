package ex0.algo;

import ex0.CallForElevator;
import ex0.Elevator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class LookDs implements CustomDataStructure {
    final static int UP = 1, DOWN = -1, ACTIVE = 0;

    private ArrayList<Integer> activeCalls;
    private ArrayList<Integer> downCalls;
    private ArrayList<Integer> upCalls;
    private int direction;
    private Elevator elevator;
    private int goingTo;


    public LookDs(Elevator elevator) {
        this.elevator = elevator;
        this.activeCalls = new ArrayList<>();
        this.downCalls = new ArrayList<>();
        this.upCalls = new ArrayList<>();
    }

    private int dist(int k, int v) {
        return Math.abs(k - v);
    }

    public int getEstimatedTimeToGetTo(int x) {
        int time = 0;
        for (int j = 0; j < activeCalls.size() && activeCalls.get(j) < x; j++) {
            if (j == 0)
                time += dist(elevator.getPos(), activeCalls.get(j)) / elevator.getSpeed();
            else
                time += dist(activeCalls.get(j), activeCalls.get(j - 1)) / elevator.getSpeed();

            time += elevator.getTimeForOpen() + elevator.getStartTime() + elevator.getStopTime() + elevator.getTimeForClose();
        }
        return time;
    }


    @Override
    public int getLast() {
        if (activeCalls.isEmpty())
            throw new RuntimeException("No active calls");
        return activeCalls.get(activeCalls.size() - 1);
    }

    @Override
    public int getFirst() {
        if (activeCalls.isEmpty())
            throw new RuntimeException("No active calls");
        return activeCalls.get(0);
    }

    @Override
    public int popLast() {
        if (activeCalls.isEmpty())
            throw new RuntimeException("No active calls");
        return activeCalls.remove(activeCalls.size() - 1);
    }

    @Override
    public int popFirst() {
        if (activeCalls.isEmpty())
            throw new RuntimeException("No active calls");
        return activeCalls.remove(0);
    }

    @Override
    public int getDirection() {
        return direction;
    }

    @Override
    public boolean hasActiveCalls() {
        return !activeCalls.isEmpty();
    }


    @Override
    public int numberOfCalls() {
        return downCalls.size() + upCalls.size() + activeCalls.size();
    }

    @Override
    public void stopped() {
        System.out.println("elevator - " + elevator.getID() + " -  stopped at " + elevator.getPos());
        if (elevator.getPos() != goingTo)
            sortedInsert(goingTo, ACTIVE);
    }

    @Override
    public int getNext() {
        feedCalls();

        System.out.println("\n\nelevator " + elevator.getID() + " position " + elevator.getPos() + "\nActive" + Arrays.toString(activeCalls.toArray()));
        System.out.println("Up " + Arrays.toString(upCalls.toArray()));
        System.out.println("Down " + Arrays.toString(downCalls.toArray()) + "   \n\n");

        if (activeCalls.isEmpty())
            return Integer.MAX_VALUE;

        if (direction == CallForElevator.UP) {
            goingTo = activeCalls.get(0);
            System.out.println(elevator.getID() + " is going to " + goingTo);
            return activeCalls.remove(0);
        }

        goingTo = activeCalls.get(activeCalls.size() - 1);
        System.out.println(elevator.getID() + " is going to " + goingTo);
        return activeCalls.remove(activeCalls.size() - 1);
    }


    @Override
    public void add(CallForElevator c) {
        System.out.println("  " + elevator.getID() + "  " + "incoming call   " + c.getSrc() + "--->" + c.getDest());


        // active calls empty
        if (activeCalls.isEmpty() && downCalls.isEmpty() && upCalls.isEmpty()) {
            if (c.getType() == CallForElevator.UP)
                direction = CallForElevator.UP;
            else
                direction = CallForElevator.DOWN;

            sortedInsert(c.getSrc(), ACTIVE);
            sortedInsert(c.getDest(), ACTIVE);
            return;
        }


        //ON THE WAY
        if (elevator.getState() == Elevator.UP && c.getSrc() >= elevator.getPos() && c.getType() == CallForElevator.UP && direction == UP ||
                elevator.getState() == Elevator.DOWN && c.getSrc() <= elevator.getPos() && c.getType() == CallForElevator.DOWN && direction == DOWN) {
            sortedInsert(c.getSrc(), ACTIVE);
            sortedInsert(c.getDest(), ACTIVE);
            return;
        }


        //waiting up
        if (c.getType() == CallForElevator.UP) {
            sortedInsert(c.getSrc(), UP);
            sortedInsert(c.getDest(), UP);
            return;
        }

        //waiting down
        if (c.getType() == CallForElevator.DOWN) {
            sortedInsert(c.getSrc(), DOWN);
            sortedInsert(c.getDest(), DOWN);
        }

    }


    private void feedCalls() {
        if (direction == CallForElevator.UP && activeCalls.isEmpty()) {
            direction = DOWN;
            feedDown();
        } else if (activeCalls.isEmpty()) {
            direction = UP;
            feedUp();
        }
    }


    private void feedUp() {
        activeCalls.addAll(upCalls);
        upCalls.clear();
    }


    private void feedDown() {
        activeCalls.addAll(downCalls);
        downCalls.clear();
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
        } else if (type == UP) {
            while (i < upCalls.size() && val >= upCalls.get(i)) {
                if (val == upCalls.get(i))
                    return;
                i++;
            }
            upCalls.add(i, val);
        } else {
            while (i < downCalls.size() && val >= downCalls.get(i)) {
                if (val == downCalls.get(i))
                    return;
                i++;
            }
            downCalls.add(i, val);
        }
    }

}
