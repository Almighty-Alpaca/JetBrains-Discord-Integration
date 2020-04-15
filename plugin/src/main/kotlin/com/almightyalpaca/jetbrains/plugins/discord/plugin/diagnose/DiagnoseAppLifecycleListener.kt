/*
 * Copyright 2017-2019 Aljoscha Grebe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.almightyalpaca.jetbrains.plugins.discord.plugin.diagnose

import com.intellij.ide.AppLifecycleListener
import com.intellij.openapi.project.Project

class DiagnoseAppLifecycleListener : AppLifecycleListener {
    override fun appStarting(projectFromCommandLine: Project?) {
        diagnoseService.discord
        diagnoseService.plugins
        diagnoseService.ide
    }
}
