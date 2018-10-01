package yan.githubsearch.data;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import yan.githubsearch.BuildConfig;

/**
 * Created by yan on 29/09/2018.
 */
public class ApiEndpoint {

    public static String getRepositoryEndpoint() {
        return BuildConfig.GITHUB_API_URL + "repos/";
    }

    public static String getIssueEndpoint(String repo) {
        return getRepositoryEndpoint() + repo + "/issues";
    }

    public static String getPullRequestEndpoint(String repo) {
        return getRepositoryEndpoint() + repo + "/pulls";
    }
}
