/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.event.GameTickEvent
import net.ccbluex.liquidbounce.event.Render2DEvent
import net.ccbluex.liquidbounce.features.module.Category
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.utils.client.ClientUtils.displayChatMessage
import net.ccbluex.liquidbounce.utils.inventory.SilentHotbar
import net.ccbluex.liquidbounce.utils.inventory.InventoryUtils
import net.ccbluex.liquidbounce.utils.inventory.hotBarSlot
import net.ccbluex.liquidbounce.config.boolean
import net.ccbluex.liquidbounce.config.choices
import net.ccbluex.liquidbounce.config.int
import net.ccbluex.liquidbounce.event.handler
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.potion.Potion

object AutoPlay : Module("AutoPlay", Category.PLAYER, gameDetecting = false, hideModule = false) {

    private val mode by choices("Mode", arrayOf("Paper", "Hypixel"), "Paper")

    // Hypixel Settings
    private val hypixelMode by choices("HypixelMode", arrayOf("Skywars", "Bedwars"), "Skywars") {
        mode == "Hypixel"
    }
    private val skywarsMode by choices("SkywarsMode", arrayOf("SoloNormal", "SoloInsane"), "SoloNormal") {
        hypixelMode == "Skywars"
    }
    private val bedwarsMode by choices("BedwarsMode", arrayOf("Solo", "Double", "Trio", "Quad"), "Solo") {
        hypixelMode == "Bedwars"
    }

    private val delay by int("Delay", 50, 0..200)

    private val bedWarsHelp by boolean("BedWarsHelp", true)

    private val itemChecker by boolean("Item-Checker", true) { bedWarsHelp }
    private val stoneSword by boolean("Stone-Sword", false) { itemChecker }
    private val ironSword by boolean("Iron-Sword", true) { itemChecker }
    private val diamondSword by boolean("Diamond-Sword", true) { itemChecker }
    private val fireBallSword by boolean("FireBall", true) { itemChecker }
    private val enderPearl by boolean("EnderPearl", true) { itemChecker }
    private val tnt by boolean("TNT", true) { itemChecker }
    private val obsidian by boolean("Obsidian", true) { itemChecker }
    private val invisibilityPotion by boolean("InvisibilityPotion", true) { itemChecker }
    private val diamondArmor by boolean("DiamondArmor", true) { bedWarsHelp }

    private val stoneSwordList = ArrayList<String>()
    private val ironSwordList = ArrayList<String>()
    private val diamondSwordList = ArrayList<String>()
    private val fireBallList = ArrayList<String>()
    private val enderpearlList = ArrayList<String>()
    private val tntList = ArrayList<String>()
    private val obsidianList = ArrayList<String>()
    private val diamondArmorList = ArrayList<String>()
    private val invisibilityPotionList = ArrayList<String>()

    private var delayTick = 0

    /**
     * Update Event
     */

    val onGameTick = handler<GameTickEvent> {
        val player = mc.thePlayer ?: return@handler

        if (!playerInGame() || !player.inventory.hasItemStack(ItemStack(Items.paper))) {
            if (delayTick > 0)
                delayTick = 0

            return@handler
        } else {
            delayTick++
        }

        when (mode) {
            "Paper" -> {
                val paper = InventoryUtils.findItem(36, 44, Items.paper) ?: return@handler

                SilentHotbar.selectSlotSilently(this, paper, immediate = true, resetManually = true)

                if (delayTick >= delay) {
                    mc.playerController.sendUseItem(player, mc.theWorld, player.hotBarSlot(paper).stack)
                    delayTick = 0
                }
            }

            "Hypixel" -> {
                if (delayTick >= delay) {
                    when (hypixelMode.lowercase()) {
                        "skywars" -> when (skywarsMode) {
                            "SoloNormal" -> player.sendChatMessage("/play solo_normal")
                            "SoloInsane" -> player.sendChatMessage("/play solo_insane")
                        }

                        "bedwars" -> when (bedwarsMode) {
                            "Solo" -> player.sendChatMessage("/play bedwars_eight_one")
                            "Double" -> player.sendChatMessage("/play bedwars_eight_two")
                            "Trio" -> player.sendChatMessage("/play bedwars_four_three")
                            "Quad" -> player.sendChatMessage("/play bedwars_four_four")
                        }
                    }
                    delayTick = 0
                }
            }
        }
    }

    /**
     * Check whether player is in game or not
     */
    private fun playerInGame(): Boolean {
        val player = mc.thePlayer ?: return false

        return player.ticksExisted >= 20
                && (player.capabilities.isFlying
                || player.capabilities.allowFlying
                || player.capabilities.disableDamage)
    }


