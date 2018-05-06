package cm.g2i.lalalaworker.controllers.settings;

import android.content.Context;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.controllers.appdata.LaLaLaSQLiteOperations;
import cm.g2i.lalalaworker.models.Strikes;
import cm.g2i.lalalaworker.models.User;
import cm.g2i.lalalaworker.models.Worker;

/**
 * Created by Sim'S on 29/07/2017.
 */

public class Settings {
    public static String USER_TOWN = "";
    public static String USER_STREET = "";
    public static String ADD = "lalalaw.sitimmo.cm";
    public static Worker worker;
    public static User user;

    public static SettingUnit userTown;
    public static SettingUnit userStreet;

    public static SettingUnit numberSearchedWorkers;
    public static SettingUnit saveHistory;
    public static SettingUnit serverAdd;
    public static SettingUnit policeSize;
    public static SettingUnit notifications;
    public static SettingUnit smsTemplate;
    public static SettingUnit considerUserLocals;
    public static SettingUnit anonymousSignal;

    public static SettingUnit accountRenewalDate;
    public static SettingUnit accountExpirationDate;
    public static SettingUnit remainingTime;

    public static final String userTownName = "USER_TOWN";
    public static final String userStreetName = "USER_STREET";
    public static final String numberSearchedWorkersName = "NUM_SEARCHED_WRKS";
    public static final String saveHistoryName = "SAVE_HISTORY";
    public static final String serverAddName = "SERVER_ADD";
    public static final String policeSizeName = "POLICE_SIZE";
    public static final String notificationsName = "NOTIFS";
    public static final String smsTemplateName = "SMS_TEMPLATE";
    public static final String considerUserLocalsName = "CONSIDER_USER_LOCALS";
    public static final String anonymousSignalName = "ANONYMOUS_SIGNAL";

    public static final String accountRenewalDateName = "WKRACCRENEWALDATE";
    public static final String accountExpirationDateName = "WKRACCEXPIRATIONDATE";
    public static final String remainingTimeName = "REMAININGTIME";

    public static void setSettings(Context context){
        userTown = LaLaLaSQLiteOperations.getSettingUnitByName(userTownName, context);
        userStreet = LaLaLaSQLiteOperations.getSettingUnitByName(userStreetName, context);

        numberSearchedWorkers = LaLaLaSQLiteOperations.getSettingUnitByName(numberSearchedWorkersName, context);
        saveHistory = LaLaLaSQLiteOperations.getSettingUnitByName(saveHistoryName, context);
        considerUserLocals = LaLaLaSQLiteOperations.getSettingUnitByName(considerUserLocalsName, context);
        serverAdd = LaLaLaSQLiteOperations.getSettingUnitByName(serverAddName, context);
        policeSize = LaLaLaSQLiteOperations.getSettingUnitByName(policeSizeName, context);
        notifications = LaLaLaSQLiteOperations.getSettingUnitByName(notificationsName, context);
        smsTemplate = LaLaLaSQLiteOperations.getSettingUnitByName(smsTemplateName, context);
        anonymousSignal = LaLaLaSQLiteOperations.getSettingUnitByName(anonymousSignalName, context);
    }

    public static void initializeSettings(Context context){
        userStreet = new SettingUnit(userStreetName, "");
        userTown = new SettingUnit(userTownName, "");

        numberSearchedWorkers = new SettingUnit(numberSearchedWorkersName, "20");
        saveHistory = new SettingUnit(saveHistoryName, "true");
        considerUserLocals = new SettingUnit(considerUserLocalsName, "false");
        serverAdd = new SettingUnit(serverAddName, "10.0.2.2");
        policeSize = new SettingUnit(policeSizeName, context.getResources().getStringArray(R.array.police_sizes)[1]);
        notifications = new SettingUnit(notificationsName, "true");
        smsTemplate = new SettingUnit(smsTemplateName, "Template 1");
        anonymousSignal = new SettingUnit(anonymousSignalName, "false");

        LaLaLaSQLiteOperations.initializeSettingUnit(userStreet, context);
        LaLaLaSQLiteOperations.initializeSettingUnit(userTown, context);
        LaLaLaSQLiteOperations.initializeSettingUnit(numberSearchedWorkers, context);
        LaLaLaSQLiteOperations.initializeSettingUnit(saveHistory, context);
        LaLaLaSQLiteOperations.initializeSettingUnit(considerUserLocals, context);
        LaLaLaSQLiteOperations.initializeSettingUnit(serverAdd, context);
        LaLaLaSQLiteOperations.initializeSettingUnit(policeSize, context);
        LaLaLaSQLiteOperations.initializeSettingUnit(notifications, context);
        LaLaLaSQLiteOperations.initializeSettingUnit(smsTemplate, context);
        LaLaLaSQLiteOperations.initializeSettingUnit(anonymousSignal, context);

        setSettings(context);
    }

    public static void initializeServerAdd(Context context){
        serverAdd = new SettingUnit(serverAddName, "10.0.2.2");
        LaLaLaSQLiteOperations.initializeSettingUnit(serverAdd, context);
        serverAdd = LaLaLaSQLiteOperations.getSettingUnitByName(serverAddName, context);
    }

    public static void setWkrsAccSettings(String[] dates, Context context){
        accountRenewalDate = new SettingUnit(accountRenewalDateName, dates[0]);
        accountExpirationDate = new SettingUnit(accountExpirationDateName, dates[1]);
        remainingTime = new SettingUnit(remainingTimeName, dates[2]);
        LaLaLaSQLiteOperations.updateSettingUnit(accountRenewalDate, context);
        LaLaLaSQLiteOperations.updateSettingUnit(accountExpirationDate, context);
        LaLaLaSQLiteOperations.updateSettingUnit(remainingTime, context);
    }

    public static void getWkrsAccSettings(Context context){
        accountRenewalDate = LaLaLaSQLiteOperations.getSettingUnitByName(accountRenewalDateName, context);
        accountExpirationDate = LaLaLaSQLiteOperations.getSettingUnitByName(accountExpirationDateName, context);
        remainingTime = LaLaLaSQLiteOperations.getSettingUnitByName(remainingTimeName, context);
    }
}
