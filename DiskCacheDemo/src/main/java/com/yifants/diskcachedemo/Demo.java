package com.yifants.diskcachedemo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xwc on 2017/7/27.
 */

public class Demo implements Serializable{

    public String name = "sfdsfdsf";
    public String country = "sdfdsfdsgfa";
    public int age = 150;
    public List<Item> dd;
    public List<ItemCa> ca;

    static class Item implements Serializable{
        public String name = "sfdsfdsf";
        public String country = "sdfdsfdsgfa";
    }

    static class ItemCa implements Serializable{
        public String name = "sfdsfdsf";
        public String country = "sdfdsfdsgfa";
    }
}
