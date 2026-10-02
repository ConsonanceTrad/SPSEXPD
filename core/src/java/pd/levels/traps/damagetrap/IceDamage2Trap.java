package pd.levels.traps.damagetrap;
import pd.actors.blobs.damageblobs.IceEffectDamage;
import pd.messages.InlineText;
public class IceDamage2Trap extends ElementalDamageTrap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(IceDamage2Trap.class)
			.t("name", "冰伤大陷阱")
			.t("desc", "会释放大范围冰属性伤害的陷阱。");
	}


 public IceDamage2Trap(){ super(TEAL, CROSSHAIR, IceEffectDamage.class, 2, 20); } }
