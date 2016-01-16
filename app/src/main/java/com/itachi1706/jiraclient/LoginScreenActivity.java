package com.itachi1706.jiraclient;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.itachi1706.jiraclient.Util.StaticMethods;
import com.itachi1706.jiraclient.Util.StaticVariables;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

public class LoginScreenActivity extends AppCompatActivity implements View.OnClickListener {

    EditText serverURL, username, password;
    TextInputLayout tilServerURL, tilUsername, tilPassword;
    Button loginBtn;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        // Init elements
        serverURL = (EditText) findViewById(R.id.editServerURL);
        username = (EditText) findViewById(R.id.editUsername);
        password = (EditText) findViewById(R.id.editPassword);
        loginBtn = (Button) findViewById(R.id.btnLogin);
        tilServerURL = (TextInputLayout) findViewById(R.id.tilServerURL);
        tilPassword = (TextInputLayout) findViewById(R.id.tilPassword);
        tilUsername = (TextInputLayout) findViewById(R.id.tilUsername);

        // On Click Listeners
        loginBtn.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.contains(StaticVariables.LAST_USERNAME))
            username.setText(sp.getString(StaticVariables.LAST_USERNAME, ""));
        if (sp.contains(StaticVariables.LAST_PASSWORD))
            password.setText(sp.getString(StaticVariables.LAST_PASSWORD, ""));
        if (sp.contains(StaticVariables.LAST_SERVER_URL))
            serverURL.setText(sp.getString(StaticVariables.LAST_SERVER_URL, ""));
    }

    @Override
    public void onClick(View v) {
        // Make sure all 3 fields are filled
        tilUsername.setErrorEnabled(false);
        tilPassword.setErrorEnabled(false);
        tilServerURL.setErrorEnabled(false);
        boolean valid = true;
        if (username.getText().toString().trim().length() == 0) {
            tilUsername.setError("Invalid Username");
            tilUsername.setErrorEnabled(true);
            valid = false;
        }
        if (serverURL.getText().toString().trim().length() == 0) {
            tilServerURL.setError("Invalid JIRA URL");
            tilServerURL.setErrorEnabled(true);
            valid = false;
        }
        if (password.getText().toString().trim().length() == 0) {
            tilPassword.setError("Invalid Password");
            tilPassword.setErrorEnabled(true);
            valid = false;
        }

        if (!valid)
            return;

        String url = serverURL.getText().toString().trim();
        String user = username.getText().toString().trim();
        String userPW = password.getText().toString().trim();

        // Save recent stuff
        sp.edit().putString(StaticVariables.LAST_USERNAME, user).apply();
        sp.edit().putString(StaticVariables.LAST_PASSWORD, userPW).apply();
        sp.edit().putString(StaticVariables.LAST_SERVER_URL, url).apply();

        attemptLoginAndGetProjects();
    }

    private void attemptLoginAndGetProjects() {
        JiraRestClient client = StaticMethods.getJiraClient(this);
        StringBuilder builder = new StringBuilder();

        Iterator iterator = null;
        if (client == null) {
            StaticMethods.showOkDialog("Projects", "No Projects Found", this);
            return;
        }
        iterator = client.getProjectClient().getAllProjects().claim().iterator();
        while (iterator.hasNext()) {
            BasicProject project = (BasicProject) iterator.next();
            builder.append(project.getName()).append(" (").append(project.getKey()).append(")\n");
        }
        StaticMethods.showOkDialog("Projects", builder.toString(), this);
    }
}
