package retranslator.world.blocks.power

import arc.Core
import arc.func.Floatp
import arc.func.Prov
import arc.graphics.g2d.Lines
import arc.math.Mathf
import arc.math.geom.Geometry
import mindustry.Vars
import mindustry.Vars.tilesize
import mindustry.Vars.world
import mindustry.core.UI
import mindustry.gen.Building
import mindustry.graphics.Pal
import mindustry.ui.Bar
import mindustry.world.blocks.power.PowerBlock
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit

open class ReTranslator(name: String) : PowerBlock(name) {

    var range = 8f
    var power = 50f
    
    init {
        consumesPower = false
        outputsPower = false
        canOverdrive = false
        drawDisabled = false
    }

    override fun setBars() {
        super.setBars()
        bars.add("power") { entity: Building ->
            Bar(
                {
                    Core.bundle.format(
                        "bar.powerbalance",
                        (if (entity.power.graph.powerBalance >= 0) "+" else "") + UI.formatAmount((entity.power.graph.powerBalance * 60).toInt())
                    )
                },
                { Pal.powerBar }
            ) { Mathf.clamp(entity.power.graph.lastPowerProduced / entity.power.graph.lastPowerNeeded) }
        }
        bars.add("batteries") { entity: Building ->
            Bar(
                {
                    Core.bundle.format(
                        "bar.powerstored",
                        UI.formatAmount(entity.power.graph.lastPowerStored.toInt()), UI.formatAmount(
                            entity.power.graph.lastCapacity
                                .toInt()
                        )
                    )
                },
                { Pal.powerBar }
            ) { Mathf.clamp(entity.power.graph.lastPowerStored / entity.power.graph.lastCapacity) }
        }
    }

    override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
        val tile = world.tile(x, y) ?: return

        Lines.stroke(2f, Pal.placing)

        Lines.dashLine(
            x * tilesize + Geometry.d4[rotation].x * (tilesize / 2f + 2),
            y * tilesize + Geometry.d4[rotation].y * (tilesize / 2f + 2),
            x * tilesize + Geometry.d4[rotation].x * (range + 0.5f) * tilesize,
            y * tilesize + Geometry.d4[rotation].y * (range + 0.5f) * tilesize,
            range.toInt()
        )
    }

    override fun setStats() {
        super.setStats()
        stats.add(Stat.powerRange, range, StatUnit.blocks)
    }
}