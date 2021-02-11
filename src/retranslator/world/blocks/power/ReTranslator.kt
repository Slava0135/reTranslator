package retranslator.world.blocks.power

import arc.Core
import arc.Core.atlas
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.math.geom.Geometry
import mindustry.Vars.tilesize
import mindustry.Vars.world
import mindustry.core.UI
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.graphics.Pal
import mindustry.ui.Bar
import mindustry.world.blocks.power.PowerBlock
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit

open class ReTranslator(name: String) : PowerBlock(name) {

    var range = 8
    var power = 50f

    var laser: TextureRegion? = null
    var laserEnd: TextureRegion? = null
    
    init {
        consumesPower = false
        outputsPower = false
        canOverdrive = false
        drawDisabled = false
        update = true
    }

    override fun load() {
        super.load()
        laser = atlas.find("laser")
        laserEnd = atlas.find("laser-end")
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
        Lines.stroke(2f, Pal.placing)
        Lines.dashLine(
            x * tilesize + Geometry.d4[rotation].x * (tilesize / 2f + 2),
            y * tilesize + Geometry.d4[rotation].y * (tilesize / 2f + 2),
            x * tilesize + Geometry.d4[rotation].x * (range + 0.5f) * tilesize,
            y * tilesize + Geometry.d4[rotation].y * (range + 0.5f) * tilesize,
            range
        )
    }

    override fun setStats() {
        super.setStats()
        stats.add(Stat.powerRange, range.toFloat(), StatUnit.blocks)
    }

    inner class ReTranslatorBuild : Building() {

        var target: Building? = null

        override fun updateTile() {
            var x = tileX()
            var y = tileY()
            target = null
            for (i in 0..range) {
                x += Geometry.d4[rotation].x
                y += Geometry.d4[rotation].y
                world.tile(x, y)?.build?.let {
                    if (it.block.hasPower) {
                        target = it
                        return
                    }
                }
                if (world.solid(x, y)) return
            }
        }

        override fun draw() {
            super.draw()
            target?.let {
                val g = Geometry.d4[rotation]
                Drawf.laser(team, laser, laserEnd, x + 0.5f * g.x, y + 0.5f * g.y, it.x - 0.5f * g.x, it.y - 0.5f * g.y, 0.25f)
            }
        }

        override fun drawSelect() {
            drawPlace(tileX(), tileY(), rotation, true)
        }
    }
}