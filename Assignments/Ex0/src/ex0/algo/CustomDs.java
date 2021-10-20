package ex0.algo;

import ex0.CallForElevator;

import java.util.ArrayList;

public class CustomDs {
    private ArrayList<Integer> activeCalls;
    private ArrayList<Integer> downCalls;
    private ArrayList<Integer> upCalls;


    public void insert(CallForElevator c) {
        int i = 0;

        if (c.getType() == CallForElevator.UP) {
            while (i < upCalls.size() && upCalls.get(i) < c.getSrc()) {
                i++;
            }
            upCalls.add(i, c.getSrc());
            upCalls.add(i, c.getDest());
        } else {

            while (i < downCalls.size() && downCalls.get(i) > c.getSrc()) {
                i++;
            }
            downCalls.add(i, c.getSrc());
            downCalls.add(i, c.getDest());
        }
    }






}
