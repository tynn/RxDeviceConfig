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
import android.content.Intent;
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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RxDeviceConfig.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
public class ConfigurationBroadcastReceiverTest {

    ConfigurationBroadcastReceiver receiver;
    TestSubscriber<Configuration> subscriber;

    @Mock
    Context context;
    @Mock
    Resources resources;
    @Mock
    Configuration configuration;

    @Before
    public void setup() throws Exception {
        subscriber = TestSubscriber.create();
        receiver = new ConfigurationBroadcastReceiver(subscriber, context);
        when(context.getResources()).thenReturn(resources);
        when(resources.getConfiguration()).thenReturn(configuration);
        whenNew(Configuration.class).withAnyArguments().thenReturn(configuration);
    }

    @Test
    public void onReceive() throws Exception {
        receiver.onReceive(context, new Intent());

        subscriber.assertNoErrors();
        subscriber.assertNotCompleted();
        subscriber.assertValue(configuration);
        verifyNew(Configuration.class).withArguments(configuration);
    }

    @Test
    public void unsubscribe() throws Exception {
        receiver.unsubscribe();

        verify(context).unregisterReceiver(receiver);
        assertThat(receiver.isUnsubscribed(), is(true));
    }

    @Test
    public void isUnsubscribed() throws Exception {
        assertThat(receiver.isUnsubscribed(), is(false));

        receiver.unsubscribe();

        assertThat(receiver.isUnsubscribed(), is(true));
    }
}
