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
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.RequiresApi;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.Locale;

import rx.observers.TestSubscriber;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.reflect.Whitebox.invokeConstructor;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RxDeviceConfig.class, ToConfigurationDiffSubscriber.class})
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
public class RxDeviceConfigTest {

    @Mock
    Context context;
    @Mock
    Resources resources;
    @Mock
    Configuration configuration;

    @Before
    public void setup() throws Exception {
        RxDeviceConfig.setDefaultObserveStrategy(null);
        when(context.getResources()).thenReturn(resources);
        when(resources.getConfiguration()).thenReturn(configuration);
        when(configuration.updateFrom(any(Configuration.class))).thenReturn(2);
        whenNew(Configuration.class).withAnyArguments().thenReturn(configuration);
    }

    @Test
    public void call() throws Exception {
        TestSubscriber<Configuration> subscriber = TestSubscriber.create();
        TestStrategy strategy = new TestStrategy();
        RxDeviceConfig rxConfig = invokeConstructor(RxDeviceConfig.class, context, strategy);

        rxConfig.call(subscriber);
        subscriber.unsubscribe();

        strategy.assertUnsubscribed();
        strategy.assertCalledWith(context);
        strategy.assertCalledWith(SafeObserver.class);
        subscriber.assertUnsubscribed();
        subscriber.assertNoErrors();
        subscriber.assertNotCompleted();
        subscriber.assertValue(configuration);
        verifyNew(Configuration.class).withArguments(configuration);
    }

    @Test
    public void setDefaultObserveStrategy() throws Exception {
        RxDeviceConfig.Strategy strategy = new TestStrategy();

        RxDeviceConfig.setDefaultObserveStrategy(new TestStrategy());
        RxDeviceConfig.setDefaultObserveStrategy(strategy);

        assertThat(RxDeviceConfig.getDefaultObserveStrategy(), is(strategy));
    }

    @Test
    public void getDefaultObserveStrategy() throws Exception {
        RxDeviceConfig.Strategy strategy = new TestStrategy();
        RxDeviceConfig.setDefaultObserveStrategy(strategy);

        assertThat(RxDeviceConfig.getDefaultObserveStrategy(), is(strategy));
    }

    @Test
    public void observe() throws Exception {
        TestSubscriber<Configuration> subscriber = TestSubscriber.create();
        TestStrategy strategy = new TestStrategy();
        RxDeviceConfig.setDefaultObserveStrategy(strategy);

        RxDeviceConfig.observe(context).subscribe(subscriber).unsubscribe();

        strategy.assertUnsubscribed();
        subscriber.assertUnsubscribed();
        subscriber.assertNoErrors();
        subscriber.assertNotCompleted();
        subscriber.assertValue(configuration);
        verifyNew(Configuration.class).withArguments(configuration);
    }

    @Test(expected = IllegalStateException.class)
    public void observe_no_strategy() throws Exception {
        RxDeviceConfig.observe(context);
    }

    @Test(expected = NullPointerException.class)
    public void observe_null_context() throws Exception {
        RxDeviceConfig.setDefaultObserveStrategy(new TestStrategy());
        RxDeviceConfig.observe(null);
    }

    @Test
    public void observe_strategy() throws Exception {
        TestSubscriber<Configuration> subscriber = TestSubscriber.create();
        TestStrategy strategy = new TestStrategy();

        RxDeviceConfig.observe(context, strategy).subscribe(subscriber).unsubscribe();

        strategy.assertUnsubscribed();
        subscriber.assertUnsubscribed();
        subscriber.assertNoErrors();
        subscriber.assertNotCompleted();
        subscriber.assertValue(configuration);
        verifyNew(Configuration.class).withArguments(configuration);
    }

    @Test(expected = NullPointerException.class)
    public void observe_strategy_null_context() throws Exception {
        RxDeviceConfig.observe(null, new TestStrategy());
    }

    @Test(expected = NullPointerException.class)
    public void observe_strategy_null_strategy() throws Exception {
        RxDeviceConfig.observe(context, null);
    }

