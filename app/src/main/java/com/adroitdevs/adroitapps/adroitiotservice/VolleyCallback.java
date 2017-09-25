package com.adroitdevs.adroitapps.adroitiotservice;

import org.json.JSONObject;

/**
 * Created by rexchris on 16/08/17.
 */

public interface VolleyCallback {
    void onSuccess(boolean result);

    void onSuccessJsonObject(JSONObject result);
}
