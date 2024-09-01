package io.github.meatwo310.compressed_copper.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.meatwo310.compressed_copper.blockentity.MachineCoreBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

public class MachineCoreBlockEntityRenderer implements BlockEntityRenderer<MachineCoreBlockEntity> {
    public MachineCoreBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(MachineCoreBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        ResourceLocation moduleBlockLoc = getModuleBlockLoc(blockEntity.getModule());
        if (moduleBlockLoc == null) return;

        BlockRenderDispatcher blockEntityRenderers = Minecraft.getInstance().getBlockRenderer();

        // rotate the block to face the correct direction
        Direction direction = blockEntity.getDirection();
        poseStack.pushPose();
        switch (direction) {
            case NORTH:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.mulPose(new Quaternionf().rotateY(0));
                poseStack.translate(-0.5, -0.5, -0.5);
                break;
            case SOUTH:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(180)));
                poseStack.translate(-0.5, -0.5, -0.5);
                break;
            case WEST:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(90)));
                poseStack.translate(-0.5, -0.5, -0.5);
                break;
            case EAST:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(270)));
                poseStack.translate(-0.5, -0.5, -0.5);
                break;
            default:
                break;
        }

        blockEntityRenderers.renderSingleBlock(
                RegistryObject.create(moduleBlockLoc, ForgeRegistries.BLOCKS).get().defaultBlockState(),
                poseStack,
                bufferSource,
                combinedLight,
                combinedOverlay,
                ModelData.EMPTY, // ← why
                RenderType.solid()
        );
        poseStack.popPose();
    }

    @Nullable
    public static ResourceLocation getModuleBlockLoc(ItemStack itemStack) {
        if (itemStack.isEmpty()) return null;
        ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(itemStack.getItem());
        if (registryName == null) return null;
        return new ResourceLocation(registryName + "_block");
    }
}
