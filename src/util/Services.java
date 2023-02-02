package util;

import model.Card;

import java.util.ArrayList;

public class Services {
    /*
    public ArrayList<model.Card> binsert(ArrayList<model.Card> a, model.Card new_c){
        if (a.size() == 0){
            a.add(new_c);
            return a;
        }
        int index = 0;
        int low = 0;
        int high = a.size() - 1;
    
        while (low <= high) {
            int mid = low  + ((high - low) / 2);
            index = mid;
            if (!(a.get(mid).greater_than(new_c))) { 
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

  
    }
    */

    public static ArrayList<Card> insert(ArrayList<Card> a, Card new_c){ //this can go in model.Player?
        if(a.size() == 0){
            a.add(new_c);
        }
        
        else if (a.get(0).greater_than(new_c)){
            a.add(0, new_c);
        }
        else{
            for(int i = 0 ; i < a.size() - 1 ; i++){
                if ((new_c.rank >= a.get(i).rank) && a.get(i+1).rank >= new_c.rank){
                    a.add(i+1, new_c);
                    return a;
                }
            }
            a.add(new_c);
        }
        return a;
    }
}
