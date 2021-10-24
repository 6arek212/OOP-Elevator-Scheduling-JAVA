package ex0.algo;

import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;

public class CLookAlgo implements ElevatorAlgo {

    private Building building;
    private String algoName = "Scan";
    private CLookDs[] callsManager;
    private int elevatorAllocationUp;
    private int elevatorAllocationDown;


    public CLookAlgo(Building building) {
        this.building = building;
        callsManager = new CLookDs[building.numberOfElevetors()];
        for (int i = 0; i < building.numberOfElevetors(); i++) {
            if (i < building.numberOfElevetors() / 2) {
                callsManager[i] = new CLookDs(building.getElevetor(i), CLookDs.ONLY_UP);
            } else {
                callsManager[i] = new CLookDs(building.getElevetor(i), CLookDs.ONLY_DOWN);
            }
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


    private int dist(CallForElevator c, int el) {
        return Math.abs(building.getElevetor(el).getPos() - c.getSrc());
    }


    // get the fastest elevator to get to call position
    private int getFastestStaticElevator(CallForElevator c) {
        int picked = 0;

        if (c.getType() == CallForElevator.UP) {
            for (int i = 0; i < building.numberOfElevetors() / 2; i++) {
                Elevator e = building.getElevetor(i);
                double time = e.getTimeForClose() + e.getStartTime() + e.getStopTime() + e.getTimeForOpen()
                        + dist(c, i) / e.getSpeed();
                if (((CLookDs) callsManager[picked]).estimatedTimeToGet(c.getSrc()) > ((CLookDs) callsManager[i]).estimatedTimeToGet(c.getSrc())) {
                    picked = i;
                }
            }
        } else {
            for (int i = building.numberOfElevetors() / 2; i < building.numberOfElevetors(); i++) {
                Elevator e = building.getElevetor(i);
                double time = e.getTimeForClose() + e.getStartTime() + e.getStopTime() + e.getTimeForOpen()
                        + dist(c, i) / e.getSpeed();
                if (((CLookDs) callsManager[picked]).estimatedTimeToGet(c.getSrc()) > ((CLookDs) callsManager[i]).estimatedTimeToGet(c.getSrc())) {
                    picked = i;
                }
            }
        }

        return picked;
    }


    private int getOnTheWayElevator(CallForElevator c) {
        int pickedElevator = -1;

        if (c.getType() == CallForElevator.UP) {
            for (int i = 0; i < building.numberOfElevetors() / 2; i++) {
                Elevator el = building.getElevetor(i);
                if (callsManager[i].getDirection() == LookDs.UP && el.getPos() <= c.getSrc()) {
                    if (pickedElevator == -1)
                        pickedElevator = i;
                    else if (((CLookDs) callsManager[pickedElevator]).estimatedTimeToGet(c.getSrc()) > ((CLookDs) callsManager[i]).estimatedTimeToGet(c.getSrc()))
                        pickedElevator = i;
                }
            }
        } else {
            for (int i = building.numberOfElevetors() / 2; i < building.numberOfElevetors(); i++) {
                Elevator el = building.getElevetor(i);

                if (callsManager[i].getDirection() == LookDs.DOWN && el.getPos() >= c.getSrc()) {
                    if (pickedElevator == -1)
                        pickedElevator = i;
                    else if (((CLookDs) callsManager[pickedElevator]).estimatedTimeToGet(c.getSrc()) > ((CLookDs) callsManager[i]).estimatedTimeToGet(c.getSrc()))
                        pickedElevator = i;
                }
            }

        }
        return pickedElevator;
    }


    private int getB(CallForElevator c) {
        int picked = 0;

        if (c.getType() == CallForElevator.UP) {
            //FIND THE ELEVATOR AT THE HIGHEST FLOOR
            for (int i = 1; i < building.numberOfElevetors() / 2; i++) {
                Elevator el = building.getElevetor(i);
                if (el.getPos() > building.getElevetor(picked).getPos())
                    picked = i;
            }
        } else {
            //FIND THE ELEVATOR AT THE LOWEST FLOOR
            picked = building.numberOfElevetors() / 2;
            for (int i = building.numberOfElevetors() / 2; i < building.numberOfElevetors(); i++) {
                Elevator el = building.getElevetor(i);
                if (el.getPos() < building.getElevetor(picked).getPos())
                    picked = i;
            }

        }
        return picked;
    }

    private int roundRobinAllocate(CallForElevator c) {
        if (c.getType() == CallForElevator.UP) {
            int ans = elevatorAllocationUp % (building.numberOfElevetors() / 2);
            elevatorAllocationUp = (elevatorAllocationUp + 1) % (building.numberOfElevetors() / 2);
            return ans;
        }
        int ans = (elevatorAllocationDown % (building.numberOfElevetors() / 2)) + building.numberOfElevetors() / 2;
        elevatorAllocationDown = ((elevatorAllocationDown + 1) % (building.numberOfElevetors() / 2)) + building.numberOfElevetors() / 2;
        return ans;
    }


    @Override
    public int allocateAnElevator(CallForElevator c) {
        int picked = getFastestStaticElevator(c);
        if (picked != -1) {
            callsManager[picked].add(c);
            return picked;
        }

        picked = getOnTheWayElevator(c);
        if (picked != -1) {
            System.out.println(c.getSrc() + " ---> " + c.getDest() + " got allocated to " + picked);
            callsManager[picked].add(c);
            return picked;
        }

//        picked = getB(c);
//        System.out.println(c.getSrc() + " ---> " + c.getDest() + " got allocated to " + picked);
//        callsManager[picked].add(c);
//        return picked;


        int roundRobinPick = roundRobinAllocate(c);
        callsManager[roundRobinPick].add(c);
        return roundRobinPick;
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
                callManager.getDirection() == CLookDs.ONLY_UP &&
                callManager.hasActiveCalls() &&
                el.getPos() == callManager.getFirst()) {
            //stop there
            el.stop(callManager.popFirst());
            callManager.stopped();
        } else if (el.getState() == Elevator.DOWN &&
                callManager.getDirection() == CLookDs.ONLY_DOWN &&
                callManager.hasActiveCalls() &&
                el.getPos() == callManager.getLast()) {
            //stop there
            el.stop(callManager.popLast());
            callManager.stopped();
        }

    }
}
