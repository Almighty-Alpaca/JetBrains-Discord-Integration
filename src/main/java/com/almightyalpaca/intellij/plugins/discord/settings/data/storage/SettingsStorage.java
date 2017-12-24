/*
 * Copyright 2017 Aljoscha Grebe
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
package com.almightyalpaca.intellij.plugins.discord.settings.data.storage;

import com.almightyalpaca.intellij.plugins.discord.settings.data.Settings;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

public abstract class SettingsStorage<T extends SettingsStorage<T, V>, V extends Settings<T>> implements Settings<T>
{
    @NotNull
    private transient final Class<T> clazz;
    @NotNull
    private transient final Supplier<T> factory;

    private boolean enabled = true;

    public SettingsStorage(@NotNull Class<T> clazz, @NotNull Supplier<T> factory)
    {
        this.clazz = clazz;
        this.factory = factory;
    }

    @Override
    public boolean isEnabled()
    {
        return enabled;
    }

    public T setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        return clazz.cast(this);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @NotNull
    public T clone()
    {
        return factory.get().setEnabled(enabled);
    }

    @NotNull
    public T clone(@NotNull V settings)
    {
        return setEnabled(settings.isEnabled());
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (!(o instanceof SettingsStorage))
            return false;
        SettingsStorage<?, ?> that = (SettingsStorage<?, ?>) o;
        return isEnabled() == that.isEnabled();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(isEnabled());
    }
}
