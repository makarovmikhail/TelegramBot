/**
 * Created by Mikhail on 26.11.2017.
 */

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;


import java.util.*;

public class MessageGenerator {
    static String start(Update update){
        StringBuilder s = new StringBuilder("");
        s.append("Приветствуем тебя " + update.getMessage().getFrom().getFirstName() + System.getProperty("line.separator"));
        s.append("Для начала либо присоединись к своим друзьям, либо создай свою группу с помощью: " + System.getProperty("line.separator"));
        s.append("/join <имя группы>" + System.getProperty("line.separator"));
        return s.toString();
    }
    static String join(Update update){
        String message  = update.getMessage().getText();
        message = message.replace("/join ","");
        if(message.length()<=0||message.startsWith("/join")){
            return "Введите <имя_группы>!";
        }
        if(DataWorker.containsGroupByName(message)){
            if(DataWorker.getGroupByName(message).containsUser(update.getMessage().getFrom().getId())) {
                return "Вы уже участник этой группы!";
            }else {
                User user = update.getMessage().getFrom();
                ArrayList<User> userList = DataWorker.getGroupByName(message).getUsers();
                userList.add(user);
                Group g = DataWorker.getGroupByName(message);
                DataWorker.removeGroupByName(message);
                g.setUsers(userList);
                DataWorker.addGroup(g);
                return "Вы успешно добавлены в группу: "+ message;
            }
        }else{
            User user = update.getMessage().getFrom();
            ArrayList<User> userArrayList = new ArrayList<User>();
            ArrayList<Place> placeArrayList = new ArrayList<Place>();
            userArrayList.add(user);
            DataWorker.addGroup(new Group(message,userArrayList,placeArrayList));
            return "Группа: " + message + " успешно создана, и Вы ее участник!";
        }
    }
    static String info(Update update){
        ArrayList<Group> groupList = DataWorker.getGroupsByUserId(update.getMessage().getFrom().getId());
        if(groupList.size()<=0) return "Вы не состоите ни в одной группе!";
        StringBuilder s = new StringBuilder("");
        for(Group g : groupList){
            s.append("Вы состоите в группе: " + g.getName() + System.getProperty("line.separator"));
        }
        return s.toString();
    }
    //Получаем управление над своими группами
    static SendMessage manage_groups(Update update){
        User user = update.getMessage().getFrom();
        ArrayList<Group> groups = DataWorker.getGroupsByUserId(user.getId());
        //Кнопки выхода из группы и пригласить участников
        /*InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList();
        List<InlineKeyboardButton> rowInline1 = new ArrayList();
        rowInline1.add(new InlineKeyboardButton().setText("Пригласить участников").setCallbackData("add_new_user"));
        rowInline1.add(new InlineKeyboardButton().setText("Покинуть группу").setCallbackData("leave_group"));
        List<InlineKeyboardButton> rowInline2 = new ArrayList();
        rowInline2.add(new InlineKeyboardButton().setText("Предложить место").setCallbackData("recommend"));
        rowInline2.add(new InlineKeyboardButton().setText("Задать свободное время").setCallbackData("pick_time"));
        // Set the keyboard to the markup
        rowsInline.add(rowInline1);
        rowsInline.add(rowInline2);
        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);*/
        SendMessage message = new SendMessage(); // Create a message object object
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList();
        if(groups.size()>0){
            for(Group g : groups){
                ArrayList<InlineKeyboardButton> list = new ArrayList<InlineKeyboardButton>();
                list.add(new InlineKeyboardButton().setText(g.getName()).setCallbackData("group_menu "+g.getName()));
                rowsInline.add(list);
            }
            message
                    .setChatId(update.getMessage().getChatId())
                    .setText("Группы, в которых вы состоите:");
            markupInline.setKeyboard(rowsInline);
            message.setReplyMarkup(markupInline);
        }else{
            message
                    .setChatId(update.getMessage().getChatId())
                    .setText("Вы не состоите ни в одной группе!");
        }
        return message;
    }

