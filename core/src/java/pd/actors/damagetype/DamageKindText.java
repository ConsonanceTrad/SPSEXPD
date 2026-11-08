/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.damagetype;

import pd.items.equipment.weapon.Weapon;
import pd.messages.InlineText;
import pd.messages.Messages;

/**
 * SPSEXPD: 伤害类型的展示文本层（伤害类型体系定稿）——
 * 武器 info 的「伤害类型」行与类型/描述文本；简体内联注册于本类静态块，
 * 英文模板在 core/src/assets/messages/misc/en/damage_kind.properties（AGENTS 约定）。
 */
public final class DamageKindText {

	public DamageKindText() { }

	static {
		InlineText.of(DamageKindText.class)
			.t("weapon_line", "伤害类型：%s")
			.t("kind_pierce", "刺击")
			.t("kind_blunt", "钝击")
			.t("kind_slash", "切割")
			.t("kind_impact", "冲击")
			.t("kind_crush", "挤压")
			.t("kind_curse", "诅咒")
			.t("kind_energy", "能量")
			.t("kind_element_fire", "元素·火焰")
			.t("kind_element_ice", "元素·冰冻")
			.t("kind_element_shock", "元素·雷电")
			.t("kind_element_light", "元素·神圣")
			.t("kind_element_poison", "元素·毒")
			.t("kind_element_acid", "元素·酸液")
			.t("kind_heal", "治疗")
			.t("kind_interfere", "干扰")
			.t("kind_hunger", "饥饿")
			.t("kind_disease", "疾病")
			.t("kind_backlash", "反噬")
			.t("kind_pure", "纯粹")
			.t("desc_pierce", "一般来自轻便的近战兵器和破片或穿甲枪械，伤害较低但可以对重甲敌人造成足额伤害。")
			.t("desc_blunt", "一般来自冲击枪械或重锤这类近战武器，攻速较慢，但对所有物理防护类型的敌人都造成足额伤害。")
			.t("desc_slash", "一般来自轻巧的单手兵器或特殊枪械，对无甲敌人造成大量伤害和流血，对有甲敌人的属性略差。")
			.t("desc_impact", "一般来自炸弹、扇、乐器和一部分音波枪械，伤害较低且随距离衰减，但会无视部分护甲。")
			.t("desc_crush", "超重型武器的碾压伤害，攻速极慢，且无视大部分护甲的防护性能。")
			.t("desc_curse", "部分仪式或部分法术的伤害类型。")
			.t("desc_energy", "无属性的法术伤害。")
			.t("desc_pure", "直接造成血量损失，无视一切防御。")
			.t("desc_heal", "对不死族而言，治疗力量反而会转化为伤害。")
			.t("desc_interfere", "数据、机械类生物受到的数据侵入与干扰伤害。")
			.t("desc_hunger", "饥饿造成的生命机能损耗。")
			.t("desc_disease", "流血等恶性状态的后果伤害。")
			.t("desc_backlash", "不合格的仪式反噬对施术者自身的伤害。")
			.t("desc_element", "元素类伤害，效果随元素系而异。");
	}

	/** 类型显示名（zh 内联 / en 模板） */
	public static String labelOf( DamageType token ) {
		if (token == null) return Messages.get(DamageKindText.class, "kind_none");
		String n = token.getClass().getSimpleName();
		switch (n) {
			case "Puncture": return Messages.get(DamageKindText.class, "kind_pierce");
			case "Blunt":    return Messages.get(DamageKindText.class, "kind_blunt");
			case "Slash":    return Messages.get(DamageKindText.class, "kind_slash");
			case "Impact":   return Messages.get(DamageKindText.class, "kind_impact");
			case "Crush":    return Messages.get(DamageKindText.class, "kind_crush");
			case "Curse":    return Messages.get(DamageKindText.class, "kind_curse");
			case "Energy": case "Dark": case "Chaos":
				return Messages.get(DamageKindText.class, "kind_energy");
			case "Fire":     return Messages.get(DamageKindText.class, "kind_element_fire");
			case "Ice":      return Messages.get(DamageKindText.class, "kind_element_ice");
			case "Shock":    return Messages.get(DamageKindText.class, "kind_element_shock");
			case "Light":    return Messages.get(DamageKindText.class, "kind_element_light");
			case "Poison":   return Messages.get(DamageKindText.class, "kind_element_poison");
			case "Acid":     return Messages.get(DamageKindText.class, "kind_element_acid");
			case "Heal":     return Messages.get(DamageKindText.class, "kind_heal");
			case "Interfere":return Messages.get(DamageKindText.class, "kind_interfere");
			case "Hunger":   return Messages.get(DamageKindText.class, "kind_hunger");
			case "Disease":  return Messages.get(DamageKindText.class, "kind_disease");
			case "Backlash": return Messages.get(DamageKindText.class, "kind_backlash");
			default:         return Messages.get(DamageKindText.class, "kind_none");
		}
	}

	/** 类型短描述（zh 内联 / en 模板；未知类型返回空串） */
	public static String descOf( DamageType token ) {
		if (token == null) return "";
		String n = token.getClass().getSimpleName();
		switch (n) {
			case "Puncture": return "desc_pierce";
			case "Blunt": case "Earth":   return "desc_blunt";
			case "Slash":    return "desc_slash";
			case "Impact": case "Wind":    return "desc_impact";
			case "Crush":    return "desc_crush";
			case "Curse":    return "desc_curse";
			case "Energy": case "Dark": case "Chaos": return "desc_energy";
			case "Fire": case "Ice": case "Shock": case "Light":
			case "Poison": case "Acid":     return "desc_element";
			case "Heal":     return "desc_heal";
			case "Interfere":return "desc_interfere";
			case "Hunger":   return "desc_hunger";
			case "Disease":  return "desc_disease";
			case "Backlash": return "desc_backlash";
			default:         return "";
		}
	}

	/** 武器 info 的「伤害类型」行；未收录武器（无显式令牌）返回空串、不加行 */
	public static String weaponKindInfo( Weapon w ) {
		DamageType token = DamageKind.weaponTokenOf(w);
		if (token == null) return "";
		String label = labelOf(token);
		String descKey = descOf(token);
		String desc = descKey.equals("") ? "" : " " + Messages.get(DamageKindText.class, descKey);
		return Messages.get(DamageKindText.class, "weapon_line", label) + desc;
	}
}
