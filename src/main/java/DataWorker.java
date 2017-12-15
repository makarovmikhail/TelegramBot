import org.telegram.telegrambots.api.objects.User;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Mikhail on 12.12.2017.
 */
public class DataWorker {
    // Объект содержащий все группы, включая участников (GroupUser)
    private static ArrayList<User> authorisedUsers;
    private static ArrayList<Group> GROUPS;
    private static Map<Integer,ArrayList<Place>> user_id_places;

    public static void setAuthorisedUsers(ArrayList<User> authorisedUsers) {
        DataWorker.authorisedUsers = authorisedUsers;
    }

    public static void addAuthorisedUsers(User user){
        authorisedUsers.add(user);
    }

    public static boolean isInAuthorisedUsers(User user){
        for(User u : authorisedUsers){
            if(u.getId().equals(user.getId())) return true;
        }
        return false;
    }

    public static void setUser_id_places(Map<Integer, ArrayList<Place>> user_id_places) {
        DataWorker.user_id_places = user_id_places;
    }

    public static Map<Integer, ArrayList<Place>> getUser_id_places() {
        return user_id_places;
    }

    public static ArrayList<Place> getPlacesByUserId(Integer userId){
        for(Integer i : user_id_places.keySet()){
            if(i.equals(userId)){
                return user_id_places.get(i);
            }
        }
        return new ArrayList<Place>();
    }

    public static ArrayList<Group> getGROUPS() {
        return GROUPS;
    }

    public static void setGROUPS(ArrayList<Group> GROUPS) {
        DataWorker.GROUPS = GROUPS;
    }

    public static boolean containsGroupByName(String name){
        for(Group g : GROUPS){
            if(g.getName().equals(name)){
                return true;
            }
        }
        return false;
    }

    public static Group getGroupByName(String name){
        for(Group g : getGROUPS()){
            if(g.getName().equals(name)){
                return g;
            }
        }
        return null;
    }

    public static void removeGroupByName(String name){
        Group group = null;
        for(Group g : GROUPS){
            if(g.getName().equals(name)){
                group = g;
            }
        }
        GROUPS.remove(group);
    }

    public static void addGroup(Group g){
        GROUPS.add(g);
    }

    public static void removeGroup(String name){
        GROUPS.remove(getGroupByName(name));
    }
    public static void removeGroup(Group g){
        GROUPS.remove(g);
    }

    public static Group getGroupByUser(User user){
        for(Group g : GROUPS){
            if(g.getUsers().contains(user)){
                return g;
            }
        }
        return null;
    }

    public static ArrayList<Group> getGroupsByUserId(Integer id){
        ArrayList<Group> userGroups = new ArrayList<Group>();
        for(Group g : GROUPS){
            for(User u : g.getUsers()) {
                if (u.getId().equals(id)){
                    userGroups.add(g);
                    break;
                }
            }
        }
        return userGroups;
    }

    public static String getInfo() {
        StringBuilder s = new StringBuilder("");
        for(Group g : getGROUPS()){
            for(User u : g.getUsers()) {
                s.append(g.getName()+" "+u.getId()+ " "+u.getFirstName());
            }
        }
        return s.toString();
    }
}