    static SendMessage users(Update update){
        SendMessage message = new SendMessage(); // Create a message object object
        if(update.getCallbackQuery().getData().split(" ").length>1){
            String groupName = update.getCallbackQuery().getData().split(" ")[1];
            ArrayList<User> users = DataWorker.getGroupByName(groupName).getUsers();
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList();
            for(User u : users){
                List<InlineKeyboardButton> list = new ArrayList<InlineKeyboardButton>();
                list.add(new InlineKeyboardButton()
                        .setText(u.getFirstName()+" "+u.getLastName())
                        .setCallbackData("user_info "+u.getId()));
                rowsInline.add(list);
            }
            markupInline.setKeyboard(rowsInline);
            message
                    .setChatId(update.getCallbackQuery().getMessage().getChatId())
                    .setText("Участники группы: " + groupName)
                    .setReplyMarkup(markupInline);
        }else{
            message
                    .setChatId(update.getCallbackQuery().getMessage().getChatId())
                    .setText("Ошибка получения участников группы");
        }
        return message;
    }

    static SendMessage leave_group(Update update){
        SendMessage message = new SendMessage(); // Create a message object object
        if(update.getCallbackQuery().getData().split(" ").length > 1) {
            String groupName = update.getCallbackQuery().getData().split(" ")[1];
            if (DataWorker.containsGroupByName(groupName)) {
                Group group = DataWorker.getGroupByName(groupName);
                ArrayList<User> userList = new ArrayList<User>();
                for (User u : group.getUsers()) {
                    if (!u.getId().equals(update.getCallbackQuery().getFrom().getId())) {
                        userList.add(u);
                    }
                }
                group.setUsers(userList);
                DataWorker.removeGroupByName(groupName);
                DataWorker.addGroup(group);
                message.setText("Вы успешно покинули группу: " + groupName);
            } else {
                message.setText("Такой группы не существует!");
            }
        }else{
            message.setText("Ошибка, при попытке покинуть группу!");
        }
        message.setChatId(update.getCallbackQuery().getMessage().getChatId());
        return message;
    }

    //Это элемент управления группой, покажет список мест группы
    static SendMessage places(Update update){
        SendMessage message = new SendMessage(); // Create a message object object
        if(update.getCallbackQuery().getData().split(" ").length>1){
            String groupName = update.getCallbackQuery().getData().split(" ")[1];
            ArrayList<Place> places = DataWorker.getGroupByName(groupName).getPlaces();
            ArrayList<Place> distinctPlaces = new ArrayList<Place>();
            for(Place p : places){
                if(!distinctPlaces.contains(p)){
                    distinctPlaces.add(p);
                }
            }
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList();
            for(Place p : distinctPlaces){
                List<InlineKeyboardButton> list = new ArrayList<InlineKeyboardButton>();
                list.add(new InlineKeyboardButton()
                        .setText(p.getName())
                        .setCallbackData("place_info " + p.getName()+"!!!"+groupName));
                rowsInline.add(list);
            }
            markupInline.setKeyboard(rowsInline);
            message
                    .setChatId(update.getCallbackQuery().getMessage().getChatId())
                    .setText("Доступные места:")
                    .setReplyMarkup(markupInline);
        }else{
            message
                    .setChatId(update.getCallbackQuery().getMessage().getChatId())
                    .setText("Ошибка получения мест");
        }
        return message;
    }

