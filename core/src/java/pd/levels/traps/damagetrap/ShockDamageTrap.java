package pd.levels.traps.damagetrap;
import pd.actors.blobs.damageblobs.ShockEffectDamage;
import pd.messages.InlineText;
public class ShockDamageTrap extends ElementalDamageTrap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(ShockDamageTrap.class)
			.t("name", "雷伤陷阱")
			.t("desc", "会释放雷属性伤害的陷阱。");
	}
 public ShockDamageTrap(){ super(YELLOW, LARGE_DOT, ShockEffectDamage.class, 1, 10); } }
