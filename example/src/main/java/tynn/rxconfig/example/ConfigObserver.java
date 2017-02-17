/*
 * Copyright (C) 2017 Christian Schmitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tynn.rxconfig.example;

import android.content.res.Configuration;
import android.util.Log;
import android.widget.TextView;

import rx.Observer;
import rx.Subscription;
import tynn.rxconfig.RxDeviceConfig;

class ConfigObserver implements Observer<Configuration> {

    private final TextView textView;

    private ConfigObserver(TextView textView) {
        this.textView = textView;
    }

    @Override
    public void onCompleted() {
        Log.i(MainActivity.TAG, "completed");
    }

    @Override
    public void onError(Throwable e) {
        Log.i(MainActivity.TAG, "error", e);
    }

    @Override
    public void onNext(Configuration configuration) {
        String config = configuration.toString();
        Log.i(MainActivity.TAG, "next configuration=" + config);
        textView.setText(config);
    }

    static Subscription off(MainActivity activity, int id, RxDeviceConfig.Strategy strategy) {
        TextView textView = (TextView) activity.findViewById(id);
        ConfigObserver observer = new ConfigObserver(textView);
        return RxDeviceConfig.observe(activity, strategy).subscribe(observer);
    }
}
