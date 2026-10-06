package pd.actors.mobs.npcs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.items.Waterskin;
import pd.items.quest.Mushroom;
import pd.items.specific.sellitem.SellMushroom;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.TinkererSprite;
import pd.windows.WndQuest;
import pd.windows.WndTinkerer;
import render.noosa.Game;
import pd.messages.InlineText;

public class Tinkerer1 extends NPC {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Tinkerer1.class)
			.t("name", "露珠研究者")
			.t("desc", "一名来自多利亚的研究者。他似乎在等待什么东西上门。")
			.t("tell1", "我想找一个特殊的蘑菇做研究，但是我不敢继续往下走。")
			.t("tell2", "你需要先带来露珠瓶，我才能改进它。");
	}




	{
		spriteClass = TinkererSprite.class;
		properties.add(Property.IMMOVABLE);
		properties.add(Property.HUMAN);
	}

	@Override
	public int defenseSkill(Char enemy) {
		return 1000;
	}

	@Override
	public void damage(int dmg, Object src) {
	}

	@Override
	public boolean add(Buff buff) {
		return false;
	}

	@Override
	public boolean reset() {
		return true;
	}

	@Override
	public SellMushroom SupercreateLoot() {
		return new SellMushroom();
	}

	@Override
	public boolean interact(Char ch) {
		sprite.turnTo(pos, Dungeon.hero.pos);
		if (ch != Dungeon.hero) return true;

		Mushroom mushroom = Dungeon.hero.belongings.getItem(Mushroom.class);
		Waterskin waterskin = Dungeon.hero.belongings.getItem(Waterskin.class);
		Game.runOnRenderThread(() -> {
			if (mushroom != null && waterskin != null) {
				//SPSEXPD: 带着任务蘑菇来就直接完成强化，不再弹选择框
				WndTinkerer.performUpgrade(Tinkerer1.this, Dungeon.hero, waterskin, mushroom);
			} else {
				GameScene.show(new WndQuest(Tinkerer1.this,
						Messages.get(Tinkerer1.this, waterskin == null ? "tell2" : "tell1")));
			}
		});
		return true;
	}
}
