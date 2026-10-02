package pd.levels.traps.damagetrap;
import pd.actors.blobs.damageblobs.DarkEffectDamage;
import pd.messages.InlineText;
public class DarkDamage2Trap extends ElementalDamageTrap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(DarkDamage2Trap.class)
			.t("name", "暗伤大陷阱")
			.t("desc", "会释放大范围暗属性伤害的陷阱。");
	}
 public DarkDamage2Trap(){ super(VIOLET, CROSSHAIR, DarkEffectDamage.class, 2, 20); } }
