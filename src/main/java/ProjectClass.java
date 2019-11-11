import org.telegram.telegrambots.api.methods.ForwardMessage;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import org.bson.Document;
import org.json.JSONObject;

import static java.lang.Math.toIntExact;
//import static java.lang.Math.toIntExact;

public class ProjectClass extends TelegramLongPollingBot {
    private static DecimalFormat df = new DecimalFormat("0.0");
    public List<User> usersList = new ArrayList<User>();
    public List<Long> usersId = new ArrayList<Long>();

    public void onUpdateReceived(Update update) {
        int week=0;
        float newdata=-1;
        int cstatus =-1;
        int[] wbwmid= new int[80];
        int j=0;
        for (int i=3510; i<3586; i++) {
            wbwmid[j] = i + 1;
            j=j+1;
        }
        wbwmid[18]=3525;wbwmid[19]=3526;wbwmid[20]=3527;wbwmid[21]=3528;
        wbwmid[14]=3533;wbwmid[15]=3534;wbwmid[16]=3536;wbwmid[17]=3537;

        long receivedId = update.getMessage().getChatId();
        System.out.println(update.getMessage().getChatId());
        int ind = usersId.indexOf(receivedId);
        if (ind == -1){
            System.out.println("USER NO EXIST");
            User newuser = new User(receivedId);
            usersList.add(newuser);
            usersId.add(receivedId);
            ind =usersList.size()-1;
        }
        User currentuser =usersList.get(ind);


        SendMessage message = new SendMessage();
        ForwardMessage fmessage = new ForwardMessage();
        int fm =0;

        String WhatWeight = "لطفا وزن قبل از بارداریتون رو به کیلوگرم بنویسید.";
        String WhatHeight = "قد شما چند سانتی متر است؟";
        String WhichWeek = "به راهنمای هفته به هفته بارداری خوش آمدید،\n" +
                " راهنمای کدام هفته بارداری را می خواهید؟";
        String Welcome = "این ربات برای مامان هاست و مربوط به کانال @mamanhaa هست. شما می تونید با استفاده از دستور های زیر از ابزارهای موجود استفاده کنید.\n" +
                "\n" +
                "دستورهای موجود در این ربات:\n" +
                "\n" +
                "/bmi می توانید بی ام آی خود را حساب کنید و بر اساس آن میزان وزنی که در بارداری باید اضافه کنید را بدانید.\n" +
                "\n" +
                "/wbw می توانید راهنمای تصویری هر هفته از بارداری را به دلخواه خود ببینید";

        String command = update.getMessage().getText();
        if (currentuser.status ==0) {
            System.out.println("status is 0");
            if (command.equals("/start")) {
                message.setText(Welcome);
            } else if (command.equals("/bmi")) {
                message.setText(WhatWeight);
                currentuser.update(1);
            } else if (command.equals("/wbw")) {
                message.setText(WhichWeek);
                currentuser.update(10);

            }
            else{
                message.setText(Welcome);
            }
        }
        else if (currentuser.status==1) {
            System.out.println(command);
            System.out.println("status is 1");
            try {
                currentuser.weight = Integer.parseInt(command);
                if (currentuser.weight<15 || currentuser.weight>120){
                    message.setText("اشتباهه! لطفا فقط وزن رو به کیلوگرم بنویس!");
                    currentuser.errortimes = currentuser.errortimes + 1;
                }else {
                    newdata = (float) (currentuser.weight);
                    cstatus = 1;
                    message.setText(WhatHeight);
                    currentuser.update(2);
                }
            } catch (NumberFormatException e) {
                message.setText("اشتباهه! لطفا دوباره وارد کنید");
                currentuser.errortimes = currentuser.errortimes + 1;
            }
            if (currentuser.errortimes > 2) {
                    currentuser.update(0);
                    currentuser.errortimes =0;
                    message.setText(Welcome);
            }



        }
        else if (currentuser.status ==2) {
            int ok = 0;
            try {
                currentuser.height = Integer.parseInt(command);
                if (currentuser.height<100 || currentuser.height>260){
                    currentuser.errortimes = currentuser.errortimes + 1;
                    message.setText("اشتباهه! لطفا فقط قد رو به سانتی متر بنویس!");
                    ok=0;
                }
                else{ok=1;cstatus=2;newdata = (float) (currentuser.height);}
            } catch (NumberFormatException e) {
                message.setText("اشتباهه! لطفا دوباره وارد کنید");
                ok =0;
                currentuser.errortimes = currentuser.errortimes + 1;}
            if (currentuser.errortimes > 2) {
                    currentuser.errortimes =0;
                    currentuser.status = 0;
                    message.setText(Welcome);
                    ok =0;
            }


            if (ok==1) {
                String message1 = "";
                String message2 = "";
                System.out.println(currentuser.weight);
                System.out.println(currentuser.height);
                float bmi = currentuser.weight / (currentuser.height * currentuser.height / 10000);
                if (bmi < 18.5) {
                    message1 = "13 تا 18 کیلوگرم";
                    message2 = "لاغر";

                } else if (bmi >= 18.5 && bmi < 25) {
                    message1 = "11 تا 16 کیلوگرم";
                    message2 = "نرمال";
                } else if (bmi >= 25 && bmi < 30) {
                    message1 = "7 تا 11 کیلوگرم";
                    message2 = "اضافه وزن";
                } else {
                    message1 = "5 تا 9 کیلوگرم";
                    message2 = "خیلی چاق";
                }
                message.setText(" وزنی که در بارداری باید اضافه کنید:\n" + message1 + "\n\n" + "وزن گیری در بارداری به شاخه توده بدنی (BMI) قبل از بارداری ارتباط دارد. BMI شما " + df.format(bmi) + "   بوده. (" + message2 + ")");
                currentuser.update(0);
            }
        } else if (currentuser.status ==10){
            message.setText(WhichWeek);
            int ok;
            try {
                week = Integer.parseInt(command);
                currentuser.status = 0;
                ok = 1;
                newdata = (float) week;
                cstatus =10;
            } catch (NumberFormatException e) {
                message.setText("اشتباهه! لطفا دوباره وارد کنید");
                ok =0;
                currentuser.errortimes = currentuser.errortimes + 1;
                if (currentuser.errortimes > 2) {
                    currentuser.status = 0;
                    currentuser.errortimes =0;
                    message.setText("اشتباهه!"+"\n"+Welcome);
                }
            }
            if (ok==1) {
                if (week==1){
                    message.setText("بارداری از اولین روز آخرین قاعدگی محاسبه و شروع می شود در حالی که تخمک گذاری (و در نتیجه لقاح) دوهفته بعد از روز اول قاعدگی می باشد. در نتیجه، مادر در واقع در دو هفته اول بارداری باردار نیست");
                }else if(week>40){
                    message.setText("بارداری معمولا 40 هفته است");
                }
                else {
                    currentuser.update(0);
                    fmessage.setChatId(update.getMessage().getChatId());
                    long fromchatid = 111470775;
                    fmessage.setFromChatId(fromchatid);
                    fmessage.setMessageId(wbwmid[week - 2]);
                    fm = 1;
                }
            }
        }
        else{
            message.setText(Welcome);
        }

        message.setChatId(receivedId);
        usersList.set(ind,currentuser);

        if (fm ==0) {
            try {
                execute(message);

            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }else{
            try {
                execute(fmessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        String user_first_name = update.getMessage().getChat().getFirstName();
        String user_last_name = update.getMessage().getChat().getLastName();
        String user_username = update.getMessage().getChat().getUserName();
        long user_id = update.getMessage().getChat().getId();
        String message_text = update.getMessage().getText();
        long chat_id = update.getMessage().getChatId();
        check(user_first_name, user_last_name, toIntExact(user_id), user_username,cstatus,newdata);



    }
    private String check(String first_name, String last_name, int user_id, String username, int status, float newdata) {
        MongoClientURI connectionString = new MongoClientURI("mongodb://localhost:27017");
        MongoClient mongoClient = new MongoClient(connectionString);
        MongoDatabase database = mongoClient.getDatabase("db_name");
        MongoCollection<Document> collection = database.getCollection("users");
        long found = collection.count(Document.parse("{id : " + Integer.toString(user_id) + "}"));
        if (found == 0) {
            Document doc = new Document("first_name", first_name)
                    .append("last_name", last_name)
                    .append("id", user_id)
                    .append("username", username);
            collection.insertOne(doc);
            mongoClient.close();
            System.out.println("User not exists in database. Written.");
            return "no_exists";
        } else {
            System.out.println("User exists in database.");
            if (status == 1){
                collection.updateOne(Filters.eq("id", user_id), Updates.set("weight",newdata));
                System.out.println("Weight update successfully...");
             }
            else if (status == 2){
                collection.updateOne(Filters.eq("id", user_id), Updates.set("height",newdata));
                System.out.println("Height update successfully...");
            }
            else if (status == 10){
                collection.updateOne(Filters.eq("id", user_id), Updates.set("week",newdata));
                System.out.println("week n. update successfully...");
            }


            mongoClient.close();
            return "exists";
        }


    }
    public String getBotUsername() {
        return "Mamanhaabot";
    }

    public String getBotToken() {
        return "306507309:AAEqRCN1r9_bqztER76UZDNVAJolVralwwM";
    }
}
