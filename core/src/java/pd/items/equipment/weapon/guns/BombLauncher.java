package pd.items.equipment.weapon.guns;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.atlas.items.EquipmentEquipWeaponGunDict;
import pd.items.Item;
import pd.items.equipment.bombs.Bomb;
import pd.messages.InlineText;
import render.utils.serialize.Bundle;

/**
 * SPSEXPD: 特殊枪械·爆弹发射器——以炸弹为弹药（弹匣容量 1）。
 * 一次装填消耗背包里的一个炸弹，命中时在目标位置引爆该炸弹；
 * 装填哪种炸弹就会产生哪种炸弹的效果。
 */
public class BombLauncher extends GunWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(BombLauncher.class)
			.t("name", "爆弹发射器")
			.t("desc", "把炸弹整个发射出去的重型武器，弹匣只装得下一发。\n\n命中时会在目标位置引爆装填的炸弹——装填什么样的炸弹，就有什么样的效果。")
			.t("no_bomb", "你没有可以装填的炸弹。")
			.t("loaded_bomb", "当前装填：%s");
	}

	private static final String BOMB = "bomb";

	/** SPSEXPD: 当前装填的炸弹（实例）。 */
	private Bomb loaded;

	{ image = EquipmentEquipWeaponGunDict.BOMB_LAUNCHER; }
	public BombLauncher() {
		super(GunArchetype.CUSTOM);
		fullCharge = 1;      //SPSEXPD: 弹匣容量 1
		shotsPerFire = 1;
		perfectSTR = 16;
		effectiveRange = 5;
		noiseRadius = 8;     //SPSEXPD: 发射爆炸物，枪声传播更远
		baseReloadTime = 2f;
	}

	/** SPSEXPD: 装填耗材 = 背包里的一个炸弹（保留其实例以决定命中效果）。 */
	@Override
	protected boolean consumeReload(Hero hero) {
		if (hero == null) return false;
		Bomb bomb = findBomb(hero);
		if (bomb == null) return false;
		if (pd.actors.buffs.EndlessAmmo.isActive(hero)) {
			loaded = bomb;
			return true;
		}
		Item consumed = bomb.detach(hero.belongings.backpack);
		if (!(consumed instanceof Bomb)) return false;
		loaded = (Bomb)consumed;
		return true;
	}

	private Bomb findBomb(Hero hero) {
		for (Item item : hero.belongings.backpack.items) {
			if (item instanceof Bomb) return (Bomb)item;
		}
		return null;
	}

	@Override protected String reloadHintKey() { return "no_bomb"; }

	public Bomb loadedBomb() { return loaded; }

	/** SPSEXPD: 命中时在目标位置引爆装填的炸弹。 */
	@Override
	protected void onBulletHit(Char attacker, Char defender, int damage) {
		if (loaded == null || defender == null) return;
		loaded.explode(defender.pos);
	}

	@Override
	public String info() {
		String info = super.info();
		if (loaded != null) info += "\n\n" + pd.messages.Messages.get(BombLauncher.class, "loaded_bomb", loaded.name());
		return info;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		if (loaded != null) bundle.put(BOMB, loaded);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		Object restored = bundle.get(BOMB);
		loaded = restored instanceof Bomb ? (Bomb)restored : null;
	}
}
