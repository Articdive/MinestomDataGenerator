package net.minestom.generators;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minestom.generators.common.DataGenerator_1_16_5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Set;

public final class MobEffectGenerator_1_16_5 extends DataGenerator_1_16_5<MobEffect> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MobEffectGenerator_1_16_5.class);

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
    public JsonArray generate() {
        Set<ResourceLocation> effectRLs = Registry.MOB_EFFECT.keySet();
        JsonArray effects = new JsonArray();

        for (ResourceLocation effectRL : effectRLs) {
            MobEffect me = Registry.MOB_EFFECT.get(effectRL);

            JsonObject effect = new JsonObject();
            // Null safety check.
            if (me == null) {
                continue;
            }
            effect.addProperty("id", effectRL.toString());
            effect.addProperty("numericalID", Registry.MOB_EFFECT.getId(me));
            effect.addProperty("name", names.get(me));
            // effect.addProperty("langId", me.getDescriptionId());
            effect.addProperty("color", me.getColor());
            effect.addProperty("instantaneous", me.isInstantenous());

            effects.add(effect);
        }
        return effects;
    }
}
