package com.kt.gigastorage.mobile.service;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.widget.Toast;

import com.kt.gigastorage.mobile.activity.MainActivity;

/**
 * Created by araise on 2016-11-03.
 */

public class ResponseFailCode {

    public static int UNAUTHENTICATION = 400; // 인증 정보 없음
    public static int AUTHENTICATIONFAILURE = 410; // 로그인 실패
    public static int ACCESSFAILURE = 440; // 접근 권한 없음
    public static int FAIL = 999; // 요청실패
    public static final int OFFLINE = 501; // 오프라인

    public String responseFail(int responseCode) {
        String message = "";
        if (responseCode == UNAUTHENTICATION) { // Code 400 인증오류
            message = "세션에 정보가 없습니다. 로그인 페이지로 이동합니다.";
        } else if (responseCode == AUTHENTICATIONFAILURE) { // Code 410 로그인 실패
            message = "로그인 정보가 올바르지 않습니다.";
        } else if (responseCode == ACCESSFAILURE) { // Code 440 접근 권한 없음
            message = "접근 권한이 없습니다.";
        } else if (responseCode == OFFLINE) {
            message = "원격지 PC가 오프라인 상태입니다.";
        } else if (responseCode == FAIL){
            message = "요청에 실패 하였습니다.";
        } else {
            message = "요청 처리가 완료되었습니다.";
        }
        return message;
    }

}
