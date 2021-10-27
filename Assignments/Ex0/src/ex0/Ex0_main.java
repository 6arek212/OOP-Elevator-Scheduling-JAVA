package ex0;

import ex0.algo.*;
import ex0.simulator.Simulator_A;

/**
 * This is the main file of Ex0 (OOP), Do play with it and make sure you know how to operate the simulator before
 * starting to implement the algorithm.
 */

//    Algorithm Results
//        Code Owners,207154261,315201327,  Case,9,  Total waiting time: 56246.34007431241,  average waiting time per call: 56.24634007431241,  unCompleted calls,7,  certificate, -2047927664
//        Code Owners,207154261,315201327,  Case,8,  Total waiting time: 139343.88209694857,  average waiting time per call: 139.34388209694856,  unCompleted calls,21,  certificate, -4751323208
//        Code Owners,207154261,315201327,  Case,7,  Total waiting time: 172403.12115705168,  average waiting time per call: 172.4031211570517,  unCompleted calls,38,  certificate, -6046093166
//        Code Owners,207154261,315201327,  Case,6,  Total waiting time: 63689.882096948444,  average waiting time per call: 63.689882096948445,  unCompleted calls,12,  certificate, -2249772378
//        Code Owners,207154261,315201327,  Case,5,  Total waiting time: 79516.12115705178,  average waiting time per call: 79.51612115705177,  unCompleted calls,27,  certificate, -2801965097
//        Code Owners,207154261,315201327,  Case,4,  Total waiting time: 23233.455368642088,  average waiting time per call: 46.466910737284174,  unCompleted calls,3,  certificate, -1493425705
//        Code Owners,207154261,315201327,  Case,3,  Total waiting time: 18950.538284333117,  average waiting time per call: 47.376345710832794,  unCompleted calls,2,  certificate, -1743884578
//        Code Owners,207154261,315201327,  Case,2,  Total waiting time: 5308.792822120196,  average waiting time per call: 53.08792822120196,  unCompleted calls,5,  certificate, -1805901051
//        Code Owners,207154261,315201327,  Case,1,  Total waiting time: 327.9897426188186,  average waiting time per call: 32.79897426188186,  unCompleted calls,4,  certificate, -1187239726
//        Code Owners,207154261,315201327,  Case,0,  Total waiting time: 213.9897426188186,  average waiting time per call: 21.39897426188186,  unCompleted calls,0,  certificate, -743781020





public class Ex0_main {
    public static Long ID0 = 207154261L, ID1 = 315201327L, ID2 = null;

    public static void main(String[] ar) {
        String codeOwner = codeOwner();
        Simulator_A.setCodeOwner(codeOwner);
        int stage = 9;  // any case in [0,9].
        System.out.println("Ex0 Simulator: isStarting, stage=" + stage + ") ... =  ");
        String callFile = null; // use the predefined cases [1-9].
        // String callFile = "data/Ex0_stage_2__.csv"; //

        Simulator_A.initData(stage, callFile);  // init the simulator data: {building, calls}.
        ElevatorAlgo look = new LookAlgo(Simulator_A.getBuilding());

        Simulator_A.initAlgo(look); // init the algorithm to be used by the simulator

        Simulator_A.runSim(); // run the simulation - should NOT take more than few seconds.

        long time = System.currentTimeMillis();
        String report_name = "out/Ex0_report_case_" + stage + "_" + time + "_ID_.log";
        Simulator_A.report(report_name); // print the algorithm results in the given case, and save the log to a file.
        //Simulator_A.report(); // if now file  - simple prints just the results.
        Simulator_A.writeAllCalls("out/Ex0_Calls_case_" + stage + "_.csv"); // time,src,dest,state,elevInd, dt.
    }

    private static String codeOwner() {
        String owners = "none";
        if (ID0 != null) {
            owners = "" + ID0;
        }
        if (ID1 != null) {
            owners += "," + ID1;
        }
        if (ID2 != null) {
            owners += "," + ID2;
        }
        return owners;
    }
}