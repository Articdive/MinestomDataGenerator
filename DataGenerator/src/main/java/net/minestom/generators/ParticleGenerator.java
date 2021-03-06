package net.minestom.generators;

import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minestom.generators.common.DataGeneratorCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Set;

public final class ParticleGenerator extends DataGeneratorCommon<ParticleType<?>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParticleGenerator.class);

    @Override
    public void generateNames() {
        for (Field declaredField : ParticleTypes.class.getDeclaredFields()) {
            if (!ParticleType.class.isAssignableFrom(declaredField.getType())) {
                continue;
            }
            try {
                ParticleType<?> pt = (ParticleType<?>) declaredField.get(null);
                names.put(pt, declaredField.getName());
            } catch (IllegalAccessException e) {
                LOGGER.error("Failed to map particle naming system", e);
                return;
            }
        }
    }

    @Override
    public JsonObject generate() {
        Set<ResourceLocation> particleRLs = Registry.PARTICLE_TYPE.keySet();
        JsonObject particles = new JsonObject();

        for (ResourceLocation particleRL : particleRLs) {
            ParticleType<?> pt = Registry.PARTICLE_TYPE.get(particleRL);
            if (pt == null) {
                continue;
            }
            JsonObject particle = new JsonObject();

            particle.addProperty("id", Registry.PARTICLE_TYPE.getId(pt));
            particle.addProperty("mojangName", names.get(pt));
            particles.add(particleRL.toString(), particle);
        }
        return particles;
    }
}
