/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package net.ccbluex.liquidbounce.handler.payload

import io.netty.buffer.Unpooled

import net.ccbluex.liquidbounce.event.Listenable
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.handler
import net.ccbluex.liquidbounce.features.module.modules.client.BrandSpoofer.customValue
import net.ccbluex.liquidbounce.features.module.modules.client.BrandSpoofer.possibleBrands
import net.ccbluex.liquidbounce.utils.client.ClientUtils.LOGGER
import net.ccbluex.liquidbounce.utils.client.MinecraftInstance
import net.minecraft.network.PacketBuffer
import net.minecraft.network.play.client.C17PacketCustomPayload

object ClientFixes : MinecraftInstance, Listenable {

    var fmlFixesEnabled = true
    var blockFML = true
    var blockProxyPacket = true
    var blockPayloadPackets = true
    var blockResourcePackExploit = true

    val onPacket = handler<PacketEvent> { event ->
        runCatching {
            val packet = event.packet

            if (mc.isIntegratedServerRunning || !fmlFixesEnabled) {
                return@runCatching
            }

            when {
                blockProxyPacket && packet.javaClass.name == "net.minecraftforge.fml.common.network.internal.FMLProxyPacket" -> {
                    event.cancelEvent()
                    return@runCatching
                }

                packet is C17PacketCustomPayload -> when {
                    blockPayloadPackets && !packet.channelName.startsWith("MC|") -> {
                    event.cancelEvent()
                    }
                        packet.channelName == "MC|Brand" -> {
                    packet.data = PacketBuffer(Unpooled.buffer()).writeString(
                        when (possibleBrands.get()) {
                            "Vanilla" -> "vanilla"
                            "LunarClient" -> "lunarclient:v2.18.2-2449"
                            "OptiFine" -> "optifine"
                            "CheatBreaker" -> "CB"
                            "Fabric" -> "fabric"
                            "PvPLounge" -> "PLC18"
                            "Geyser" -> "geyser"
                            "Minebuilders" -> "minebuilders9"
                            "Feather" -> "feather"
                            "FML" -> "fml,forge"
                            "Log4j" -> "LOLG4J"
                            "Custom" -> customValue.get()
                            else -> {
                                // do nothing
                                return@runCatching
                            }
                        }
                    )
                }
            }
        }
    }.onFailure {
        LOGGER.error("Failed to handle packet on client fixes.", it)
        }
    }

    @JvmStatic
    fun getClientModName(): String {
        return possibleBrands.get()
    }
}