package cm.g2i.lalalaworker.models;

import cm.g2i.lalalaworker.controllers.services.Date;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Sim'S on 25/07/2017.
 */

public class History implements Serializable{
    private ArrayList<HistoryUnit> units;

    private int numberCall;
    private int numberSms;

    public History(){
        units = new ArrayList<>();
        setNumbers();
    }

    public History(HistoryUnit[] units){
        this.units = new ArrayList<>();
        if (units!=null){
            for (int i=0; i<units.length; i++){
                addUnit(units[i]);
            }
            setNumbers();
        }
    }

    public void addUnit(HistoryUnit unit){
        for (HistoryUnit h: units){
            if (!h.semiEquals(unit)) return;
        }
        units.add(unit);
    }

    public void removeUnit(HistoryUnit unit){
        units.remove(unit);
    }

    public void reset(){
        units = new ArrayList<>();
    }

    public ArrayList<HistoryUnit> getUnits() {
        return units;
    }

    public String details(String call, String sms){
        return numberCall+" "+call+" & "+numberSms+" "+sms;
    }

    public long lastDate(){
        if (units!=null && units.size()>0){
            long time = Date.getTimeMillisString(units.get(0).getDate());
            for (int i=1; i<units.size(); i++){
                long l = Date.getTimeMillisString(units.get(i).getDate());
                if (l > time) time = l;
            }
            return time;
        }
        return 0;
    }

    public void sortUnits(int debut, int fin){
        if (debut<fin){
            long pivot = Date.getTimeMillisString(units.get(fin).getDate());
            int i = debut;
            int j = debut;
            while (i<=fin){
                if (Date.getTimeMillisString(units.get(i).getDate()) > pivot){
                    HistoryUnit h = units.get(i);
                    units.set(i, units.get(j));
                    units.set(j, h);
                    j++;
                }
                i++;
            }
            HistoryUnit h = units.get(fin);
            units.set(fin, units.get(j));
            units.set(j, h);
            sortUnits(debut, j-1);
            sortUnits(j+1, fin);
        }
    }

    public void sortUnits(){
        sortUnits(0, units.size()-1);
    }

    public void setNumbers(){
        numberCall = 0;
        numberSms = 0;
        for (HistoryUnit unit: units){
            if (unit.getType()==HistoryUnit.TYPE_CALL) numberCall++;
            else numberSms++;
        }
    }
}
