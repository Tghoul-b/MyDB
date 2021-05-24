package com.flyn.mydb.Util;


import java.util.Collection;
import com.flyn.mydb.bean.Node;
public class OperUtil {
    public static  String input(String text){
        return text;
    }
    public static void garbageClear(Collection<Node> rootNodes) {
        for (Node node : rootNodes) {
            node.getFile().delete();
        }
//        Tree.leaves = null;
//        Tree.inners = null;
    }

}
