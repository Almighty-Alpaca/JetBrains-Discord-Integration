/*
 * Copyright 2017-2020 Aljoscha Grebe
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

package gamesdk.impl.managers

import gamesdk.api.*
import gamesdk.api.managers.ImageManager
import gamesdk.api.types.DiscordImageHandle
import gamesdk.api.types.DiscordImageSize
import gamesdk.impl.*
import gamesdk.impl.types.*
import java.awt.Transparency
import java.awt.color.ColorSpace
import java.awt.image.*

internal class NativeImageManagerImpl(core: NativeCoreImpl) : NativeObjectImpl.Delegate(core), ImageManager {
    override fun fetch(handle: DiscordImageHandle, refresh: Boolean, callback: DiscordImageHandleResultCallback): Unit =
        native { pointer -> fetch(pointer, handle.toNativeDiscordImageHandle(), refresh, callback.toNativeDiscordResultObjectCallback(NativeDiscordImageHandle::toDiscordImageHandle)) }

    override suspend fun fetch(handle: DiscordImageHandle, refresh: Boolean): DiscordImageHandleResult =
        suspendCallback { callback -> fetch(handle, refresh, callback) }

    override fun getDimensions(handle: DiscordImageHandle): DiscordImageDimensionsResult =
        native { pointer -> getDimensions(pointer, handle.toNativeDiscordImageHandle()).toDiscordObjectResult(NativeDiscordImageDimensions::toDiscordImageDimensions) }

    override fun getData(handle: DiscordImageHandle, dataLength: DiscordImageSize): DiscordObjectResult<ByteArray> =
        native { pointer -> getData(pointer, handle.toNativeDiscordImageHandle(), dataLength.toNativeDiscordImageSize()).toDiscordObjectResult() }

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun getData(handle: DiscordImageHandle): DiscordObjectResult<ByteArray> =
        getDimensions(handle).flatMap { dimensions -> getData(handle, dimensions.width * dimensions.height * 4u) }

    @OptIn(ExperimentalUnsignedTypes::class)
    override suspend fun getImage(handle: DiscordImageHandle): DiscordObjectResult<BufferedImage> =
        fetch(handle, true).flatMap { fetchedHandle ->
            getDimensions(fetchedHandle).flatMap { dimensions ->
                getData(fetchedHandle, dimensions.width * dimensions.height * 4u).map { data ->
                    createRgbaImage(dimensions.width.toInt(), dimensions.height.toInt(), data)
                }
            }
        }

    @OptIn(ExperimentalUnsignedTypes::class)
    private fun createRgbaImage(width: Int, height: Int, data: ByteArray): BufferedImage {
        // val image = BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR)
        // image.raster.setDataElements(0, 0, width, height, data)
        // return image

        // For some reason the code above also works (although it's ARGB instead of RGBA) but by creating
        // the image manually we can avoid to create a entire new DataBuffer and copy everything over
        // and instead use the ByteArray directly

        val cs = ColorSpace.getInstance(ColorSpace.CS_sRGB)
        val nBits = intArrayOf(8, 8, 8, 8)
        val bOffs = intArrayOf(0, 1, 2, 3)
        val colorModel = ComponentColorModel(
            cs, nBits, true, false,
            Transparency.TRANSLUCENT,
            DataBuffer.TYPE_BYTE
        )

        val dataBuffer = DataBufferByte(data, data.size)

        val raster = Raster.createInterleavedRaster(
            dataBuffer,
            width, height,
            width * 4, 4,
            bOffs, null
        )

        return BufferedImage(colorModel, raster, false, null)
    }
}

private external fun Native.fetch(pointer: NativePointer, handle: NativeDiscordImageHandle, refresh: Boolean, callback: NativeDiscordImageHandleResultCallback)

private external fun Native.getDimensions(pointer: NativePointer, handle: NativeDiscordImageHandle): NativeDiscordImageDimensionsResult

private external fun Native.getData(pointer: NativePointer, handle: NativeDiscordImageHandle, dataLength: NativeDiscordImageSize): NativeDiscordObjectResult<ByteArray>
