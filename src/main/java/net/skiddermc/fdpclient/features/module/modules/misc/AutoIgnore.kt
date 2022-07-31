/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package net.skiddermc.fdpclient.features.module.modules.misc

import net.skiddermc.fdpclient.FDPClient
import net.skiddermc.fdpclient.event.EventTarget
import net.skiddermc.fdpclient.event.PacketEvent
import net.skiddermc.fdpclient.features.module.Module
import net.skiddermc.fdpclient.features.module.ModuleCategory
import net.skiddermc.fdpclient.features.module.ModuleInfo
import net.skiddermc.fdpclient.ui.client.hud.element.elements.Notification
import net.skiddermc.fdpclient.ui.client.hud.element.elements.NotifyType
import net.skiddermc.fdpclient.value.IntegerValue
import net.minecraft.network.play.client.C01PacketChatMessage
import net.minecraft.network.play.server.S02PacketChat

@ModuleInfo(name = "AutoIgnore", category = ModuleCategory.MISC)
class AutoIgnore : Module() {
    private val minDelayValue = IntegerValue("MinDelay", 3000, 1000, 5000)
    private val vlValue = IntegerValue("IgnoreVL", 3, 1, 7)

    private val chatTimes = HashMap<String, Long>()
    private val chatVL = HashMap<String, Float>()
    private val blockedPlayer = ArrayList<String>()

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is C01PacketChatMessage) {
            val msg = event.packet.message
            if (msg.startsWith("/ignorar remover ", ignoreCase = true)) {
                blockedPlayer.remove((msg.replace("/ignorar remover ", "").lowercase()))
            }
        }

        if (event.packet is S02PacketChat) {
            val msg = event.packet.chatComponent.unformattedText
            if (msg.contains("Mensagem de", ignoreCase = true)) {
                val nowTime = System.currentTimeMillis()
                val name = msg.split(":")[0].replace("Mensagem de ", "")
                if (blockedPlayer.contains(name.lowercase())) {
                    event.cancelEvent()
                    return
                }

                val vl = chatVL.getOrDefault(name, 0F)
                if ((nowTime - chatTimes.getOrDefault(name, 0)) <minDelayValue.get()) {
                    chatVL[name] = vl + 1
                } else {
                    if (vl> 1) {
                        chatVL[name] = vl - 0.5F
                    }
                }

                chatTimes[name] = System.currentTimeMillis()

                if (chatVL[name]!!> vlValue.get()) {
                    mc.thePlayer.sendChatMessage("/ignorar add $name")
                    FDPClient.hud.addNotification(Notification(this.name, "$name ignored for spamming...", NotifyType.INFO, time = 1500))
                    blockedPlayer.add(name.lowercase())
                    event.cancelEvent()
                }
            }
        }
    }
} 