/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.potions;

import java.util.List;

import pd.actors.hero.Hero;
import render.utils.math.Random;
import render.utils.serialize.Reflection;

/**
 * SPSEXPD: 紊乱魔药家族的公共基类（docs/alchemy-rework.md §6.2，裁决 #6）。
 *
 * <p>使用时从「粗制药水效果池」里随机抽一条执行，抽取在**使用时**进行（不是酿造时定死），
 * 同一垛内逐瓶独立。良性/恶性只决定抽取的池（正面池 / 有害池）。
 */
public abstract class AbstractConcoction extends Potion {

	{
		stackable = true;
		bones = false;
	}

	/** 该魔药可抽取的效果池（正面或有害）。 */
	protected abstract List<Class<? extends Potion>> pool();

	@Override
	public boolean isKnown() {
		return true;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public int value() {
		return 40 * quantity;
	}

	/** 掷出一条粗制药水效果（形态 CRUDE，裁决 #6）。 */
	protected Potion roll() {
		List<Class<? extends Potion>> pool = pool();
		if (pool == null || pool.isEmpty()) return null;
		Potion effect = Reflection.newInstance(pool.get(Random.Int(pool.size())));
		if (effect != null) {
			effect.form = PotionForm.CRUDE;
			//效果物品不显示也不参与图鉴/天赋统计
			effect.anonymize();
		}
		return effect;
	}

	@Override
	public void apply( Hero hero ) {
		Potion effect = roll();
		if (effect != null) effect.apply( hero );
	}

	@Override
	public void shatter( int cell ) {
		Potion effect = roll();
		if (effect != null) {
			effect.shatter( cell );
		} else {
			super.shatter( cell );
		}
	}
}
