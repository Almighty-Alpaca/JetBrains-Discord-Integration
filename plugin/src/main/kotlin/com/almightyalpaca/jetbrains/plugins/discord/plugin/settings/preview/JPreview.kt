package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.preview

import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.renderer.Renderer
import kotlinx.coroutines.*
import javax.swing.ImageIcon
import javax.swing.JLabel
import kotlin.coroutines.CoroutineContext

class JPreview : JLabel(), CoroutineScope {
    private val parentJob: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + parentJob

    private val preview = PreviewRenderer()

    var type: Renderer.Type
        get() = preview.type
        set(value) {
            preview.type = value
            update()

        }

    private var updateJob: Job? = null

    init {
        icon = ImageIcon(preview.dummy)

        update(true)
    }

    @Synchronized
    fun update(force: Boolean = false) {
        updateJob?.cancel()

        updateJob = launch {
            val (modified, image) = preview.draw(force)

            if (modified) {
                icon = ImageIcon(image)
            }

            updateJob = launch {
                delay(1000)
                update()
            }
        }
    }
}
