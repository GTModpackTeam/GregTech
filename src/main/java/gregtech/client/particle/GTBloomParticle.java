package gregtech.client.particle;

import gregtech.client.renderer.IRenderSetup;
import gregtech.client.shader.postprocessing.BloomType;
import gregtech.client.utils.BloomEffectUtil;
import gregtech.client.utils.IBloomEffect;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class GTBloomParticle extends GTParticle implements IBloomEffect {

    private final BloomEffectUtil.BloomRenderTicket ticket;

    public GTBloomParticle(double posX, double posY, double posZ) {
        super(posX, posY, posZ);

        this.ticket = BloomEffectUtil.registerBloomRender(getBloomRenderSetup(), getBloomType(), this);
    }

    @Nullable
    protected abstract IRenderSetup getBloomRenderSetup();

    @Nonnull
    protected abstract BloomType getBloomType();

    @Override
    protected void onExpired() {
        this.ticket.invalidate();
    }
}
