package com.nurram.project.imagetextrecognition.util;

import android.util.Log;

import com.detectlanguage.DetectLanguage;
import com.detectlanguage.Result;
import com.detectlanguage.errors.APIError;

import java.util.List;

public class LangDetect {
    private static LangDetect sInstance;
    private boolean isReliable = false;

    private LangDetect() {
    }

    public static LangDetect getInstance() {
        if (sInstance == null) {
            sInstance = new LangDetect();
        }

        return sInstance;
    }

    public String detectLanguage(String text) throws APIError {
        DetectLanguage.apiKey = "76c80eb1836ed8d6ccefd8507416a52c";
        List<Result> results = DetectLanguage.detect(text);
        Result resultVal = null;

        for (Result result : results) {
            Log.d("TAG", result.language);
            Log.d("TAG", result.isReliable + "");
            if (result.language.equals("en") && result.isReliable) {
                isReliable = true;
            }

            resultVal = result;
        }

        if (isReliable) {
            return resultVal.language;
        } else {
            return "";
        }
    }
}
