/**
 * Created by Mikhail on 12.12.2017.
 */

import org.telegram.telegrambots.api.objects.User;
import java.util.ArrayList;

public class Group {
    String name;
    ArrayList<User> users;
    ArrayList<Place> places;
    ArrayList<String> times;
    Group(String name){
        this.name = name;
        users = new ArrayList<User>();
        places = new ArrayList<Place>();
        times = new ArrayList<String>();
    }
    Group(String name, ArrayList<User> users, ArrayList<Place> places){
        this.name = name;
        this.users = users;
        this.places = places;
        this.times = new ArrayList<String>();
    }
    public String getName() {
        return name;
    }

    public ArrayList<Place> getPlaces() {
        return places;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public ArrayList<String> getTime() {
        return times;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlaces(ArrayList<Place> places) {
        this.places = places;
    }

    public void setTime(ArrayList<String> times) {
        this.times = times;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public boolean containsUser(Integer id){
        for(User u : users){
            if(u.getId().equals(id)){
                return true;
            }
        }
        return false;
    }
    public void removeUserByID(Integer id){
        for(User u : users){
            if(u.getId().equals(id)){
                users.remove(u);
            }
        }
    }
}
