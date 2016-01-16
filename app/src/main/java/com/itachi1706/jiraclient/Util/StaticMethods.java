package com.itachi1706.jiraclient.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by kennethsohyq on 16/1/16.
 * For com.itachi1706.jiraclient.Util in JiraClient
 */
public class StaticMethods {

    public static void showOkDialog(String title, String message, Context context) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(message)
                .setPositiveButton(android.R.string.ok, null).show();
    }

    public static JiraRestClient getJiraClient(Context context) {
        String url, user, userpw;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        url = sp.getString(StaticVariables.LAST_SERVER_URL, "");
        user = sp.getString(StaticVariables.LAST_USERNAME, "");
        userpw = sp.getString(StaticVariables.LAST_PASSWORD, "");

        URI jiraUri;

        try {
            jiraUri = new URI(url);
        } catch (URISyntaxException e) {
            StaticMethods.showOkDialog("Error", "Invalid URL", context);
            return null;
        }

        final JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        return factory.createWithBasicHttpAuthentication(jiraUri, user, userpw);
    }
}
