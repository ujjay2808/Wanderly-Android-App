package com.ujjay.wanderly.api;

import com.ujjay.wanderly.models.APIResponse;

public interface APICallback {
    void onSuccess(APIResponse response);
    void onError(String error);
}