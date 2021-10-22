package ex0.algo;

import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;

import java.util.HashSet;

public class LookAlgo implements ElevatorAlgo {
    public static final int UP = 1, DOWN = -1, LEVEL = 0;

    private Building building;
    private String algoName = "Scan";
    private int elevatorAllocation;
    private CustomDataStructure[] callsManager;

    public LookAlgo(Building building) {
        this.building = building;
        callsManager = new CustomDataStructure[building.numberOfElevetors()];
        for (int i = 0; i < building.numberOfElevetors(); i++) {
            callsManager[i] = new LookDs(building.getElevetor(i));
        }
    }

    @Override
    public Building getBuilding() {
        return building;
    }

    @Override
    public String algoName() {
        return algoName;
    }


    private int getOnTheWayElevator(CallForElevator c) {
        int pickedElevator = -1;

        if (c.getType() == CallForElevator.UP) {
            for (int i = 0; i < building.numberOfElevetors(); i++) {
                Elevator el = building.getElevetor(i);

                if (el.getState() == Elevator.UP && el.getPos() <= c.getSrc()) {
                    if (pickedElevator == -1)
                        pickedElevator = i;
                    else if (el.getPos() > building.getElevetor(pickedElevator).getPos())
                        pickedElevator = i;
                }
            }

        } else {
            for (int i = 0; i < building.numberOfElevetors(); i++) {
                Elevator el = building.getElevetor(i);

                if (el.getState() == Elevator.DOWN && el.getPos() >= c.getSrc()) {
                    if (pickedElevator == -1)
                        pickedElevator = i;
                        //or has less load
                    else if (el.getPos() < building.getElevetor(pickedElevator).getPos())
                        pickedElevator = i;
                }
            }

        }
        return pickedElevator;
    }


    // get the fastest elevator to get to call position
    private int getFastestStaticElevator(CallForElevator call) {
        int picked = -1;

        for (int i = 0; i < building.numberOfElevetors(); i++) {
            Elevator el = building.getElevetor(i);
            if (el.getState() == Elevator.LEVEL) {
                if (picked == -1)
                    picked = i;
                else if (dist(call, picked) / building.getElevetor(picked).getSpeed() > dist(call, i) / building.getElevetor(i).getSpeed())
                    picked = i;

            }
        }

        return picked;
    }

    private int dist(CallForElevator c, int el) {
        return Math.abs(building.getElevetor(el).getPos() - c.getSrc());
    }

    //the calls that are going to move more than half of the building going to fastest elevators

    @Override
    public int allocateAnElevator(CallForElevator c) {

        //search for the fastest static elevator to get to this call
        int picked = getFastestStaticElevator(c);
        if (picked != -1) {
            callsManager[picked].add(c);
            return picked;
        }

        //search for the closest elevator that is on the way of this call
        picked = getOnTheWayElevator(c);
        if (picked != -1) {
            callsManager[picked].add(c);
            return picked;
        }

        //Other wise use RoundRobin for splitting the load
        int roundRobinPick = roundRobinAllocate();
        callsManager[roundRobinPick].add(c);
        return roundRobinPick;
    }



    private int roundRobinAllocate() {
        int ans = elevatorAllocation % building.numberOfElevetors();
        elevatorAllocation = (elevatorAllocation + 1) % (building.numberOfElevetors());
        return ans;
    }


    @Override
    public void cmdElevator(int elev) {
        Elevator el = building.getElevetor(elev);
        CustomDataStructure callManager = callsManager[elev];


        if (el.getState() == Elevator.LEVEL) {
            int next = callManager.getNext();
            if (next != Integer.MAX_VALUE) {
                el.goTo(next);
            }

        } else if (el.getState() == Elevator.UP &&
                callManager.getDirection() == LookDs.UP &&
                callManager.hasActiveCalls() &&
                el.getPos() == callManager.getFirst()) {
            //stop there
            el.stop(callManager.popFirst());
            callManager.stopped();
        } else if (el.getState() == Elevator.DOWN &&
                callManager.getDirection() == LookDs.DOWN &&
                callManager.hasActiveCalls() &&
                el.getPos() == callManager.getLast()) {
            //stop there
            el.stop(callManager.popLast());
            callManager.stopped();
        }
    }


}