    //Голосует за место в списке мест групп
    static SendMessage place_info(Update update){
        SendMessage message = new SendMessage(); // Create a message object object
        String messageText  = update.getCallbackQuery().getData();
        System.out.println(messageText);
        //messageText становится тем, что нам нужно
        messageText = messageText.replace("place_info ","");
        if(messageText.split("!!!").length!=2) {
            message.setText("Ошибка выбора места!");
        }else {
            String placeName = messageText.split("!!!")[0];
            String groupName = messageText.split("!!!")[1];
            Group g = DataWorker.getGroupByName(groupName);
            ArrayList<Place> places = g.getPlaces();
            Place resPlace = null;
            for(Place p : places){
                if(p.getName().equals(placeName)){
                    resPlace = p;
                }
            }
            places.add(resPlace);
            g.setPlaces(places);
            DataWorker.removeGroupByName(groupName);
            DataWorker.addGroup(g);
            message.setText("Вы успешно выбрали место: " + placeName);
        }
        message.setChatId(update.getCallbackQuery().getMessage().getChatId());
        return message;
    }

    //Можно добавить место в свои места (НЕ В ГРУППУ)
    static SendMessage addplace(Update update){
        SendMessage message = new SendMessage(); // Create a message object object
        String messageText  = update.getMessage().getText();
        messageText = messageText.replace("/addplace ","");
        if(messageText.length()<=0||messageText.startsWith("/addplace")){
            message.setText("Введите /addplace <название_места>!");
        }else if(DataWorker.getPlacesByUserId(update.getMessage().getFrom().getId()).contains(messageText)) {
            message.setText("Это место уже есть в вашем списке!");
        }else{
            User user = update.getMessage().getFrom();
            ArrayList<Place> places = DataWorker.getPlacesByUserId(user.getId());
            places.add(new Place(messageText));
            Map<Integer,ArrayList<Place>> mapIdPlaces = DataWorker.getUser_id_places();
            mapIdPlaces.remove(user.getId());
            mapIdPlaces.put(user.getId(),places);
            DataWorker.setUser_id_places(mapIdPlaces);
            message.setText("Вы успешно добавили место: "+ messageText);

        }
        message.setChatId(update.getMessage().getChatId());
        return message;
    }

    //Можно добавить место в места группы!
    static SendMessage add_place(Update update){
        SendMessage message = new SendMessage(); // Create a message object object
        String messageText  = update.getCallbackQuery().getData();
        System.out.println(messageText);
        messageText = messageText.replace("add_place ","");
        if(messageText.split("!!!").length!=2) {
            message.setText("Ошибка рекомендации места!");
        }else {
            String placeName = messageText.split("!!!")[0];
            String groupName = messageText.split("!!!")[1];
            ArrayList<Place> places = DataWorker.getGroupByName(groupName).getPlaces();
            Place insertPlace = null;
            for(Place p : DataWorker.getPlacesByUserId(update.getCallbackQuery().getFrom().getId())){
                if(p.getName().equals(placeName)){
                    insertPlace = p;
                }
            }
            places.add(insertPlace);
            Group g = DataWorker.getGroupByName(groupName);
            g.setPlaces(places);
            DataWorker.removeGroupByName(groupName);
            DataWorker.addGroup(g);
            message.setText("Вы успешно рекомендовали место: " + placeName);
        }
        message.setChatId(update.getCallbackQuery().getMessage().getChatId());
        return message;
    }

    static SendMessage pick(Update update){
        SendMessage message = new SendMessage(); // Create a message object object
        String messageText  = update.getCallbackQuery().getData();
        System.out.println(messageText);
        //messageText становится тем, что нам нужно
        messageText = messageText.replace("pick ","");
        if(messageText.split("!!!").length!=2) {
            message.setText("Ошибка добавления места!");
        }else {
            String timeValue = messageText.split("!!!")[0];
            String groupName = messageText.split("!!!")[1];
            Group g = DataWorker.getGroupByName(groupName);
            ArrayList<String> times = g.getTime();
            times.add(timeValue);
            g.setTime(times);
            DataWorker.removeGroupByName(groupName);
            DataWorker.addGroup(g);
            message.setText("Вы успешно выбрали время: " + timeValue);
        }
        message.setChatId(update.getCallbackQuery().getMessage().getChatId());
        return message;
    }

