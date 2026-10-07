/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.artifacts;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemSprite;
import pd.utils.GLog;
import pd.windows.WndOptions;
import render.noosa.audio.Sample;

import java.util.ArrayList;
import pd.messages.InlineText;

/**
 * SPSEXPD: 移植自 Darkest PD 0.7.2 的神器「撒旦之心」（HeartOfSatan），用来取代圣杯。
 *
 * <p>主动「血祭」会刺伤自己（伤害随神器等级平方增长），活着就能让心脏升级；
 * 满级后心脏让佩戴者的生命回复能突破血肉极限（额外生命上限 HT/2，由 Hero.HT 读取）。</p>
 */
public class HeartOfSatan extends Artifact {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(HeartOfSatan.class)
			.t("name", "撒旦之心")
			.t("ac_prick", "血祭")
			.t("yes", "确定")
			.t("no", "算了")
			.t("prick_warn", "每一次血祭都需要更多生命能量，稍有不慎就能轻易要了你的命。\n\n确定要继续吗？")
			.t("onprick", "你刺破指尖，把血滴在心脏上。")
			.t("ondeath", "心脏把你的生命精华吸干了……")
			.t("levelup", "心脏的跳动更有力了！")
			.t("desc", "脱离了躯体的硕大心脏仍在微微颤动，如同鲜活一般。没人愿意相信恶魔会有心脏。")
			.t("desc_hint", "心脏似乎在逐渐恢复活力，你能隐约感到它在为你输送生命能量——但你还得继续割伤自己。")
			.t("desc_lvlmax", "心脏已经彻底鲜活：它让你的自然回复能够突破血肉的极限。")
			.t("desc_cursed", "被诅咒的心脏紧紧贴着你，抑制你回复生命，甚至在微微吮吸。");
	}

	public static final String AC_PRICK = "PRICK";
	/** 血祭消耗的时间。 */
	public static final float PRICK_TIME = 3f;

	{
		image = SpecificPlaceHolderDict.ARTIFACT_HOLDER_0;
		levelCap = 9;
		charge = 0;
		partialCharge = 0;
		chargeCap = 10;
		defaultAction = AC_PRICK;
	}

	/** 每次血祭造成的伤害：等级² × 3（0 级 0 点，9 级 243 点）。 */
	public int prickValue() {
		return level() * 3 * level();
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero) && level() < levelCap()) actions.add(AC_PRICK);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);

		if (!AC_PRICK.equals(action)) return;

		if (!isEquipped(hero)) {
			GLog.i(Messages.get(Artifact.class, "need_to_equip"));
			return;
		}
		if (level() >= levelCap()) return;

		//伤害超过当前生命四分之三时先确认，避免手滑自杀
		if (prickValue() > hero.HP * 3 / 4) {
			GameScene.show(new WndOptions(new ItemSprite(this), Messages.titleCase(name()),
					Messages.get(this, "prick_warn"),
					Messages.get(this, "yes"), Messages.get(this, "no")) {
				@Override
				protected void onSelect(int index) {
					if (index == 0) prick(hero);
				}
			});
			return;
		}
		prick(hero);
	}

	/** 血祭：扣自己的血，活着就让心脏升级。 */
	public void prick(Hero hero) {
		if (hero == null) return;

		int damage = Math.max(1, prickValue());
		hero.damage(damage, this);
		if (hero.sprite != null) {
			hero.sprite.operate(hero.pos);
			if (damage > 10) Sample.INSTANCE.play(pd.Assets.Sounds.CURSED);
		}
		GLog.w(Messages.get(this, "onprick"));
		hero.spend(PRICK_TIME);
		hero.busy();

		if (!hero.isAlive()) {
			GLog.n(Messages.get(this, "ondeath"));
			Dungeon.fail(getClass());
			return;
		}

		upgrade();
		GLog.p(Messages.get(this, "levelup"));
		updateQuickslot();
	}

	@Override
	public String desc() {
		String desc = super.desc();
		Hero hero = Dungeon.hero;
		if (hero == null || !isEquipped(hero)) return desc;

		if (cursed) return desc + "\n\n" + Messages.get(this, "desc_cursed");
		if (level() >= levelCap()) return desc + "\n\n" + Messages.get(this, "desc_lvlmax");
		if (level() > 0) return desc + "\n\n" + Messages.get(this, "desc_hint");
		return desc;
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new Regeneration();
	}

	/** 装备标记 + 满级时的额外生命上限。 */
	public class Regeneration extends ArtifactBuff {
		/** 满级后提供 HT/2 的额外生命上限（Hero 计算 HT 时读取）。 */
		public int extraCap() {
			if (target == null || itemLevel() < levelCap()) return 0;
			return target.HT / 2;
		}

		@Override
		public boolean act() {
			spend(TICK);
			return true;
		}
	}
}
