/**
 * Created by Mikhail on 12.12.2017.
 */
public class Place {
    String name;
    String location;
    Place(String name){
        this.name = name;
        location = "";
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
