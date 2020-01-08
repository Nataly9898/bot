package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@DatabaseTable(tableName = "Notifications")
public class Notification {

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField
    private String name;

    @DatabaseField(columnName = "Chat_Id")
    private Long chatId;


    @DatabaseField(columnName = "Notification")
    private String notification;

    @DatabaseField(columnName = "Date_time")
    private Long millisecondsFromEpoch;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @DatabaseField(columnName = "Status")
    private String status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public Long getMillisecondsFromEpoch() {
        return millisecondsFromEpoch;
    }

    public void setMillisecondsFromEpoch(Long millisecondsFromEpoch) {
        this.millisecondsFromEpoch = millisecondsFromEpoch;
    }

    @NotNull
    public static Boolean checkNameCorrectness(String argument) {
        String name= argument;

        Pattern pattern = Pattern.compile("\\S+");
        Matcher matcher = pattern.matcher(name);

        return matcher.matches();
    }

    @NotNull
    public static Boolean checkDateTimeCorrectness(String argument) {

        String date= argument;

        Pattern pattern = Pattern.compile("\\d{2}.\\d{2}.\\d{4} \\d{2}:\\d{2}");
        Matcher matcher = pattern.matcher(date);

        return matcher.matches();
    }


    public boolean setNotificationInfo(String[] arguments) {

        String date=arguments[1]+" "+arguments[2];

        if (!checkDateTimeCorrectness(date)) {
            return false;
        }

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd.MM.yyyy HH:mm");

        Date notificationDate = null;

        try {
            notificationDate=simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (notificationDate.getTime()<= System.currentTimeMillis()){
            return false;
        }

        StringBuilder sb=new StringBuilder();

        for (int i=3; i< arguments.length; i++){

            sb.append(arguments[i]);
        }

        String notificationText = sb.toString();

        this.setNotification(notificationText);
        this.setMillisecondsFromEpoch(notificationDate.getTime());
        this.setStatus("active");

        return true;
    }


    public Long getId() {
        return id;
    }
}
