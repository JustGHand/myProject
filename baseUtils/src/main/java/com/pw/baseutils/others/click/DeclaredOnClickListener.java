package com.pw.baseutils.others.click;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DeclaredOnClickListener implements View.OnClickListener {
    private final View mHostView;
    private final String mMethodName;

    private Method mResolvedMethod;
    private Context mResolvedContext;

    public DeclaredOnClickListener(@NonNull View hostView, @NonNull String methodName) {
        mHostView = hostView;
        mMethodName = methodName;
    }

    @Override
    public void onClick(@NonNull View v) {
        if (mResolvedMethod == null) {
            resolveMethod(mHostView.getContext(), mMethodName);
        }

        try {
            mResolvedMethod.invoke(mResolvedContext, v);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(
                    "Could not execute non-public method for android:onClick", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(
                    "Could not execute method for android:onClick", e);
        }
    }

    @NonNull
    private void resolveMethod(@Nullable Context context, @NonNull String name) {
        while (context != null) {
            try {
                if (!context.isRestricted()) {
                    final Method method = context.getClass().getMethod(mMethodName, View.class);
                    if (method != null) {
                        mResolvedMethod = method;
                        mResolvedContext = context;
                        return;
                    }
                }
            } catch (NoSuchMethodException e) {
                // Failed to find method, keep searching up the hierarchy.
            }

            if (context instanceof ContextWrapper) {
                context = ((ContextWrapper) context).getBaseContext();
            } else {
                // Can't search up the hierarchy, null out and fail.
                context = null;
            }
        }

        final int id = mHostView.getId();
        final String idText = id == View.NO_ID ? "" : " with id '"
                + mHostView.getContext().getResources().getResourceEntryName(id) + "'";
        throw new IllegalStateException("Could not find method " + mMethodName
                + "(View) in a parent or ancestor Context for android:onClick "
                + "attribute defined on view " + mHostView.getClass() + idText);
    }
}
