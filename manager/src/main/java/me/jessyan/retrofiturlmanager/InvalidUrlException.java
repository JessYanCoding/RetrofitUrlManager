package me.jessyan.retrofiturlmanager;

import android.text.TextUtils;

/**
 * Created by yu on 2017/7/24.
 */
public class InvalidUrlException extends RuntimeException {

    public InvalidUrlException(String url) {
        super("You've configured an invalid url : " + (TextUtils.isEmpty(url) ? "EMPTY_OR_NULL_URL" : url));
    }
}
