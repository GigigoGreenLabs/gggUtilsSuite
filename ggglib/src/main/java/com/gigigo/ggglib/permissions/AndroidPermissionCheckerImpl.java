/*
 * Created by Gigigo Android Team
 *
 * Copyright (C) 2016 Gigigo Mobile Services SL
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

package com.gigigo.ggglib.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4ox.content.ContextCompat;
import com.gigigo.ggglib.ContextProvider;
import com.gigigo.ggglib.permission.PermissionManager;
import com.gigigo.ggglib.permission.listener.single.CompositePermissionListener;
import com.gigigo.ggglib.permission.listener.single.PermissionListener;

public class AndroidPermissionCheckerImpl implements PermissionChecker {

  private final ContextProvider contextProvider;

  public AndroidPermissionCheckerImpl(Context context, ContextProvider contextProvider) {
    PermissionManager.initialize(context);
    this.contextProvider = contextProvider;
  }

  @Override public void askForPermission(Permission permission,
      UserPermissionRequestResponseListener userResponse, Activity activity) {
    if (PermissionManager.isRequestOngoing()) {
      return;
    }

    PermissionListener[] listeners = createListeners(permission, userResponse, activity);

    PermissionManager.checkPermission(new CompositePermissionListener(listeners),
        permission.getAndroidPermissionStringType());
  }



  @Override public void continuePendingPermissionsRequestsIfPossible() {
    PermissionManager.continuePendingRequestIfPossible(
        new ContinueRequestPermissionListenerImpl(contextProvider));
  }

  @Override public boolean isGranted(Permission permission) {
    Context context = contextProvider.getApplicationContext();
    int permissionGranted =
        ContextCompat.checkSelfPermission(context, permission.getAndroidPermissionStringType());
    return PackageManager.PERMISSION_GRANTED == permissionGranted;
  }

  private PermissionListener[] createListeners(Permission permission,
      UserPermissionRequestResponseListener userResponse, Activity activity) {
    PermissionListener basicListener = getPermissionListenerImpl(permission, userResponse);
    try {
      return new PermissionListener[] { basicListener };
    } catch (NullContainerException n) {
      return new PermissionListener[] { basicListener };
    }
  }

  private PermissionListener getPermissionListenerImpl(final Permission permission,
      final UserPermissionRequestResponseListener userPermissionRequestResponseListener) {
    return new GenericPermissionListenerImpl(permission, userPermissionRequestResponseListener,
        contextProvider);
  }
}
