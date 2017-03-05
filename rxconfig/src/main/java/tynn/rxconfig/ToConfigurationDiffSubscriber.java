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

package tynn.rxconfig;

import android.content.res.Configuration;

import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observer;
import rx.Subscriber;

class ToConfigurationDiffSubscriber extends Subscriber<Configuration> {

    private AtomicBoolean initialized = new AtomicBoolean();

    private final Observer<? super Integer> observer;
    private final Configuration configuration;

    ToConfigurationDiffSubscriber(Subscriber<? super Integer> subscriber) {
        super(subscriber);
        observer = subscriber;
        configuration = new Configuration();
    }

    @Override
    public void onStart() {
        request(1);
    }

    @Override
    public void onCompleted() {
        if (!isUnsubscribed()) {
            observer.onCompleted();
        }
    }

    @Override
    public void onError(Throwable e) {
        if (!isUnsubscribed()) {
            observer.onError(e);
        }
    }

    @Override
    public void onNext(Configuration configuration) {
        if (initialized.compareAndSet(false, true)) {
            if (!isUnsubscribed()) {
                this.configuration.setTo(configuration);
                request(1);
            }
        } else {
            int diff = this.configuration.updateFrom(configuration);
            if (!isUnsubscribed()) {
                if (diff != 0) {
                    observer.onNext(diff);
                }
                request(1);
            }
        }
    }
}
