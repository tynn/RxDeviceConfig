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

package tynn.rxconfig.component;

import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.atomic.AtomicReference;

import rx.Observer;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.powermock.reflect.Whitebox.getInternalState;

@RunWith(MockitoJUnitRunner.class)
public class ComponentStrategyTest {

    @Mock
    Context context;
    @Mock
    Observer observer;

    @Test
    public void call() throws Exception {
        ComponentStrategy strategy = new ComponentStrategy();

        ConfigurationComponentCallbacks callbacks
                = (ConfigurationComponentCallbacks) strategy.call(observer, context);

        AtomicReference<Context> context = getInternalState(callbacks, AtomicReference.class);
        assertThat(context.get(), is(this.context));
        Observer observer = getInternalState(callbacks, Observer.class);
        assertThat(observer, is(this.observer));
        verify(this.context).registerComponentCallbacks(callbacks);
    }
}
