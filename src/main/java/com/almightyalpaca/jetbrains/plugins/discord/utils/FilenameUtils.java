/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.almightyalpaca.jetbrains.plugins.discord.utils;

public class FilenameUtils
{
    public static String getExtension(String filename)
    {
        if (filename == null)
        {
            return null;
        }
        else
        {
            int index = indexOfExtension(filename);
            return index == -1 ? "" : filename.substring(index + 1);
        }
    }

    private static int indexOfExtension(String filename)
    {
        if (filename == null)
        {
            return -1;
        }
        else
        {
            int extensionPos = filename.lastIndexOf(46);
            int lastSeparator = indexOfLastSeparator(filename);
            return lastSeparator > extensionPos ? -1 : extensionPos;
        }
    }

    private static int indexOfLastSeparator(String filename)
    {
        if (filename == null)
        {
            return -1;
        }
        else
        {
            int lastUnixPos = filename.lastIndexOf(47);
            int lastWindowsPos = filename.lastIndexOf(92);
            return Math.max(lastUnixPos, lastWindowsPos);
        }
    }

    public static String getBaseName(String filename)
    {
        return removeExtension(getName(filename));
    }

    public static String removeExtension(String filename)
    {
        if (filename == null)
        {
            return null;
        }
        else
        {
            failIfNullBytePresent(filename);
            int index = indexOfExtension(filename);
            return index == -1 ? filename : filename.substring(0, index);
        }
    }

    public static String getName(String filename)
    {
        if (filename == null)
        {
            return null;
        }
        else
        {
            failIfNullBytePresent(filename);
            int index = indexOfLastSeparator(filename);
            return filename.substring(index + 1);
        }
    }

    private static void failIfNullBytePresent(String path)
    {
        int len = path.length();

        for (int i = 0; i < len; ++i)
        {
            if (path.charAt(i) == 0)
            {
                throw new IllegalArgumentException("Null byte present in file/path name. There are no known legitimate use cases for such data, but several injection attacks may use it");
            }
        }
    }
}
