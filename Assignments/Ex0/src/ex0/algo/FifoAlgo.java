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
        int elev = getLeastLoadedElevator();
        calls[elev].add(c);
        return elev;
    }

    private int getLeastLoadedElevator() {
        int leastLoaded = 0;

        for (int i = 1; i < building.numberOfElevetors(); i++) {
            if (calls[i].size() < calls[leastLoaded].size())
                leastLoaded = i;
        }

        return leastLoaded;
    }





    @Override
    public void cmdElevator(int elev) {
        Elevator el = building.getElevetor(elev);

        System.out.println(calls[elev].size());
        if (el.getState() == LEVEL && !calls[elev].isEmpty()) {
            CallForElevator call = calls[elev].peek();

                if (el.getPos() == call.getSrc())
                    el.goTo(calls[elev].poll().getDest());
                else
                    el.goTo(call.getSrc());
        }
    }





}