    @Test
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void observeChanges() throws Exception {
        TestSubscriber<Integer> subscriber = TestSubscriber.create();
        TestStrategy strategy = new TestStrategy();
        RxDeviceConfig.setDefaultObserveStrategy(strategy);

        RxDeviceConfig.observeChanges(context).subscribe(subscriber);
        strategy.emitter.onNext(configuration);
        subscriber.unsubscribe();

        strategy.assertUnsubscribed();
        subscriber.assertUnsubscribed();
        subscriber.assertNoErrors();
        subscriber.assertNotCompleted();
        subscriber.assertValue(2);
        verifyNew(Configuration.class).withArguments(configuration);
    }

    @Test(expected = IllegalStateException.class)
    public void observeChanges_no_strategy() throws Exception {
        RxDeviceConfig.observeChanges(context);
    }

    @Test(expected = NullPointerException.class)
    public void observeChanges_null_context() throws Exception {
        RxDeviceConfig.setDefaultObserveStrategy(new TestStrategy());
        RxDeviceConfig.observeChanges(null);
    }

    @Test
    public void observeChanges_strategy() throws Exception {
        TestSubscriber<Integer> subscriber = TestSubscriber.create();
        TestStrategy strategy = new TestStrategy();

        RxDeviceConfig.observeChanges(context, strategy).subscribe(subscriber);
        strategy.emitter.onNext(configuration);
        subscriber.unsubscribe();

        strategy.assertUnsubscribed();
        subscriber.assertUnsubscribed();
        subscriber.assertNoErrors();
        subscriber.assertNotCompleted();
        subscriber.assertValue(2);
        verifyNew(Configuration.class).withArguments(configuration);
    }

    @Test(expected = NullPointerException.class)
    public void observeChanges_strategy_null_context() throws Exception {
        RxDeviceConfig.observeChanges(null, new TestStrategy());
    }

    @Test(expected = NullPointerException.class)
    public void observeChanges_strategy_null_strategy() throws Exception {
        RxDeviceConfig.observeChanges(context, null);
    }

    @Test
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void observePrimaryLocale() throws Exception {
        TestSubscriber<Locale> subscriber = TestSubscriber.create();
        TestStrategy strategy = new TestStrategy();
        RxDeviceConfig.setDefaultObserveStrategy(strategy);
        whenNew(ToPrimaryLocale.class).withNoArguments()
                .thenReturn(new TestToPrimaryLocale(Locale.FRANCE));

        RxDeviceConfig.observePrimaryLocale(context).subscribe(subscriber).unsubscribe();

        strategy.assertUnsubscribed();
        subscriber.assertUnsubscribed();
        subscriber.assertNoErrors();
        subscriber.assertNotCompleted();
        subscriber.assertValue(Locale.FRANCE);
        verifyNew(Configuration.class).withArguments(configuration);
    }

    @Test(expected = IllegalStateException.class)
    public void observePrimaryLocale_no_strategy() throws Exception {
        RxDeviceConfig.observePrimaryLocale(context);
    }

    @Test(expected = NullPointerException.class)
    public void observePrimaryLocale_null_context() throws Exception {
        RxDeviceConfig.setDefaultObserveStrategy(new TestStrategy());
        RxDeviceConfig.observePrimaryLocale(null);
    }

    @Test
    public void observePrimaryLocale_strategy() throws Exception {
        TestSubscriber<Locale> subscriber = TestSubscriber.create();
        TestStrategy strategy = new TestStrategy();
        whenNew(ToPrimaryLocale.class).withNoArguments()
                .thenReturn(new TestToPrimaryLocale(Locale.ITALY));

        RxDeviceConfig.observePrimaryLocale(context, strategy).subscribe(subscriber).unsubscribe();

        strategy.assertUnsubscribed();
        subscriber.assertUnsubscribed();
        subscriber.assertNoErrors();
        subscriber.assertNotCompleted();
        subscriber.assertValue(Locale.ITALY);
        verifyNew(Configuration.class).withArguments(configuration);
    }

    @Test(expected = NullPointerException.class)
    public void observePrimaryLocale_strategy_null_context() throws Exception {
        RxDeviceConfig.observePrimaryLocale(null, new TestStrategy());
    }

    @Test(expected = NullPointerException.class)
    public void observePrimaryLocale_strategy_null_strategy() throws Exception {
        RxDeviceConfig.observePrimaryLocale(context, null);
    }
}
