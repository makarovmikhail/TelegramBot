/**
 * Created by Mikhail on 26.11.2017.
 */

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.toIntExact;

public class PlanBot extends TelegramLongPollingBot {

    PlanBot() {
        DataWorker.setGROUPS(new ArrayList<Group>());
        DataWorker.setAuthorisedUsers(new ArrayList<User>());
        DataWorker.setUser_id_places(new HashMap<Integer,ArrayList<Place>>());
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            if(message_text.equals("/start")){
                message_text = MessageGenerator.start(update);
                SendMessage message = new SendMessage() // Create a message object object
                        .setChatId(chat_id)
                        .setText(message_text);
                try {
                    execute(message); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if(message_text.startsWith("/join")){
                message_text = MessageGenerator.join(update);
                SendMessage message = new SendMessage() // Create a message object object
                        .setChatId(chat_id)
                        .setText(message_text);
                try {
                    execute(message); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                // кнопки с названиями групп
            }else if(message_text.startsWith("/myplaces")){
                try {
                    execute(MessageGenerator.myplaces(update)); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                // кнопки с названиями групп
            }else if(message_text.startsWith("/addplace")){
                try {
                    execute(MessageGenerator.addplace(update)); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }else if(message_text.equals("/manage")){
                try {
                    execute(MessageGenerator.manage_groups(update)); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }else {
                SendMessage message = new SendMessage() // Create a message object object
                        .setChatId(chat_id)
                        .setText(message_text);
                try {
                    execute(message); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }else if(update.hasCallbackQuery()){
            String call_data = update.getCallbackQuery().getData();
            call_data = call_data.split(" ")[0];
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();


            if (call_data.equals("group_menu")) {
                try {
                    execute(MessageGenerator.group_menu(update));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }else if(call_data.equals("users")){
                try {
                    execute(MessageGenerator.users(update));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }else if(call_data.equals("leave_group")){
                try {
                    execute(MessageGenerator.leave_group(update));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }else if(call_data.equals("recommend")){
                try {
                    execute(MessageGenerator.recommend(update));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }else if(call_data.equals("time_picker")){
                try {
                    execute(MessageGenerator.time_picker(update));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }else if(call_data.equals("places")){
                try {
                    execute(MessageGenerator.places(update));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }else if(call_data.equals("notification")){
                try {
                    execute(MessageGenerator.notification(update));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }else if(call_data.equals("add_place")){
                try {
                    execute(MessageGenerator.add_place(update));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }else if(call_data.equals("pick")){
                try {
                    execute(MessageGenerator.pick(update));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }else if(call_data.equals("place_info")){
                try {
                    execute(MessageGenerator.place_info(update));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "WhereAreWeGoBot";
    }

    @Override
    public String getBotToken() {
        return "492895138:AAFV-_XOu2CuoUp6V3YIaoZkXXXAuD_IIvc";
    }
}
