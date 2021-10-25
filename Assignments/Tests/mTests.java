import ex0.Building;
import ex0.CallForElevator;
import ex0.algo.LookDs;
import ex0.simulator.Simulator_A;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class mTests {
    Building building;
    CallForElevator call = new CallForElevator() {
        @Override
        public int getState() {
            return CallForElevator.INIT;
        }

        @Override
        public double getTime(int state) {
            return 0;
        }

        @Override
        public int getSrc() {
            return 0;
        }

        @Override
        public int getDest() {
            return 5;
        }

        @Override
        public int getType() {
            return CallForElevator.UP;
        }

        @Override
        public int allocatedTo() {
            return 0;
        }
    };




    public mTests() {
        String codeOwner = "207154261";
        Simulator_A.setCodeOwner(codeOwner);
        int stage = 9;  // any case in [0,9].
        System.out.println("Ex0 Simulator: isStarting, stage=" + stage + ") ... =  ");
        String callFile = null; // use the predefined cases [1-9].
        // String callFile = "data/Ex0_stage_2__.csv"; //

        Simulator_A.initData(stage, callFile);
        this.building = Simulator_A.getBuilding();
    }


    @Test
    public void add() {
        LookDs lookDs = new LookDs(building.getElevetor(0));
        lookDs.add(call);
        assertEquals(lookDs.numberOfCalls() , 2);
    }

    @Test
    public void getNext() {
        LookDs lookDs = new LookDs(building.getElevetor(0));
        lookDs.add(call);
        assertEquals(lookDs.getNext() , 0);
        assertEquals(lookDs.getNext() , 5);
    }




}
