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
import android.os.Build;

import java.util.Locale;

import rx.functions.Func1;

class ToPrimaryLocale implements Func1<Configuration, Locale> {

    @Override
    @SuppressWarnings("deprecation")
    public Locale call(Configuration configuration) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return configuration.locale;
        } else {
            return configuration.getLocales().get(0);
        }
    }
}
