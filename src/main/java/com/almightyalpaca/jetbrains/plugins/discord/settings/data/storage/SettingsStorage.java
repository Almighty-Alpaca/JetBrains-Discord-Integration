/*
 * Copyright 2017-2018 Aljoscha Grebe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.almightyalpaca.jetbrains.plugins.discord.settings.data.storage;

import com.almightyalpaca.jetbrains.plugins.discord.settings.data.Settings;
import com.intellij.util.xmlb.annotations.Attribute;

import java.util.Objects;

public abstract class SettingsStorage implements Settings
{
    @Attribute
    private boolean enabled = true;

    @Override
    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (!(o instanceof SettingsStorage))
            return false;
        SettingsStorage that = (SettingsStorage) o;
        return isEnabled() == that.isEnabled();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(isEnabled());
    }
}
