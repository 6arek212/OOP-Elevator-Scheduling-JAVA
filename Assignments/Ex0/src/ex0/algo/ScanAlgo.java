package ex0.algo;

import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;

import java.util.ArrayList;

public class ScanAlgo implements ElevatorAlgo {
    public static final int UP = 1, DOWN = -1, LEVEL = 0;

    private Building building;
    private String algoName = "Scan";
    private int elevatorAllocation;
    private CustomDs[] calls;


    public ScanAlgo(Building building) {
        this.building = building;
        calls = new CustomDs[building.numberOfElevetors()];
        for (int i = 0; i < building.numberOfElevetors(); i++) {
            calls[i] = new CustomDs(i,building.getElevetor(i));
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


    // RoundRobin allocation , which splits the load equally between the elevators
//    @Override
//    public int allocateAnElevator(CallForElevator c) {
//        int ans = elevatorAllocation % building.numberOfElevetors();
//        elevatorAllocation = (elevatorAllocation + 1) % building.numberOfElevetors();
//        calls[ans].add(c);
//        return ans;
//    }


    @Override
    public int allocateAnElevator(CallForElevator c) {
        if (c.getType() == CallForElevator.UP) {
            int pickedElevator = -1;
            for (int i = 0; i < building.numberOfElevetors(); i++) {
                Elevator el = building.getElevetor(i);

                if (el.getState() == Elevator.UP && el.getPos() <= c.getSrc()) {
                    if (pickedElevator == -1)
                        pickedElevator = i;
                        //or has less load
                    else if (el.getPos() > building.getElevetor(pickedElevator).getPos())
                        pickedElevator = i;
                }
            }

            if (pickedElevator != -1) {
                calls[pickedElevator].add(c);
                return pickedElevator;
            }
        } else {
            int pickedElevator = -1;
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

            if (pickedElevator != -1) {
                calls[pickedElevator].add(c);
                return pickedElevator;
            }

        }

        int roundRobinPick = roundRobinAllocate();
        calls[roundRobinPick].add(c);
        return roundRobinPick;
    }


    private int roundRobinAllocate() {
        int ans = elevatorAllocation % building.numberOfElevetors();
        elevatorAllocation = (elevatorAllocation + 1) % building.numberOfElevetors();
        return ans;
    }


    @Override
    public void cmdElevator(int elev) {
        Elevator elv = building.getElevetor(elev);

        if (elv.getState() == Elevator.LEVEL) {
            int next = calls[elev].getNext();
            if (next != Integer.MAX_VALUE)
                elv.goTo(next);
        }
    }


}
