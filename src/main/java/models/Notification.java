package models;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Notification {

    private String name;
    private Long chatId;
    private String notification;
    private Long millisecondsFromEpoch;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return name.equals(that.name) &&
                chatId.equals(that.chatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, chatId);
    }
}
