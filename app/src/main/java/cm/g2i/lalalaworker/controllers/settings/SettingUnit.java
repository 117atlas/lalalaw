package cm.g2i.lalalaworker.controllers.settings;

/**
 * Created by Sim'S on 10/08/2017.
 */

public class SettingUnit {
    private String name;
    private String value;

    public SettingUnit(){
        super();
    }

    public SettingUnit(String name){
        this.name = name;
        this.value = "";
    }

    public SettingUnit(String name, String value){
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
