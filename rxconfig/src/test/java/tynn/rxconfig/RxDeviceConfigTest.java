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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import rx.observers.TestSubscriber;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.reflect.Whitebox.invokeConstructor;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RxDeviceConfig.class)
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
        whenNew(Configuration.class).withAnyArguments().thenReturn(configuration);
    }

    @Test
    public void call() throws Exception {
        TestSubscriber<Configuration> subscriber = TestSubscriber.create();
        TestStrategy strategy = new TestStrategy();
        RxDeviceConfig rxconfig = invokeConstructor(RxDeviceConfig.class, context, strategy);

        rxconfig.call(subscriber);
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
    public void emitConfig() throws Exception {
        TestSubscriber<Configuration> subscriber = TestSubscriber.create();

        RxDeviceConfig.emitConfig(subscriber, context);

        subscriber.assertNotCompleted();
        subscriber.assertNoErrors();
        subscriber.assertValue(configuration);
        verifyNew(Configuration.class).withArguments(configuration);
    }
}
