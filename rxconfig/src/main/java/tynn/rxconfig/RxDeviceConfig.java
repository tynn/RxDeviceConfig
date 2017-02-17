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

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func2;

public class RxDeviceConfig implements Observable.OnSubscribe<Configuration> {

    @Nullable
    private static Strategy STRATEGY;

    private final Context context;
    private final Strategy strategy;

    private RxDeviceConfig(Context context, Strategy strategy) {
        this.context = context;
        this.strategy = strategy;
    }

    @Override
    public void call(Subscriber<? super Configuration> subscriber) {
        Observer<? super Configuration> observer = new SafeObserver<>(subscriber);
        subscriber.add(strategy.call(observer, context));
        emitConfig(subscriber, context);
    }

    /**
     * @param strategy
     */
    public static void setDefaultObserveStrategy(Strategy strategy) {
        STRATEGY = strategy;
    }

    /**
     * @return
     */
    public static Strategy getDefaultObserveStrategy() {
        return STRATEGY;
    }

    /**
     * @param context
     * @return
     */
    public static Observable<Configuration> observe(@NonNull Context context) {
        if (STRATEGY == null) {
            throw new IllegalStateException("default strategy is null");
        }
        return observe(context, STRATEGY);
    }

    /**
     * @param context
     * @param strategy
     * @return
     */
    public static Observable<Configuration> observe(@NonNull Context context, @NonNull Strategy strategy) {
        nonNull(strategy, "strategy");
        nonNull(context, "context");
        return Observable.create(new RxDeviceConfig(context, strategy)).onBackpressureLatest();
    }

    private static void nonNull(Object o, String name) {
        if (o == null) {
            throw new NullPointerException(name + " is null");
        }
    }

    static void emitConfig(Observer<? super Configuration> observer, Context context) {
        Configuration config = context.getResources().getConfiguration();
        config = new Configuration(config);
        observer.onNext(config);
    }

    public interface Strategy extends Func2<Observer<? super Configuration>, Context, Subscription> {
    }
}
