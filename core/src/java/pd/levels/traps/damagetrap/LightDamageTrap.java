package pd.levels.traps.damagetrap;
import pd.actors.blobs.damageblobs.LightEffectDamage;
import pd.messages.InlineText;
public class LightDamageTrap extends ElementalDamageTrap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(LightDamageTrap.class)
			.t("name", "光伤陷阱")
			.t("desc", "会释放光属性伤害的陷阱。");
	}
 public LightDamageTrap(){ super(WHITE, LARGE_DOT, LightEffectDamage.class, 1, 10); } }
