package xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.mob;

import net.minecraft.entity.mob.WitchEntity;

import xyz.wagyourtail.jsmacros.client.api.helpers.entity.MobEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.item.ItemStackHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class WitchEntityHelper extends MobEntityHelper<WitchEntity> {

    public WitchEntityHelper(WitchEntity base) {
        super(base);
    }

    /**
     * @return {@code true} if this witch is drinking a potion, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isDrinkingPotion() {
        return base.isDrinking();
    }

    /**
     * @return the held potion item.
     *
     * @since 1.8.4
     */
    public ItemStackHelper getPotion() {
        return getMainHand();
    }

}
