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
package com.almightyalpaca.intellij.plugins.discord.utils;

import org.jgroups.JChannel;
import org.jgroups.View;

public class JGroupsUtil
{
    public static boolean isLeader(JChannel channel)
    {
        return isLeader(channel, channel.getView());
    }

    public static boolean isLeader(JChannel channel, View view)
    {
        return view.getCoord().equals(channel.getAddress());
    }
}
