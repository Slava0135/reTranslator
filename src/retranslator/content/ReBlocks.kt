package retranslator.content

import mindustry.content.Items
import mindustry.ctype.ContentList
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.Block
import retranslator.world.blocks.power.ReTranslator

class ReBlocks : ContentList {
    override fun load() {
        retranslator = object : ReTranslator("retranslator") {
            init {
                requirements(Category.power, ItemStack.with(Items.copper, 1, Items.lead, 3))
                consumes.powerBuffered(500f)
                alwaysUnlocked = true
            }
        }
    }

    companion object {
        var retranslator: Block? = null
    }
}