package pd.levels.traps.damagetrap;
import pd.actors.blobs.damageblobs.LightEffectDamage;
import pd.messages.InlineText;
public class LightDamage2Trap extends ElementalDamageTrap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(LightDamage2Trap.class)
			.t("name", "光伤大陷阱")
			.t("desc", "会释放大范围光属性伤害的陷阱。");
	}


 public LightDamage2Trap(){ super(WHITE, CROSSHAIR, LightEffectDamage.class, 2, 20); } }
