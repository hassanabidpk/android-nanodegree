package com.udacity.gradle.builditbigger;

import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;
import android.util.Pair;

/**
 * Created by hassanabid on 12/13/15.
 */
public class NonEmptyStringTest extends AndroidTestCase {

    public void test() {

        String resultStr = null;

        EndpointsAsyncTask task =  new EndpointsAsyncTask(getContext());
        task.execute();
        try {
            resultStr = task.get();
            Log.d("NonEmptyStringTest", " result : " + resultStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(resultStr);

    }
}
