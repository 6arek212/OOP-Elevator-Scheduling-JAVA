package ex0.algo;

import ex0.CallForElevator;
import ex0.Elevator;

import java.util.ArrayList;
import java.util.Arrays;

public class CustomDs {
    private final int UP = 1, DOWN = -1, ACTIVE = 0;

    private int elevatorNumber;
    private ArrayList<Integer> activeCalls;
    private ArrayList<Integer> downCalls;
    private ArrayList<Integer> upCalls;
    private int direction;
    private Elevator elevator;



    public CustomDs(int elevatorNumber, Elevator elevator) {
        this.elevator = elevator;
        this.elevatorNumber = elevatorNumber;
        this.activeCalls = new ArrayList<>();
        this.downCalls = new ArrayList<>();
        this.upCalls = new ArrayList<>();
    }

    public int getNext() {
        if (activeCalls.isEmpty())
            feedCalls();

        System.out.println("  " + elevatorNumber + "  Active" + Arrays.toString(activeCalls.toArray()));
        System.out.println(Arrays.toString(upCalls.toArray()));
        System.out.println(Arrays.toString(downCalls.toArray()));


        if (activeCalls.isEmpty())
            return Integer.MAX_VALUE;

        if (direction == CallForElevator.UP)
            return activeCalls.remove(0);

        return activeCalls.remove(activeCalls.size() - 1);
    }

    public void add(CallForElevator c) {
        System.out.println("  " + elevatorNumber + "  " + "incoming call   " + c.getSrc() + "--->" + c.getDest());


        // active calls empty
        if (activeCalls.isEmpty() && downCalls.isEmpty() && upCalls.isEmpty()) {
            if (c.getType() == CallForElevator.UP)
                direction = CallForElevator.UP;
            else
                direction = CallForElevator.DOWN;

            activeCalls.add(c.getSrc());
            activeCalls.add(c.getDest());
            return;
        }


        // on the way up
        if (elevator.getState() == Elevator.UP &&
                c.getType() == CallForElevator.UP &&
                c.getSrc() >= elevator.getPos()) {

            sortedInsert(c.getSrc(), ACTIVE);
            sortedInsert(c.getDest(), ACTIVE);

        } else if (c.getType() == CallForElevator.UP) {
            sortedInsert(c.getSrc(), UP);
            sortedInsert(c.getDest(), UP);
        }


        // on the way down
        if (direction == CallForElevator.DOWN &&
                c.getType() == CallForElevator.DOWN &&
                c.getSrc() <= elevator.getPos()) {

            sortedInsert(c.getSrc(), ACTIVE);
            sortedInsert(c.getDest(), ACTIVE);

        } else if (c.getType() == CallForElevator.DOWN) {
            sortedInsert(c.getSrc(), DOWN);
            sortedInsert(c.getDest(), DOWN);
        }

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


    private void feedCalls() {
        if (direction == CallForElevator.UP && activeCalls.isEmpty()) {
            direction = DOWN;
            feedDown();
        } else {
            direction = UP;
            feedUp();
        }
    }


    private void feedUp() {
        for (int i = 0; i < upCalls.size(); i++) {
            activeCalls.add(upCalls.remove(i));
        }
    }


    private void feedDown() {
        for (int i = 0; i < downCalls.size(); i++) {
            activeCalls.add(downCalls.remove(i));
        }
    }

}
