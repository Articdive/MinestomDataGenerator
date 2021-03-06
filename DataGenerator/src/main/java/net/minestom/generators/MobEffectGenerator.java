package net.minestom.generators;

import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minestom.generators.common.DataGeneratorCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Set;

public final class MobEffectGenerator extends DataGeneratorCommon<MobEffect> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MobEffectGenerator.class);

    @Override
    public void generateNames() {
        for (Field declaredField : MobEffects.class.getDeclaredFields()) {
            if (!MobEffect.class.isAssignableFrom(declaredField.getType())) {
                continue;
            }
            try {
                MobEffect me = (MobEffect) declaredField.get(null);
                names.put(me, declaredField.getName());
            } catch (IllegalAccessException e) {
                LOGGER.error("Failed to map effect naming system.", e);
                return;
            }
        }
    }

    @Override
    public JsonObject generate() {
        Set<ResourceLocation> effectRLs = Registry.MOB_EFFECT.keySet();
        JsonObject effects = new JsonObject();

        for (ResourceLocation effectRL : effectRLs) {
            MobEffect me = Registry.MOB_EFFECT.get(effectRL);

            JsonObject effect = new JsonObject();
            // Null safety check.
            if (me == null) {
                continue;
            }
            effect.addProperty("id", Registry.MOB_EFFECT.getId(me));
            effect.addProperty("mojangName", names.get(me));
            effect.addProperty("translationKey", me.getDescriptionId());
            effect.addProperty("color", me.getColor());
            effect.addProperty("instantaneous", me.isInstantenous());

            effects.add(effectRL.toString(), effect);
        }
        return effects;
    }
}
