package ex0.algo;

import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;

public class LookAlgo2 implements ElevatorAlgo {
    public static final int UP = 1, DOWN = -1, LEVEL = 0;

    private Building building;
    private String algoName = "Scan";
    private int elevatorAllocation;
    private CustomDataStructure[] callsManager;

    public LookAlgo2(Building building) {
        this.building = building;
        callsManager = new CustomDataStructure[building.numberOfElevetors()];
        for (int i = 0; i < building.numberOfElevetors(); i++) {
            callsManager[i] = new LookDs(building.getElevetor(i));
        }

        //int p = 0;
//        for (int i = 0; i < building.numberOfElevetors(); i++) {
//            if (building.getElevetor(i).getSpeed() > building.getElevetor(p).getSpeed())
//                p = i;
//        }
//        callsManager[p] = new CLookDs(building.getElevetor(p),CLookDs.ONLY_UP);

    }

    @Override
    public Building getBuilding() {
        return building;
    }

    @Override
    public String algoName() {
        return algoName;
    }


    public int getOnTheWayElevator(CallForElevator c) {
        int pickedElevator = -1;

        if (c.getType() == CallForElevator.UP) {
            for (int i = 0; i < building.numberOfElevetors(); i++) {
                Elevator el = building.getElevetor(i);
                if (callsManager[i].getDirection() == LookDs.UP && el.getPos() <= c.getSrc()) {
                    if (pickedElevator == -1)
                        pickedElevator = i;
                    else if (((LookDs) callsManager[pickedElevator]).estimatedTimeToGet(c) > ((LookDs) callsManager[i]).estimatedTimeToGet(c))
                        pickedElevator = i;
                }
            }
        } else {
            for (int i = 0; i < building.numberOfElevetors(); i++) {
                Elevator el = building.getElevetor(i);

                if (callsManager[i].getDirection() == LookDs.DOWN && el.getPos() >= c.getSrc()) {
                    if (pickedElevator == -1)
                        pickedElevator = i;
                    else if (((LookDs) callsManager[pickedElevator]).estimatedTimeToGet(c) > ((LookDs) callsManager[i]).estimatedTimeToGet(c))
                        pickedElevator = i;
                }
            }

        }
        return pickedElevator;
    }


    // get the fastest elevator that is idle to get to call position
    public int getFastestStaticElevator(CallForElevator c) {
        int picked = -1;
        double pickedTime = -1;
        for (int i = 0; i < building.numberOfElevetors(); i++) {
            if (callsManager[i].getDirection() == LookDs.LEVEL) {
                Elevator e = building.getElevetor(i);
                double time = e.getTimeForClose() + e.getStartTime() + e.getStopTime() + e.getTimeForOpen()
                        + dist(c, i) / e.getSpeed();

                if (picked == -1) {
                    picked = i;
                    pickedTime = time;
                } else if (pickedTime > time) {
                    picked = i;
                    pickedTime = time;
                }
            }
        }
        return picked;
    }


    private int dist(CallForElevator c, int el) {
        return Math.abs(building.getElevetor(el).getPos() - c.getSrc());
    }


    // IF not elevator on the way get the elevator with no active calls
    public int getOptimal(CallForElevator c) {
        int picked = -1;

        if (c.getType() == CallForElevator.UP) {
            for (int i = 0; i < building.numberOfElevetors(); i++) {
                if (callsManager[i].getDirection() == LookDs.DOWN) {
                    if (picked == -1)
                        picked = i;
                    else if (
                            ((LookDs) callsManager[picked]).estimatedTimeToGet(c) > ((LookDs) callsManager[i]).estimatedTimeToGet(c)
                                    && !callsManager[i].hasActiveCalls()
                    ) {
                        picked = i;
                    }
                }
            }
        } else {

            for (int i = 0; i < building.numberOfElevetors(); i++) {
                if (callsManager[i].getDirection() == LookDs.UP) {
                    if (picked == -1)
                        picked = i;
                    else if (
                            ((LookDs) callsManager[picked]).estimatedTimeToGet(c) > ((LookDs) callsManager[i]).estimatedTimeToGet(c)
                                    && !callsManager[i].hasActiveCalls()
                    ) {
                        picked = i;
                    }
                }
            }

        }

        return picked;
    }


    //the calls that are going to move more than half of the building going to the fastest elevators
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


        picked = getOptimal(c);
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
            if (next != Integer.MAX_VALUE)
                el.goTo(next);
        } else if (el.getState() == Elevator.UP &&
                callManager.getDirection() == LookDs.UP &&
                callManager.hasActiveCalls() &&
                el.getPos() == callManager.getFirst()
        ) {
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
