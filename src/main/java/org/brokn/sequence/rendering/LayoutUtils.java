package org.brokn.sequence.rendering;

import org.brokn.sequence.model.Lane;

public class LayoutUtils {

//    public static int columnMultiplier(RenderableGraph model, String title) {
//        for (int i = 0; i < model.getLanes().toArray().length; i++) {
//            if(model.getLanes().get(i).equals(title)){
//                System.out.println("returning index " + i + " for name " + title);
//                return i;
//            }
//        }
//
//        System.out.println("no index found for name " + title + ", returning -1");
//        return -1;
//    }

    public static int columnXPosition(Lane lane) {
        int multi = lane.getIndex();
        if (multi == 0) {
            System.out.println("x for " + lane.getName() + " is 10");
            return 10;
        } else {
            int x = (multi * RenderableLane.NODE_WIDTH) + (multi * RenderableLane.NODE_GAP);
            System.out.println("x for " + lane.getName() + " is " + x);
            return 10 + x;
        }
    }

}
