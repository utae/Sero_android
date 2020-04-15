package net.videofactory.new_audi.common;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Utae on 2016-03-17.
 */
public class ReorderedArraySet<E> {

    private ArrayList<E> arrayList;

    public ReorderedArraySet() {
        arrayList = new ArrayList<>();
    }

    public boolean add(E object){
        if(arrayList.contains(object)){
            arrayList.remove(object);
        }
        return arrayList.add(object);
    }

    public E get(int index){
        return arrayList.get(index);
    }

    public int size(){
        return arrayList.size();
    }

    public E getCurValue(){
        if(arrayList.size() > 0){
            return arrayList.get(arrayList.size()-1);
        }else{
            return null;
        }
    }

    public void removeCurValue(){
        arrayList.remove(arrayList.size()-1);
    }
}
