/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package net.ccbluex.liquidbounce.features.module.modules.other

import net.ccbluex.liquidbounce.FDPClient.CLIENT_NAME
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.Category
import net.ccbluex.liquidbounce.utils.kotlin.RandomUtils.nextFloat
import net.ccbluex.liquidbounce.utils.kotlin.RandomUtils.nextInt
import net.ccbluex.liquidbounce.utils.kotlin.RandomUtils.randomString
import net.ccbluex.liquidbounce.utils.timing.MSTimer
import net.ccbluex.liquidbounce.utils.timing.TimeUtils.randomDelay
import net.ccbluex.liquidbounce.config.IntegerValue
import net.ccbluex.liquidbounce.config.TextValue
import net.ccbluex.liquidbounce.config.boolean
import net.ccbluex.liquidbounce.event.handler

object Spammer : Module("Spammer", Category.OTHER, subjective = true, hideModule = false) {
    private val maxDelayValue: IntegerValue = object : IntegerValue("MaxDelay", 1000, 0..5000) {
        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceAtLeast(minDelay)

        override fun onChanged(oldValue: Int, newValue: Int) {
            delay = randomDelay(minDelay, get())
        }
    }
    private val maxDelay by maxDelayValue

    private val minDelay: Int by object : IntegerValue("MinDelay", 500, 0..5000) {
        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceAtMost(maxDelay)

        override fun onChanged(oldValue: Int, newValue: Int) {
            delay = randomDelay(get(), maxDelay)
        }

        override fun isSupported() = !maxDelayValue.isMinimal()
    }

    private val message by
        TextValue("Message", "$CLIENT_NAME Client | fdpinfo.github(.io) | opZywl on GitHub")

    private val custom by boolean("Custom", false)

    private val msTimer = MSTimer()
    private var delay = randomDelay(minDelay, maxDelay)

    val onUpdate = handler<UpdateEvent> {
        if (msTimer.hasTimePassed(delay)) {
            mc.thePlayer.sendChatMessage(
                if (custom) replace(message)
                else message + " >" + randomString(nextInt(5, 11)) + "<"
            )
            msTimer.reset()
            delay = randomDelay(minDelay, maxDelay)
        }
    }

    private fun replace(text: String): String {
        var replacedStr = text

        replaceMap.forEach { (key, valueFunc) ->
            replacedStr = replacedStr.replace(key, valueFunc)
        }

        return replacedStr
    }

    private inline fun String.replace(oldValue: String, newValueProvider: () -> Any): String {
        var index = 0
        val newString = StringBuilder(this)
        while (true) {
            index = newString.indexOf(oldValue, startIndex = index)
            if (index == -1) {
                break
            }
            // You have to replace them one by one, otherwise all parameters like %s would be set to the same random string.
            val newValue = newValueProvider().toString()
            newString.replace(index, index + oldValue.length, newValue)
            index += newValue.length
        }
        return newString.toString()
    }


    private fun randomPlayer() =
        mc.netHandler.playerInfoMap
            .map { playerInfo -> playerInfo.gameProfile.name }
            .filter { name -> name != mc.thePlayer.name }
            .randomOrNull() ?: "none"

    private val replaceMap = mapOf(
        "%f" to { nextFloat().toString() },
        "%i" to { nextInt(0, 10000).toString() },
        "%ss" to { randomString(nextInt(1, 6)) },
        "%s" to { randomString(nextInt(1, 10)) },
        "%ls" to { randomString(nextInt(1, 17)) },
        "%p" to { randomPlayer() }
    )
}