package xyz.wagyourtail.jsmacros.access;

import net.minecraft.text.ClickEvent;

public class CustomClickEvent
#if MCV >= 12105
    implements ClickEvent
#else
    extends ClickEvent
#endif
{
    private final Runnable event;

    public CustomClickEvent(Runnable event) {
        #if MCV < 12105
        super(null, null);
        #endif
        this.event = event;
    }

    @Override
    public int hashCode() {
        return event.hashCode();
    }

    public Runnable event() {
        return event;
    }

    @Override
    public Action getAction() {
        //TODO: switch to enum extension with mixin 9.0 or whenever Mumfrey gets around to it
        // https://github.com/SpongePowered/Mixin/issues/387
        return null;
    }

}
