package retranslator

import mindustry.content.Blocks
import mindustry.content.Items
import mindustry.mod.*
import mindustry.type.ItemStack
import retranslator.content.ReBlocks

class ReTranslator : Mod() {
    override fun loadContent() {
        ReBlocks().load()

        Blocks.powerNode.requirements = Blocks.powerNode.requirements.plus(ItemStack(Items.surgeAlloy, 1))
        Blocks.powerNodeLarge.requirements = Blocks.powerNodeLarge.requirements.plus(ItemStack(Items.surgeAlloy, 3))
    }
}
