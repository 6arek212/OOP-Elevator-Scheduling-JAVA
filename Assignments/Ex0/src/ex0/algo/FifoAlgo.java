package ex0.algo;

import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class FifoAlgo implements ElevatorAlgo {
    public static final int UP = 1, DOWN = -1, LEVEL = 0;

    private Building building;
    private String algoName = "001";


    private Queue<CallForElevator>[] calls;


    public FifoAlgo(Building building) {
        this.building = building;

        calls = new LinkedList[building.numberOfElevetors()];

        for (int i = 0; i < building.numberOfElevetors(); i++) {
            calls[i] = new LinkedList<>();
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

    @Override
    public int allocateAnElevator(CallForElevator c) {
        return getLeastLoadedElevator();
    }

    private int getLeastLoadedElevator() {
        int leastLoaded = 0;

        for (int i = 1; i < building.numberOfElevetors(); i++) {
            if (calls[i].size() < calls[leastLoaded].size())
                leastLoaded = i;
        }

        return leastLoaded;
    }


    private int dist(Elevator el, CallForElevator c) {
        return Math.abs(c.getSrc() - el.getPos());
    }


    //if its going up then it will go to the nearst call up until the upper call not to the end of the building
    @Override
    public void cmdElevator(int elev) {
        Elevator el = building.getElevetor(elev);
        if (el.getState() == LEVEL) {
            CallForElevator call = calls[elev].peek();

            if (el.getPos() == call.getDest())
                el.goTo(calls[elev].poll().getDest());
            else
                el.goTo(call.getSrc());
        }
    }


    private int getPotentialUpElevator(CallForElevator c) {
        ArrayList<Integer> upElevators = new ArrayList<>();

        for (int i = 0; i < building.numberOfElevetors(); i++) {
            Elevator el = building.getElevetor(i);
            if (el.getState() == Elevator.UP && el.getPos() <= c.getSrc()) {
                upElevators.add(i);
            }
        }


        if (upElevators.size() != 0) {
            int closest = upElevators.get(0);
            for (int i = 1; i < upElevators.size(); i++) {
                if (dist(building.getElevetor(upElevators.get(i)), c) < dist(building.getElevetor(closest), c)) {
                    closest = upElevators.get(i);
                }
            }
            return closest;
        }
        return -1;
    }


}
