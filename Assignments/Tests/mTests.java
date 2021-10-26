import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;
import ex0.algo.LookAlgo;
import ex0.algo.LookDs;
import ex0.simulator.Simulator_A;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class mTests {
    static Building building;
    static LookAlgo lookAlgo;
    CallForElevator call;
    LookDs lookDs;


    @BeforeAll
    public static void init() {
        Simulator_A.setCodeOwner("207154261");
        Simulator_A.initData(9, null);
        building = Simulator_A.getBuilding();
        lookAlgo = new LookAlgo(building);
    }

    @BeforeEach
    public void setup() {
        lookDs = new LookDs(building.getElevetor(0));
        call = new CallForElevator() {
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
                return 2;
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
    }


    @Test
    public void add() {
        lookDs.add(call);
        assertEquals(lookDs.numberOfCalls(), 2);
    }

    @Test
    public void getNext() {
        lookDs.add(call);
        assertEquals(lookDs.getNext(), 2);
        assertEquals(lookDs.getNext(), 5);
    }


    @Test
    public void getPickUpElevator() {
        int pickedElevator = lookAlgo.getPickUpElevator(call);
        assertEquals(3, pickedElevator);
        // 3 is ths the fastest elevator
    }

    @Test
    public void getFastestStaticElevator() {
        int pickedElevator = lookAlgo.getFastestStaticElevator(call);
        assertEquals(3, pickedElevator);
        // 3 is ths the fastest elevator
    }


    @Test
    public void roundRobinAllocate() {
        int pickedElevator = lookAlgo.roundRobinAllocate();
        assertEquals(0, pickedElevator);
        pickedElevator = lookAlgo.roundRobinAllocate();
        assertEquals(1, pickedElevator);
        pickedElevator = lookAlgo.roundRobinAllocate();
        assertEquals(2, pickedElevator);
    }


}
