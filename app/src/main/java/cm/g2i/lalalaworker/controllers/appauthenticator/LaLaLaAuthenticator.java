package cm.g2i.lalalaworker.controllers.appauthenticator;

import android.Manifest;
import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import cm.g2i.lalalaworker.ui.activities.RegisterUserActivity;

/**
 * Created by Sim'S on 28/07/2017.
 */

public class LaLaLaAuthenticator extends AbstractAccountAuthenticator {

    private Context context;

    public static String ACCOUNT_TYPE = "com.lalala.user";
    public static String WORKER_ACCOUNT_TYPE = "lalalaworker";
    private static String AUTH_TOKEN_KEY = "FullAccess";
    private static String NEW_ACCOUNT_KEY = "NewAccount";

    private static String AUTH_TOKEN_DEFAULT = "OK";

    public LaLaLaAuthenticator(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse accountAuthenticatorResponse, String accountType, String authTokenType,
                             String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        final Intent intent = new Intent(context, RegisterUserActivity.class);
        intent.putExtra(ACCOUNT_TYPE, accountType);
        intent.putExtra(AUTH_TOKEN_KEY, authTokenType);
        intent.putExtra(NEW_ACCOUNT_KEY, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, accountAuthenticatorResponse);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String authTokenType, Bundle bundle) throws NetworkErrorException {
        AccountManager manager = AccountManager.get(context);

        String authToken = manager.peekAuthToken(account, authTokenType);
        System.out.println(authToken);

        if (TextUtils.isEmpty(authToken)) {
            authToken = AUTH_TOKEN_DEFAULT;
        }
        System.out.println(authToken);

        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }

        final Intent intent = new Intent(context, RegisterUserActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, accountAuthenticatorResponse);
        intent.putExtra(ACCOUNT_TYPE, account.type);
        intent.putExtra(AUTH_TOKEN_KEY, authTokenType);

        final Bundle _bundle = new Bundle();
        _bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String s) {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String[] strings) throws NetworkErrorException {
        return null;
    }

    public static void createAccount(String name, Context context) {
        Account account = new Account(name, ACCOUNT_TYPE);

        AccountManager manager = AccountManager.get(context);
        manager.addAccountExplicitly(account, "lalala", null);
        manager.setAuthToken(account, AUTH_TOKEN_KEY, AUTH_TOKEN_DEFAULT);
    }

    public static void createWorkerAccount(String name, String passwd, Context context) {
        Account account = new Account(name, WORKER_ACCOUNT_TYPE);

        AccountManager manager = AccountManager.get(context);
        manager.addAccountExplicitly(account, passwd, null);
        manager.setAuthToken(account, AUTH_TOKEN_KEY, AUTH_TOKEN_DEFAULT);
    }

    public static Account[] getAccountsByType(String type, Context context) {
        AccountManager manager = AccountManager.get(context);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("WHAT?");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Account[] accounts = manager.getAccountsByType(type);
        return accounts;
    }
}
