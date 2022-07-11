package io.github.lukebemish.excavated_variants.forge.registry;

import com.google.common.base.Suppliers;
import io.github.lukebemish.excavated_variants.ExcavatedVariants;
import io.github.lukebemish.excavated_variants.forge.ExcavatedVariantsForge;
import io.github.lukebemish.excavated_variants.platform.Services;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.function.Supplier;

public class BlockAddedCallback {
    private static final Supplier<ModContainer> EV_CONTAINER = Suppliers.memoize(() -> ModList.get().getModContainerById(ExcavatedVariants.MOD_ID).orElseThrow());

    private static boolean isRegistering = false;

    public static void register() {
        if (ExcavatedVariants.hasLoaded() && !isRegistering) {
            isRegistering = true;
            ArrayList<ExcavatedVariants.RegistryFuture> toRemove = new ArrayList<>();
            for (ExcavatedVariants.RegistryFuture b : ExcavatedVariants.getBlockList()) {
                if (ExcavatedVariants.loadedBlockRLs.contains(b.ore.block_id.get(0)) &&
                        ExcavatedVariants.loadedBlockRLs.contains(b.stone.block_id)) {
                    ExcavatedVariants.registerBlockAndItem((rlr, bl) -> {
                        final ModContainer activeContainer = ModLoadingContext.get().getActiveContainer();
                        ModLoadingContext.get().setActiveContainer(EV_CONTAINER.get());
                        ForgeRegistries.BLOCKS.register(rlr, bl);
                        ModLoadingContext.get().setActiveContainer(activeContainer);
                    }, (rlr, it) -> {
                        ExcavatedVariantsForge.toRegister.register(rlr.getPath(), it);
                        return () -> Services.REGISTRY_UTIL.getItemById(rlr);
                    }, b);
                    toRemove.add(b);
                }
            }
            ExcavatedVariants.blockList.removeAll(toRemove);
            isRegistering = false;
        }
    }
}
