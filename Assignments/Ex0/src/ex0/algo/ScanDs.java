package ex0.algo;

import ex0.CallForElevator;
import ex0.Elevator;

import java.util.ArrayList;
import java.util.Arrays;

public class ScanDs implements CustomDataStructure {
    final static int UP = 1, DOWN = -1, ACTIVE = 0;

    private ArrayList<Integer> activeCalls;
    private ArrayList<Integer> downCalls;
    private ArrayList<Integer> upCalls;
    private int direction;
    private Elevator elevator;
    private int goingTo;


    public ScanDs(Elevator elevator) {
        this.elevator = elevator;
        this.activeCalls = new ArrayList<>();
        this.downCalls = new ArrayList<>();
        this.upCalls = new ArrayList<>();
    }

    public int getLast() {
        if (activeCalls.isEmpty())
            throw new RuntimeException("No active calls");
        return activeCalls.get(activeCalls.size() - 1);
    }


    public int getFirst() {
        if (activeCalls.isEmpty())
            throw new RuntimeException("No active calls");
        return activeCalls.get(0);
    }


    @Override
    public int numberOfCalls() {
        return activeCalls.size() + downCalls.size() + upCalls.size();
    }

    public int popLast() {
        if (activeCalls.isEmpty())
            throw new RuntimeException("No active calls");
        return activeCalls.remove(activeCalls.size() - 1);
    }


    public int popFirst() {
        if (activeCalls.isEmpty())
            throw new RuntimeException("No active calls");
        return activeCalls.remove(0);
    }

    public int getDirection() {
        return direction;
    }

    public boolean hasActiveCalls() {
        return !activeCalls.isEmpty();
    }


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
        if (direction == CallForElevator.UP && activeCalls.isEmpty() && elevator.getPos() != elevator.getMaxFloor()) {
            activeCalls.add(elevator.getMaxFloor());
        } else if (direction == CallForElevator.UP && activeCalls.isEmpty() && elevator.getPos() == elevator.getMaxFloor()) {
            direction = DOWN;
            feedDown();
        } else if (direction == CallForElevator.DOWN && activeCalls.isEmpty() && elevator.getPos() != elevator.getMinFloor()) {
            activeCalls.add(elevator.getMinFloor());
        }
        else if (direction == CallForElevator.DOWN && activeCalls.isEmpty() && elevator.getPos() == elevator.getMinFloor()) {
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
