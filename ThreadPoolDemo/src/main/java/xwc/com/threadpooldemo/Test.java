package xwc.com.threadpooldemo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xwc on 2017/8/7.
 */

public class Test {

    public static void main(String[] args){
//        Pool.init();
//        SingleOne.getInstance().useTool();

        List<Tool> list = new ArrayList<>();
        list.add(new Tool());
        list.clear();
        System.out.println("list size = " + list.size());
        List<Tool> cache = new ArrayList<>();
        cache.add(new Tool());
        cache.add(new Tool());

        list.addAll(0, cache);
        System.out.println("list size = " + list.size());
    }
}
