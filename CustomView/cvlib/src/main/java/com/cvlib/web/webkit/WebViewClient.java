/*
 * Copyright 2015-present wequick.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.cvlib.web.webkit;

import android.content.Context;
import android.graphics.Bitmap;


public abstract class WebViewClient {
    /**
     * @param context the activity of the WebView
     */
    public void onPageStarted(Context context, WebView view, String url, Bitmap favicon) {}

    /**
     * @param context the activity of the WebView
     */
    public void onPageFinished(Context context, WebView view, String url) {}

    /**
     * @param context the activity of the WebView
     */
    public void onReceivedError(Context context, WebView view, int errorCode,
                                String description, String failingUrl) {}

    /**
     * Tell the host application the current progress of loading a page.
     * @param view The WebView that initiated the callback.
     * @param newProgress Current page loading progress, represented by
     *                    an integer between 0 and 100.
     */
    public void onProgressChanged(Context context, WebView view, int newProgress) {}
}
