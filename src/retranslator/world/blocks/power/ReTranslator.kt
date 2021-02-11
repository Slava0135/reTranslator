package retranslator.world.blocks.power

import arc.Core
import arc.Core.atlas
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.math.geom.Geometry
import arc.math.geom.Position
import mindustry.Vars.tilesize
import mindustry.Vars.world
import mindustry.core.Renderer
import mindustry.core.UI
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.ui.Bar
import mindustry.world.blocks.power.PowerDistributor
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit

open class ReTranslator(name: String) : PowerDistributor(name) {

    var range = 8
    var laserPower = 50f

    var laser: TextureRegion? = null
    var laserEnd: TextureRegion? = null

    var laserColor1 = Color.white
    var laserColor2 = Pal.powerLight
    
    init {
        consumesPower = false
        outputsPower = false
        canOverdrive = false
        drawDisabled = false
        update = true
        rotate = true
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

        var target: Position? = null
        var lastAmount = 0f

        override fun updateTile() {
            lastAmount = transfer()
        }

        private fun transfer(): Float {
            var x = tileX()
            var y = tileY()
            target = null
            for (i in 0..range) {
                x += Geometry.d4[rotation].x
                y += Geometry.d4[rotation].y
                world.tile(x, y)?.let {
                    val build = it.build
                    if (build != null && build.block.hasPower) {
                        target = it
                        val amount = Mathf.clamp(laserPower, 0f, this.power.graph.batteryStored)
                        Mathf.clamp(amount, 0f, build.power.graph.totalBatteryCapacity - build.power.graph.batteryStored)
                        build.power.graph.transferPower(amount)
                        power.graph.transferPower(-amount)
                        return amount
                    }
                }
                if (world.solid(x, y)) return 0f
            }
            return 0f
        }

        override fun draw() {
            super.draw()

            Draw.z(Layer.power)
            Draw.color(laserColor1, laserColor2, (1f - lastAmount / laserPower) * 0.86f + Mathf.absin(3f, 0.1f))
            Draw.alpha(Renderer.laserOpacity)

            target?.let {
                val g = Geometry.d4[rotation]
                val t = tilesize / 2
                Drawf.laser(team, laser, laserEnd, x + g.x * t, y + g.y * t, it.x - g.x * t, it.y - g.y * t, 0.5f)
            }
        }

        override fun drawSelect() {
            drawPlace(tileX(), tileY(), rotation, true)
        }
    }
}