/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package net.ccbluex.liquidbounce.features.module.modules.visual

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue

@ModuleInfo(name = "ViewBobbing", category = ModuleCategory.VISUAL)
object ViewBobbing : Module() {
    private val noBob = BoolValue("NoBob", false)
    private val customBobbing = BoolValue("CustomBobbing", true)
    private val bobbingAmount = FloatValue("BobbingAmount", 0.03f, -0.5f, 0.5f).displayable { customBobbing.get() }

    @EventTarget
    fun onMotion(event: MotionEvent?) {
        if (customBobbing.get() && mc.thePlayer.onGround && MovementUtils.isMoving()) {
            mc.thePlayer.cameraYaw = bobbingAmount.value
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (noBob.get()) {
            mc.thePlayer.distanceWalkedModified = 0f
        }
    }
}