    val onRender2D = handler<Render2DEvent> { 
        if (!bedWarsHelp) return@handler

        val player = mc.thePlayer ?: return@handler
        val world = mc.theWorld ?: return@handler

        if (player.ticksExisted < 5) {
            stoneSwordList.clear()
            ironSwordList.clear()
            diamondSwordList.clear()
            fireBallList.clear()
            enderpearlList.clear()
            tntList.clear()
            obsidianList.clear()
            diamondArmorList.clear()
            invisibilityPotionList.clear()
        }
        for (entity in world.playerEntities) {
            if (entity.heldItem?.item == Items.stone_sword && stoneSword && !stoneSwordList.contains(entity.name)) {
                displayChatMessage("§F[§dBWH§F] ${entity.displayName.formattedText} has §l§8Stone Sword")
                stoneSwordList.add(entity.name)
                player.playSound("note.pling", 1.0f, 1.0f)
            }
            if (entity.heldItem?.item == Items.iron_sword && ironSword && !ironSwordList.contains(entity.name)) {
                displayChatMessage("§F[§dBWH§F] ${entity.displayName.formattedText} has §l§FIron Sword")
                ironSwordList.add(entity.name)
                player.playSound("note.pling", 1.0f, 1.0f)
            }
            if (entity.heldItem?.item == Items.diamond_sword && diamondSword && !diamondSwordList.contains(entity.name)) {
                displayChatMessage("§F[§dBWH§F] ${entity.displayName.formattedText} has §l§bDiamond Sword")
                diamondSwordList.add(entity.name)
                player.playSound("note.pling", 1.0f, 1.0f)
            }
            if (entity.heldItem?.item == Items.fire_charge && fireBallSword && !fireBallList.contains(entity.name)) {
                displayChatMessage("§F[§dBWH§F] ${entity.displayName.formattedText} has §l§6FireBall")
                fireBallList.add(entity.name)
                player.playSound("note.pling", 1.0f, 1.0f)
            }
            if (entity.heldItem?.item == Items.ender_pearl && enderPearl && !enderpearlList.contains(entity.name)) {
                displayChatMessage("§F[§dBWH§F] ${entity.displayName.formattedText} has §l§9Ender Pearl")
                enderpearlList.add(entity.name)
                player.playSound("note.pling", 1.0f, 1.0f)
            }
            if (entity.heldItem?.item == ItemBlock.getItemById(46) && tnt && !tntList.contains(entity.name)) {
                displayChatMessage("§F[§dBWH§F] ${entity.displayName.formattedText} has §l§4TNT Block")
                tntList.add(entity.name)
                player.playSound("note.pling", 1.0f, 1.0f)
            }
            if (entity.heldItem?.item == ItemBlock.getItemById(49) && obsidian && !obsidianList.contains(entity.name)) {
                displayChatMessage("§F[§dBWH§F] ${entity.displayName.formattedText} has §l§0Obsidian Block")
                obsidianList.add(entity.name)
                player.playSound("note.pling", 1.0f, 1.0f)
            }
            if (isWearingDiamondArmor(entity) && diamondArmor && !diamondArmorList.contains(entity.name)) {
                displayChatMessage("§F[§dBWH§F] ${entity.displayName.formattedText} has §l§bDiamond Armor")
                diamondArmorList.add(entity.name)
                player.playSound("note.pling", 1.0f, 1.0f)
            }
            if (entity.heldItem?.item == Potion.invisibility && invisibilityPotion && !invisibilityPotionList.contains(
                    entity.name
                )
            ) {
                displayChatMessage("§F[§dBWH§F] ${entity.displayName.formattedText} has §l§5Invisibility Potion")
                invisibilityPotionList.add(entity.name)
                player.playSound("note.pling", 1.0f, 1.0f)
            }
            if (entity.isDead) {
                stoneSwordList.remove(entity.name)
                ironSwordList.remove(entity.name)
                diamondSwordList.remove(entity.name)
                fireBallList.remove(entity.name)
                enderpearlList.remove(entity.name)
                tntList.remove(entity.name)
                obsidianList.remove(entity.name)
                diamondArmorList.remove(entity.name)
                invisibilityPotionList.remove(entity.name)
            }
        }
    }

    private fun isWearingDiamondArmor(player: EntityPlayer): Boolean {
        val armorInventory = player.inventory?.armorInventory ?: return false

        for (itemStack in armorInventory) {
            if (itemStack != null && (itemStack.item == Items.diamond_leggings || itemStack.item == Items.diamond_chestplate)) {
                return true
            }
        }

        return false
    }

    /**
     * HUD Tag
     */
    override val tag
        get() = mode
}
