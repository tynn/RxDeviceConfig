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

import android.content.Context;
import android.content.res.Configuration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.observers.TestSubscriber;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationServiceConnectionTest {

    ConfigurationServiceConnection connection;
    TestSubscriber<Configuration> subscriber;

    @Mock
    Context context;

    @Before
    public void setup() {
        subscriber = TestSubscriber.create();
        connection = new ConfigurationServiceConnection(subscriber, context);
    }

    @Test
    public void onServiceConnected() throws Exception {
        ConfigurationService service = new ConfigurationService();

        connection.onServiceConnected(null, service.new OnRegisterSubscriber());

        assertThat(service.observers.contains(subscriber), is(true));
    }

    @Test
    public void onServiceDisconnected() throws Exception {
        connection.onServiceDisconnected(null);

        subscriber.assertError(ServiceDisconnected.class);
        subscriber.assertNotCompleted();
        subscriber.assertNoValues();
    }

    @Test
    public void unsubscribe() throws Exception {
        connection.unsubscribe();

        verify(context).unbindService(connection);
        assertThat(connection.isUnsubscribed(), is(true));
    }

    @Test
    public void isUnsubscribed() throws Exception {
        assertThat(connection.isUnsubscribed(), is(false));

        connection.unsubscribe();

        assertThat(connection.isUnsubscribed(), is(true));
    }
}
