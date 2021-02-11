package retranslator.content

import mindustry.ctype.ContentList
import mindustry.world.Block
import retranslator.world.blocks.power.ReTranslator

class ReBlocks : ContentList {
    override fun load() {
        retranslator = object : ReTranslator("retranslator") {

        }
    }

    companion object {
        var retranslator: Block? = null
    }
}