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

import rx.Observer;
import rx.Subscriber;

class SafeObserver<T> implements Observer<T> {

    private final Subscriber<T> subscriber;

    SafeObserver(Subscriber<T> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void onCompleted() {
        if (!subscriber.isUnsubscribed()) {
            subscriber.onCompleted();
        }
    }

    @Override
    public void onError(Throwable e) {
        if (!subscriber.isUnsubscribed()) {
            subscriber.onError(e);
        }
    }

    @Override
    public void onNext(T o) {
        if (!subscriber.isUnsubscribed()) {
            subscriber.onNext(o);
        }
    }
}
