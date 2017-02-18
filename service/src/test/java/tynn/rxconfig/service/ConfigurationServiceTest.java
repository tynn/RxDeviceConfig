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

package tynn.rxconfig.service;

import android.content.res.Configuration;
import android.os.IBinder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import rx.observers.TestSubscriber;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ConfigurationService.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
public class ConfigurationServiceTest {

    ConfigurationService service;
    TestSubscriber<Configuration> subscriber;

    @Mock
    Configuration configuration;

    @Before
    public void setup() throws Exception {
        service = new ConfigurationService();
        subscriber = TestSubscriber.create();
        service.observers.add(subscriber);
        whenNew(Configuration.class).withAnyArguments().thenReturn(configuration);
    }

    @Test
    public void onBind() throws Exception {
        IBinder binder = service.onBind(null);

        assertThat(binder, instanceOf(ConfigurationService.OnRegisterSubscriber.class));
    }

    @Test
    public void onUnbind() throws Exception {
        boolean rebind = service.onUnbind(null);

        assertThat(rebind, is(false));
        assertThat(service.observers.isEmpty(), is(true));
    }

    @Test
    public void onConfigurationChanged() throws Exception {
        service.onConfigurationChanged(configuration);

        subscriber.assertNoErrors();
        subscriber.assertNotCompleted();
        subscriber.assertValue(configuration);
        verifyNew(Configuration.class).withArguments(configuration);
    }
}
