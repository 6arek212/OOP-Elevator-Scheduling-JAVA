package ex0.algo;

import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;

import java.util.concurrent.RecursiveTask;

public class SSTFAlgo implements ElevatorAlgo {
    private Building building;
    private String algoName = "SSTF";
    private SSTFDs[] callsManager;
    private int elevatorAllocation;

    public SSTFAlgo(Building building) {
        this.building = building;
        callsManager = new SSTFDs[building.numberOfElevetors()];
        for (int i = 0; i < building.numberOfElevetors(); i++) {
            callsManager[i] = new SSTFDs(building.getElevetor(i));
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


    private int getClosestElevator(CallForElevator c) {
        int picked = 0;
        for (int i = 1; i < building.numberOfElevetors(); i++) {
            if (dist(c, picked) > dist(c, i))
                picked = i;
        }
        return picked;
    }


    @Override
    public int allocateAnElevator(CallForElevator c) {
        int picked = getClosestElevator(c);
        callsManager[picked].add(c);
        return picked;
    }

    private int roundRobinAllocate() {
        int ans = elevatorAllocation % building.numberOfElevetors();
        elevatorAllocation = (elevatorAllocation + 1) % (building.numberOfElevetors());
        return ans;
    }

    @Override
    public void cmdElevator(int elev) {
        Elevator el = building.getElevetor(elev);
        SSTFDs callManager = callsManager[elev];


        if (el.getState() == Elevator.LEVEL && callManager.hasActiveCalls()) {
            el.goTo(callManager.getNext());
        }

    }
}
