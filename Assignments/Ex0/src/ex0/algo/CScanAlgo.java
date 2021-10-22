package ex0.algo;

import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;

public class CScanAlgo implements ElevatorAlgo {

    private Building building;
    private String algoName = "Scan";
    private CScanDs[] callsManager;

    public CScanAlgo(Building building) {
        this.building = building;
        callsManager = new CScanDs[building.numberOfElevetors()];


        for (int i = 0; i < building.numberOfElevetors(); i++) {
            if (i < building.numberOfElevetors() / 2) {
                callsManager[i] = new CScanDs(building.getElevetor(i), CScanDs.ONLY_UP);
            } else {
                callsManager[i] = new CScanDs(building.getElevetor(i), CScanDs.ONLY_DOWN);
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


    @Override
    public int allocateAnElevator(CallForElevator c) {
        if (c.getType() == CallForElevator.UP) {
            //look for the closest up elevator

            int picked = -1;

            // FIND THE CLOSEST ELEVATOR THAT PASSES THIS CALL
            for (int i = 0; i < building.numberOfElevetors() / 2; i++) {
                Elevator el = building.getElevetor(i);
                if (el.getPos() <= c.getSrc()) {
                    if (picked == -1)
                        picked = i;
                    else if (dist(c, picked) > dist(c, i))
                        picked = i;
                }
            }

            if (picked != -1) {
                System.out.println(c.getSrc()+" ---> "+c.getDest()+" got allocated to "+picked);
                callsManager[picked].add(c);
                return picked;
            }


            //FIND THE ELEVATOR AT THE HIGHEST FLOOR
            picked = 0;
            for (int i = 1; i < building.numberOfElevetors() / 2; i++) {
                Elevator el = building.getElevetor(i);
                if (el.getPos() > building.getElevetor(picked).getPos())
                    picked = i;
            }


            System.out.println(c.getSrc()+" ---> "+c.getDest()+" got allocated to "+picked);

            callsManager[picked].add(c);
            return picked;


        } else {


            int picked = -1;

            // FIND THE CLOSEST ELEVATOR THAT PASSES THIS CALL
            for (int i = building.numberOfElevetors() / 2; i < building.numberOfElevetors(); i++) {
                Elevator el = building.getElevetor(i);
                if (el.getPos() >= c.getSrc()) {
                    if (picked == -1)
                        picked = i;
                    else if (dist(c, picked) > dist(c, i))
                        picked = i;
                }
            }

            if (picked != -1) {
                callsManager[picked].add(c);
                System.out.println(c.getSrc()+" ---> "+c.getDest()+" got allocated to "+picked);
                return picked;
            }

            //FIND THE ELEVATOR AT THE LOWEST FLOOR
            picked = building.numberOfElevetors() / 2;
            for (int i = building.numberOfElevetors() / 2; i < building.numberOfElevetors(); i++) {
                Elevator el = building.getElevetor(i);
                if (el.getPos() < building.getElevetor(picked).getPos())
                    picked = i;
            }

            System.out.println(c.getSrc()+" ---> "+c.getDest()+" got allocated to "+picked);
            callsManager[picked].add(c);
            return picked;
        }

    }


    @Override
    public void cmdElevator(int elev) {
        Elevator el = building.getElevetor(elev);
        CScanDs callManager = callsManager[elev];

        if (el.getState() == Elevator.LEVEL) {
            if (callManager.isThereDestinations())
                el.goTo(callManager.getNext());
        }


        if (el.getState() == Elevator.UP &&
                callManager.getDirection() == CScanDs.ONLY_UP &&
                callManager.hasActiveCalls() &&
                el.getPos() == callManager.getFirst()) {
            //stop there
            el.stop(callManager.popFirst());
            callManager.stopped();
        }


        if (el.getState() == Elevator.DOWN &&
                callManager.getDirection() == CScanDs.ONLY_DOWN &&
                callManager.hasActiveCalls() &&
                el.getPos() == callManager.getLast()) {
            //stop there
            el.stop(callManager.popLast());
            callManager.stopped();
        }

    }
}
