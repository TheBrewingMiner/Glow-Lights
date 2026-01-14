package net.thebrewingminer.glowlights.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class GlowSmokeParticle extends TextureSheetParticle {

    // Mimics properties of campfire smoke particles, but with glow particle textures and sizes.
    GlowSmokeParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, boolean isSignal) {
        super(level, x, y, z);
        this.friction = 0.96F;
        this.quadSize *= 0.75F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.hasPhysics = true;

        if (isSignal) {
            this.lifetime = this.random.nextInt(50) + 210;
        } else {
            this.lifetime = this.random.nextInt(50) + 80;
        }

        // Randomly set color of particle to the bright green of glow squid glow particles,
        // or to a plain white color.
        if (level.random.nextBoolean()){
            this.setColor(0.6F, 1.0F, 0.8F);
        }

        this.gravity = 3.0E-6F;
        this.xd = xSpeed;
        this.yd = ySpeed + (double)(this.random.nextFloat() / 500.0F);
        this.zd = zSpeed;
    }

    public void tick(){
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ < this.lifetime && this.alpha > 0.0F){
            this.xd += this.random.nextFloat() / 5000.0F * (float)(this.random.nextBoolean() ? 1 : -1);
            this.zd += this.random.nextFloat() / 5000.0F * (float)(this.random.nextBoolean() ? 1 : -1);
            this.yd -= this.gravity;
            this.move(this.xd, this.yd, this.zd);
            if (this.age >= this.lifetime - 60 && this.alpha > 0.01F) {
                this.alpha -= 0.015F;
            }
        } else {
            this.remove();
        }
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class GlowSignalProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public GlowSignalProvider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            GlowSmokeParticle signalGlowSmoke = new GlowSmokeParticle(level, x, y, z, xSpeed, ySpeed, xSpeed, true);

            signalGlowSmoke.setAlpha(0.95F);
            signalGlowSmoke.pickSprite(this.sprites);
            return signalGlowSmoke;
        }
    }

    public static class CosyGlowProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public CosyGlowProvider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            GlowSmokeParticle cozyGlowSmoke = new GlowSmokeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, false);

            cozyGlowSmoke.setAlpha(0.9F);
            cozyGlowSmoke.pickSprite(this.sprites);
            return cozyGlowSmoke;
        }
    }
}