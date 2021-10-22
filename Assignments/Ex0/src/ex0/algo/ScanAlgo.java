package ex0.algo;

import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;

public class ScanAlgo implements ElevatorAlgo {
    public static final int UP = 1, DOWN = -1, LEVEL = 0;

    private Building building;
    private String algoName = "Scan";
    private int elevatorAllocation;
    private CustomDataStructure[] callsManager;


    public ScanAlgo(Building building) {
        this.building = building;
        callsManager = new CustomDataStructure[building.numberOfElevetors()];

        for (int i = 0; i < building.numberOfElevetors(); i++) {
                callsManager[i] = new ScanDs(building.getElevetor(i));
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


    private int getClosestLevelElevator(CallForElevator call) {
        int picked = -1;

        for (int i = 0; i < building.numberOfElevetors(); i++) {
            Elevator el = building.getElevetor(i);
            if (el.getState() == Elevator.LEVEL) {
                if (picked == -1)
                    picked = i;
                else if (dist(call, picked) > dist(call, i))
                    picked = i;
            }
        }

        return picked;
    }

    private int dist(CallForElevator c, int el) {
        return Math.abs(building.getElevetor(el).getPos() - c.getSrc());
    }


    @Override
    public int allocateAnElevator(CallForElevator c) {

        //search for the closest elevator that is on the way of this call
        int picked = getOnTheWayElevator(c);
        if (picked != -1) {
            callsManager[picked].add(c);
            return picked;
        }


        //search for the closest static elevator
        picked = getClosestLevelElevator(c);
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
        elevatorAllocation = (elevatorAllocation + 1) % building.numberOfElevetors();
        return ans;
    }


    @Override
    public void cmdElevator(int elev) {
        Elevator el = building.getElevetor(elev);
        CustomDataStructure callManager = callsManager[elev];

        if (el.getState() == Elevator.LEVEL) {
            int next = callManager.getNext();
            if (next != Integer.MAX_VALUE)
                el.goTo(next);
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
