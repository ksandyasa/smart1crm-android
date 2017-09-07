/*
 * Copyright 2017 Matthew Lim
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sales1crm.ekamant.sales1crm.activities.utilities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * This class will check Runtime Permission. It will ask the user if they allowed the application
 * to access device Camera, User Location, Phone Call, and Storage.
 * @author Matthew
 */

public class PermissionCheck {

    public static final int  REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    public static final int REQUEST_CODE_ASK_PERMISSION = 123;

    private int currentApiVersion = Build.VERSION.SDK_INT;

    public boolean checkPermission(final Context context) {
        if (currentApiVersion >= Build.VERSION_CODES.M) {

            List<String> permissionsNeeded = new ArrayList<String>();

            final List<String> permissionsList = new ArrayList<String>();
            if (!addPermission(permissionsList, Manifest.permission.CAMERA, context))
                permissionsNeeded.add("Camera");
            if (!addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION, context))
                permissionsNeeded.add("NetworkLocation");
            if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION, context))
                permissionsNeeded.add("GPSLocation");
            if (!addPermission(permissionsList, Manifest.permission.CALL_PHONE, context))
                permissionsNeeded.add("Phone");
            if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE, context))
                permissionsNeeded.add("WriteStorage");
            if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE, context))
                permissionsNeeded.add("ReadStorage");


            if (permissionsList.size() > 0) {
                if (permissionsNeeded.size() > 0) {
                    /*String message = "You need to grant access to " + permissionsNeeded.get(0);
                    for (int i = 0; i < permissionsNeeded.size(); i++) {
                        message = message + ", " + permissionsNeeded.get(i);

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setMessage(message);
                        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions((Activity) context
                                        , permissionsList.toArray(new String[permissionsList.size()])
                                        , REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                        AlertDialog alert = alertBuilder.create();
                        alert.show();


                    }*/

                    ActivityCompat.requestPermissions((Activity)context
                            ,permissionsList.toArray(new String[permissionsList.size()]),
                            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);

                }
            }
            return true;
        } else {
            return true;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionList, String permission, Context context) {

        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(permission);

            if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission))
                ;
            return false;
        }

        return true;

    }
}
