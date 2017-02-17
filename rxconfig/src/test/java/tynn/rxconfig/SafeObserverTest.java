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

import org.junit.Before;
import org.junit.Test;

import rx.observers.TestSubscriber;

public class SafeObserverTest {

    SafeObserver<String> observer;
    TestSubscriber<String> subscriber;

    @Before
    public void setup() {
        subscriber = TestSubscriber.create();
        observer = new SafeObserver<>(subscriber);
    }

    @Test
    public void onCompleted() throws Exception {
        observer.onCompleted();

        subscriber.assertCompleted();
    }

    @Test
    public void onCompleted_unsubscribed() throws Exception {
        subscriber.unsubscribe();

        observer.onCompleted();

        subscriber.assertNotCompleted();
    }

    @Test
    public void onError() throws Exception {
        Throwable error = new IllegalStateException();

        observer.onError(error);

        subscriber.assertError(error);
    }

    @Test
    public void onError_unsubscribed() throws Exception {
        Throwable error = new IllegalStateException();
        subscriber.unsubscribe();

        observer.onError(error);

        subscriber.assertNoErrors();
    }

    @Test
    public void onNext() throws Exception {
        String value = "value";

        observer.onNext(value);

        subscriber.assertValue(value);
    }

    @Test
    public void onNext_unsubscribed() throws Exception {
        String value = "value";
        subscriber.unsubscribe();

        observer.onNext(value);

        subscriber.assertNoValues();
    }
}
