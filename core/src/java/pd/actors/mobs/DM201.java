/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.CorrosiveGas;
import pd.actors.buffs.Corruption;
import pd.items.quest.MetalShard;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import pd.sprites.DM201Sprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class DM201 extends DM200 {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(DM201.class)
			.t("name", "DM-201")
			.t("vent", "DM-201投出了一枚酸蚀榴弹！")
			.t("desc", "矮人曾做过一些围绕DM-200的机动性缺陷展开的实验，并据此设计制造了一批完全固定的DM系列机器。DM-201是经过改装的DM-200，其作为哨戒炮塔完全没有移动能力。作为交换，DM-201们的坚固程度与攻击能力都得到了显著的提升。\n\n由于DM-201并没有用于排放废气的引擎，矮人给它们装备了酸蚀气体榴弹！不过，DM-201对榴弹的使用很谨慎，只有在受到来自远处的攻击时才会向敌人投掷榴弹。");
	}




	{
		spriteClass = DM201Sprite.class;

		HP = HT = 120;

		properties.add(Property.IMMOVABLE);

		HUNTING = new Hunting();
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 15, 25 );
	}

	private boolean threatened = false;

	@Override
	public void damage(int dmg, Object src) {
		if (!(src instanceof Corruption)) {
			if ((src instanceof Char && !Dungeon.level.adjacent(pos, ((Char) src).pos))
					|| enemy == null || !Dungeon.level.adjacent(pos, enemy.pos)) {
				threatened = true;
			}
		}
		super.damage(dmg, src);
	}

	public void onZapComplete(){
		zap();
		next();
	}

	private void zap( ){
		threatened = false;
		spend(TICK);

		GameScene.add(Blob.seed(enemy.pos, 15, CorrosiveGas.class).setStrength(8));
		for (int i : PathFinder.NEIGHBOURS8){
			if (!Dungeon.level.solid[enemy.pos+i]) {
				GameScene.add(Blob.seed(enemy.pos + i, 5, CorrosiveGas.class).setStrength(8));
			}
		}

	}

	@Override
	protected boolean canVent(int target) {
		return false;
	}

	@Override
	protected boolean getCloser(int target) {
		return false;
	}

	@Override
	protected boolean getFurther(int target) {
		return false;
	}

	@Override
	public void rollToDropLoot() {
		if (Dungeon.hero.lvl > maxLvl + 2) return;

		super.rollToDropLoot();

		int ofs;
		do {
			ofs = PathFinder.NEIGHBOURS8[Random.Int(8)];
		} while (Dungeon.level.solid[pos + ofs] && !Dungeon.level.passable[pos + ofs]);
		Dungeon.level.drop( new MetalShard(), pos + ofs ).sprite.drop( pos );
	}

	private class Hunting extends Mob.Hunting {

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {

			if (threatened && enemyInFOV){
				if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
					sprite.zap( enemy.pos );
					return false;
				} else {
					zap();
					return true;
				}
			} else {
				return super.act( enemyInFOV, justAlerted );
			}

		}

	}

}
