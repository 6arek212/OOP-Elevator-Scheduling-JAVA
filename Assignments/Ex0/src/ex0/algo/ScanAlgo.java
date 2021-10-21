package ex0.algo;

import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;

import java.util.ArrayList;

public class ScanAlgo implements ElevatorAlgo {
    public static final int UP = 1, DOWN = -1, LEVEL = 0;

    private Building building;
    private String algoName = "Scan";
    private int elevator;
    private ArrayList<CallForElevator>[] calls;

    public ScanAlgo(Building building) {
        this.building = building;
        calls = new ArrayList[building.numberOfElevetors()];

        for (int i = 0; i < building.numberOfElevetors(); i++) {
            calls[i] = new ArrayList<>();
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
        int ans = elevator % building.numberOfElevetors();
        elevator++;
        calls[ans].add(c);
        return ans;
    }


    @Override
    public void cmdElevator(int elev) {
        Elevator elv = building.getElevetor(elev);
        if (elv.getState() == Elevator.LEVEL) {


        }

        if (elv.getState() == Elevator.UP) {


        }

    }

    private void getClosestRequestOnTheWay(int elev) {

        int j = -1;
        for (int i = 0; i < calls[elev].size(); i++) {
            CallForElevator c = calls[elev].get(i);
            if (c.getType() == Elevator.UP && c.getSrc() >= building.getElevetor(i).getPos()){


            }
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


    private int dist(Elevator el, CallForElevator c) {
        return Math.abs(c.getSrc() - el.getPos());
    }

}