    static SendMessage time_picker(Update update){
        SendMessage message = new SendMessage(); // Create a message object object
        if(update.getCallbackQuery().getData().split(" ").length > 1){
            String groupName = update.getCallbackQuery().getData().split(" ")[1];
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList();
            String startTime0 = "00:00-00:30";
            String startTime1 = "00:30-01:30";
            int hour = 0;
            for(int i = 0; i < 24; i++){
                List<InlineKeyboardButton> list = new ArrayList<InlineKeyboardButton>();
                list.add(new InlineKeyboardButton().setText(startTime0).setCallbackData("pick " + startTime0+"!!!"+groupName));
                list.add(new InlineKeyboardButton().setText(startTime1).setCallbackData("pick " + startTime1+"!!!"+groupName));
                rowsInline.add(list);
                hour++;
                if(String.valueOf(hour).length()>1){
                    startTime0 = String.valueOf(hour)+":00-"+String.valueOf(hour)+":30";
                    if(String.valueOf(hour+1).length()>1) {
                        startTime1 = String.valueOf(hour) + ":30-" + String.valueOf(hour + 1) + ":00";
                    }else{
                        startTime1 = String.valueOf(hour) + ":30-0" + String.valueOf(hour + 1) + ":00";
                    }
                }else{
                    startTime0 = "0"+String.valueOf(hour)+":00-0"+String.valueOf(hour)+":30";
                    if(String.valueOf(hour+1).length()>1) {
                        startTime1 = "0"+String.valueOf(hour) + ":30-" + String.valueOf(hour + 1) + ":00";
                    }else{
                        startTime1 = "0"+String.valueOf(hour) + ":30-0" + String.valueOf(hour + 1) + ":00";
                    }
                }
                if((hour+1)==24){
                    startTime1 = String.valueOf(hour) + ":30-00:00";
                }
            }
            markupInline.setKeyboard(rowsInline);
            message
                    .setChatId(update.getCallbackQuery().getMessage().getChatId())
                    .setText("Выберете время:")
                    .setReplyMarkup(markupInline);
        }else{
            message
                    .setChatId(update.getCallbackQuery().getMessage().getChatId())
                    .setText("Ошибка получения мест");
        }
        return message;
    }

    static SendMessage group_menu(Update update){

        SendMessage message = new SendMessage(); // Create a message object object

        if(update.getCallbackQuery().getData().split(" ").length > 1){
            String groupName = update.getCallbackQuery().getData().split(" ")[1];
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList();
            List<InlineKeyboardButton> rowInline1 = new ArrayList<InlineKeyboardButton>();
            rowInline1.add(new InlineKeyboardButton("Участники").setCallbackData("users " + groupName));
            rowInline1.add(new InlineKeyboardButton("Места").setCallbackData("places " + groupName));
            rowInline1.add(new InlineKeyboardButton("Рекомендовать").setCallbackData("recommend " + groupName));
            List<InlineKeyboardButton> rowInline2 = new ArrayList<InlineKeyboardButton>();
            rowInline2.add(new InlineKeyboardButton("Время").setCallbackData("time_picker " + groupName));
            rowInline2.add(new InlineKeyboardButton("Покинуть").setCallbackData("leave_group " + groupName));
            rowInline2.add(new InlineKeyboardButton("Оповещение").setCallbackData("notification " + groupName));
            rowsInline.add(rowInline1);
            rowsInline.add(rowInline2);
            markupInline.setKeyboard(rowsInline);
            message
                    .setChatId(update.getCallbackQuery().getMessage().getChatId())
                    .setText("Меню группы: "+groupName)
                    .setReplyMarkup(markupInline);
        }else{
            message
                    .setChatId(update.getCallbackQuery().getMessage().getChatId())
                    .setText("Ошибка получения меню группы");
        }

        return message;
    }

