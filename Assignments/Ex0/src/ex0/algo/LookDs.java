package ex0.algo;

import ex0.CallForElevator;
import ex0.Elevator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class LookDs {
    final static int UP = 1, DOWN = -1, LEVEL = 0;

    private ArrayList<Integer> activeCalls;
    private ArrayList<Integer> downCalls;
    private ArrayList<Integer> upCalls;
    private int direction = 0;
    private Elevator elevator;
    private int goingTo;


    public LookDs(Elevator elevator) {
        this.elevator = elevator;
        this.activeCalls = new ArrayList<>();
        this.downCalls = new ArrayList<>();
        this.upCalls = new ArrayList<>();
        this.direction = LEVEL;
    }


    private int dist(int floor1, int floor2) {
        return Math.abs(floor1 - floor2);
    }


    public void stopped() {
        if (elevator.getPos() != goingTo)
            sortedInsert(goingTo, activeCalls);
    }


    public int getNext() {
        feedCalls();
        if (activeCalls.isEmpty()) {
            goingTo = Integer.MAX_VALUE;
            direction = LEVEL;
            return Integer.MAX_VALUE;
        }


        if (direction == CallForElevator.UP) {
            goingTo = activeCalls.get(0);
            return activeCalls.remove(0);
        }

        goingTo = activeCalls.get(activeCalls.size() - 1);
        return activeCalls.remove(activeCalls.size() - 1);
    }


    public void add(CallForElevator c) {

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
        if (c.getSrc() >= elevator.getPos() && c.getType() == CallForElevator.UP && direction == UP ||
                c.getSrc() <= elevator.getPos() && c.getType() == CallForElevator.DOWN && direction == DOWN) {
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


    // feed the active calls list
    private void feedCalls() {
        if (direction == UP && activeCalls.isEmpty() && !downCalls.isEmpty()) {
            direction = DOWN;
            feedDown();
        } else if (direction == UP && activeCalls.isEmpty()) {
            feedUp();
        } else if (direction == DOWN && activeCalls.isEmpty() && !upCalls.isEmpty()) {
            direction = UP;
            feedUp();
        } else if (direction == DOWN && activeCalls.isEmpty()) {
            feedDown();
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


    private void sortedInsert(int val, ArrayList<Integer> list) {
        int i = 0;

        while (i < list.size() && val >= list.get(i)) {
            if (val == list.get(i))
                return;
            i++;
        }
        list.add(i, val);
    }


    public int estimatedTimeToGet(CallForElevator call) {
        int time = 0;
        // if it on the way -> time till get to this call
        if (direction == UP && call.getType() == CallForElevator.UP && elevator.getPos() <= call.getSrc()) {
            time = estimatedTimeToGet(call.getSrc());
        }

        // down -> active + call
        else if (direction == DOWN && call.getType() == CallForElevator.UP || direction == UP && call.getType() == CallForElevator.DOWN) {
            if (hasActiveCalls()) {
                time += timeToFinishActive();
                time += dist(getLast(), call.getSrc()) / elevator.getSpeed();
            } else {
                time += dist(elevator.getPos(), call.getSrc()) / elevator.getSpeed();
            }

        } else if (direction == DOWN && call.getType() == CallForElevator.DOWN && elevator.getPos() >= call.getSrc()) {
            time = estimatedTimeToGet(call.getSrc());
        }
        return time;
    }


    public int timeToFinishActive() {
        int time = 0;
        if (!activeCalls.isEmpty()) {
            if (elevator.getState() == Elevator.LEVEL) {
                time += elevator.getTimeForClose() + elevator.getStartTime();
            }

            if (goingTo != Integer.MAX_VALUE) {
                time += elevator.getStopTime() + elevator.getTimeForClose();
                time += dist(goingTo, elevator.getPos()) / elevator.getSpeed();
            }

            for (int j = 0; j < activeCalls.size(); j++) {
                int i = 0;
                if (direction == DOWN) {
                    j = activeCalls.size() - j;
                } else
                    i = j;


                time += elevator.getStartTime() + elevator.getTimeForOpen();

                if (i == 0 && direction == UP || i == activeCalls.size() - 1 && direction == DOWN) {
                    if (goingTo != Integer.MAX_VALUE) {
                        time += dist(goingTo, activeCalls.get(i)) / elevator.getSpeed();
                    } else
                        time += dist(elevator.getPos(), activeCalls.get(i)) / elevator.getSpeed();
                } else {
                    if (direction == UP)
                        time += dist(activeCalls.get(i - 1), activeCalls.get(i)) / elevator.getSpeed();
                    else {
                        time += dist(activeCalls.get(i + 1), activeCalls.get(i)) / elevator.getSpeed();
                    }
                }
                time += elevator.getStopTime() + elevator.getTimeForClose();
            }

        }

        return time;
    }


    private int estimatedTimeToGet(int floor) {
        int time = 0;
        if (direction == UP && !activeCalls.isEmpty()) {
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

        } else if (direction == DOWN) {

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

    public boolean hasCalls() {
        return !activeCalls.isEmpty() || !downCalls.isEmpty() || !upCalls.isEmpty();
    }

    public int numberOfCalls() {
        return downCalls.size() + upCalls.size() + activeCalls.size();
    }

    public int numberOfActiveCalls() {
        return activeCalls.size();
    }

}
