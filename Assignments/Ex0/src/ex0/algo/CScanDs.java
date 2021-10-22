package ex0.algo;

import ex0.CallForElevator;
import ex0.Elevator;

import java.util.ArrayList;
import java.util.Arrays;

public class CScanDs implements CustomDataStructure {
    private final int WAITING = 1, ACTIVE = 0;

    public final static int ONLY_UP = 1;
    public final static int ONLY_DOWN = -1;


    private Elevator elevator;
    private ArrayList<Integer> activeCalls;
    private ArrayList<Integer> waitingCalls;
    private int direction;
    private boolean isGoingToEnd;
    private int goingTo;


    public CScanDs(Elevator elevator, int direction) {
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
        if (elevator.getState() == Elevator.UP && direction == ONLY_UP && c.getSrc() >= elevator.getPos() ||
                elevator.getState() == Elevator.DOWN && direction == ONLY_DOWN && c.getSrc() <= elevator.getPos()) {
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
        //UP
        if (direction == ONLY_UP) {

            if (activeCalls.isEmpty() && elevator.getPos() != elevator.getMaxFloor()) {
                isGoingToEnd = true;
                activeCalls.add(elevator.getMaxFloor());
                return;
            }


            if (activeCalls.isEmpty() && elevator.getPos() == elevator.getMaxFloor()) {
                isGoingToEnd = false;
                activeCalls.add(elevator.getMinFloor());
                return;
            }
        }


        //DOWN

        if (direction == ONLY_DOWN) {
            if (activeCalls.isEmpty() && elevator.getPos() != elevator.getMinFloor()) {
                isGoingToEnd = true;
                activeCalls.add(elevator.getMinFloor());
                return;
            }

            if (activeCalls.isEmpty() && elevator.getPos() == elevator.getMinFloor()) {
                isGoingToEnd = false;
                activeCalls.add(elevator.getMaxFloor());
                return;
            }
        }


        if (activeCalls.isEmpty()) {
            activeCalls.addAll(waitingCalls);
            waitingCalls.clear();
        }
    }


    public int getDirection() {
        return direction;
    }
}