    //Добавляет место в группу, в меню которой сейчас находимся!
    static SendMessage recommend(Update update){
        SendMessage message = new SendMessage(); // Create a message object object
        User user = update.getCallbackQuery().getFrom();
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList();
        String groupName = update.getCallbackQuery().getMessage().getText().split(": ")[1];
        if(DataWorker.getPlacesByUserId(user.getId()).size()>0) {
            for (Place p : DataWorker.getPlacesByUserId(user.getId())) {
                List<InlineKeyboardButton> list = new ArrayList<InlineKeyboardButton>();
                list.add(new InlineKeyboardButton()
                        .setText(p.getName())
                        .setCallbackData("add_place " + p.getName()+"!!!"+groupName));
                rowsInline.add(list);
            }
            markupInline.setKeyboard(rowsInline);
            message
                    .setChatId(update.getCallbackQuery().getMessage().getChatId())
                    .setText("Доступные места:")
                    .setReplyMarkup(markupInline);
        }else {
            message
                    .setChatId(update.getCallbackQuery().getMessage().getChatId())
                    .setText("У вас нет ни одного места!");
        }
        return message;
    }

    static SendMessage myplaces(Update update){
        SendMessage message = new SendMessage(); // Create a message object object
        User user = update.getMessage().getFrom();
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList();
        if(DataWorker.getPlacesByUserId(user.getId()).size()>0) {
            for (Place p : DataWorker.getPlacesByUserId(user.getId())) {
                List<InlineKeyboardButton> list = new ArrayList<InlineKeyboardButton>();
                list.add(new InlineKeyboardButton()
                        .setText(p.getName())
                        .setCallbackData("place_info " + p.getName()));
                rowsInline.add(list);
            }
            markupInline.setKeyboard(rowsInline);
            message
                    .setChatId(update.getMessage().getChatId())
                    .setText("Доступные места:")
                    .setReplyMarkup(markupInline);
        }else {
            message
                    .setChatId(update.getMessage().getChatId())
                    .setText("У вас нет ни одного места!");
        }
        return message;
    }

    static SendMessage notification(Update update){
        SendMessage message = new SendMessage(); // Create a message object object
        if(update.getCallbackQuery().getData().split(" ").length>1){
            String groupName = update.getCallbackQuery().getData().split(" ")[1];
            Group g = DataWorker.getGroupByName(groupName);
            ArrayList<String> times = g.getTime();
            ArrayList<Place> places = g.getPlaces();
            HashMap<String, Integer> distinctTimes = new HashMap<String, Integer>();
            HashMap<Place, Integer> distinctPlaces = new HashMap<Place, Integer>();
            for(String t : times){
                if(!distinctTimes.containsKey(t)){
                    distinctTimes.put(t,1);
                }else{
                    distinctTimes.put(t,distinctTimes.get(t)+1);
                }
            }
            Integer maxCountTime = 0;
            String ansTime = "";
            for(String t : distinctTimes.keySet()){
                if (distinctTimes.get(t) > maxCountTime){
                    maxCountTime = distinctTimes.get(t);
                    ansTime = t;
                }
            }

            for(Place p : places){
                if(!distinctPlaces.containsKey(p)){
                    distinctPlaces.put(p,1);
                }else{
                    distinctPlaces.put(p,distinctPlaces.get(p)+1);
                }
            }
            Integer maxCountPlace = 0;
            String ansPlace = "";
            for(Place p : distinctPlaces.keySet()){
                if (distinctPlaces.get(p) > maxCountPlace){
                    maxCountPlace = distinctPlaces.get(p);
                    ansPlace = p.getName();
                }
            }
            message
                    .setChatId(update.getCallbackQuery().getMessage().getChatId())
                    .setText("Группа: "+groupName+System.getProperty("line.separator")
                            +"Идет в: "
                            +ansPlace+", промежуток времени: "+ansTime);
        }else{
            message
                    .setChatId(update.getCallbackQuery().getMessage().getChatId())
                    .setText("Ошибка получения уведомления");
        }
        return message;
    }
}