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

    public boolean hasCalls() {
        return !activeCalls.isEmpty() || !downCalls.isEmpty() || !upCalls.isEmpty();
    }

    @Override
    public int numberOfCalls() {
        return downCalls.size() + upCalls.size() + activeCalls.size();
    }

    @Override
    public void stopped() {
        System.out.println("elevator - " + elevator.getID() + " -  stopped at " + elevator.getPos());
        if (elevator.getPos() != goingTo)
            sortedInsert(goingTo, activeCalls);
    }


    @Override
    public int getNext() {
        feedCalls();

        System.out.println("\n\nelevator " + elevator.getID() + " position " + elevator.getPos() + "\nActive" + Arrays.toString(activeCalls.toArray()));
        System.out.println("Up " + Arrays.toString(upCalls.toArray()));
        System.out.println("Down " + Arrays.toString(downCalls.toArray()) + "   \n\n");

        if (activeCalls.isEmpty()) {
            return Integer.MAX_VALUE;
        }


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

            sortedInsert(c.getSrc(), activeCalls);
            sortedInsert(c.getDest(), activeCalls);
            return;
        }


        //ON THE WAY
        if (elevator.getState() == Elevator.UP && c.getSrc() >= elevator.getPos() && c.getType() == CallForElevator.UP && direction == UP ||
                elevator.getState() == Elevator.DOWN && c.getSrc() <= elevator.getPos() && c.getType() == CallForElevator.DOWN && direction == DOWN) {
            sortedInsert(c.getSrc(), activeCalls);
            sortedInsert(c.getDest(), activeCalls);
            return;
        }


        //waiting up
        if (c.getType() == CallForElevator.UP) {
            sortedInsert(c.getSrc(), upCalls);
            sortedInsert(c.getDest(), upCalls);
            return;
        }

        //waiting down
        if (c.getType() == CallForElevator.DOWN) {
            sortedInsert(c.getSrc(), downCalls);
            sortedInsert(c.getDest(), downCalls);
        }

    }


    private void feedCalls() {
        if (direction == UP && activeCalls.isEmpty()) {
            direction = DOWN;
            feedDown();
        } else if (direction == DOWN && activeCalls.isEmpty()) {
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


    private void sortedInsert(int val , ArrayList<Integer> list) {
        int i = 0;

        while (i < list.size() && val >= list.get(i)) {
            if (val == list.get(i))
                return;
            i++;
        }
        list.add(i, val);
    }


}
