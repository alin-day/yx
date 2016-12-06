package com.yxyige.msdk.api;

import android.os.Bundle;

public interface YXMResultListener {
    void onSuccess(Bundle bundle);

    void onFailture(int code, String msg);
}