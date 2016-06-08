package mllib;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linheng on 2016/4/18.
 */
public class Item implements Cloneable {
    public String label;
    public int clusteredClass;
    public List<Double> vec=new ArrayList<Double>();

    public Item(){
        label ="";
        clusteredClass=0;
    };
    public Item clone()
    {
        Item o=null;
        try
        {
            o=(Item)super.clone();//Object 中的clone()识别出你要复制的是哪一个对象。
        }
        catch(CloneNotSupportedException e)
        {
            System.out.println(e.toString());
        }
        return o;
    }
}
