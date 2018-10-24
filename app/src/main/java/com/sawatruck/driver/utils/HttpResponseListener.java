package com.sawatruck.driver.utils;

/**
 * Created by SKY on 9/19/2017.
 */

public interface HttpResponseListener {
    void OnSuccess(Object response);
    void OnFailure(Object error);
}
