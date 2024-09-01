package io.github.meatwo310.compressed_copper.recipe;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.meatwo310.compressed_copper.CompressedCopper;
import io.github.meatwo310.compressed_copper.blockentity.MachineCoreBlockEntity;
import io.github.meatwo310.compressed_copper.item.CompressableItem;
import io.github.meatwo310.compressed_copper.handler.item.ItemStackHandlerPlus;
import io.github.meatwo310.compressed_copper.util.MathUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CompressedMachineRecipe implements Recipe<SimpleContainer> {
    public static final String RECIPE_ID = "compressed_machine_recipe";

    public final CompressedMachineCodec codec;
    private final NonNullList<ItemStack> ingredients = NonNullList.create();
    private final NonNullList<ItemStack> resultItems = NonNullList.create();
    private final ResourceLocation recipeId;

    public CompressedMachineRecipe(CompressedMachineCodec compressedMachineCodec, ResourceLocation recipeId) {
        this.codec = compressedMachineCodec;
        this.ingredients.addAll(codec
                .getInputItems()
                .stream()
                .map(ItemStack::copy)
                .toList()
        );
        this.resultItems.addAll(codec
                .getOutputItems()
                .stream()
                .map(ItemStack::copy)
                .toList()
        );
        this.recipeId = recipeId;
    }

    @Override
    public boolean matches(SimpleContainer simpleContainer, Level level) {
        if (level.isClientSide()) return false;

        ItemStack moduleItem = simpleContainer.getItem(MachineCoreBlockEntity.SLOT_MODULE);
        int moduleCompressedLevel = CompressableItem.getCompressedLevel(moduleItem);
        if (moduleCompressedLevel < codec.getMinTier() || moduleCompressedLevel > codec.getMaxTier()) return false;

        ItemStackHandlerPlus input = new ItemStackHandlerPlus(MachineCoreBlockEntity.INPUT_SLOTS);
        for (int i = 0; i < MachineCoreBlockEntity.INPUT_SLOTS; i++) {
            input.setStackInSlot(i + MachineCoreBlockEntity.SLOT_INPUT, simpleContainer.getItem(i));
        }

        return input.hasStacks(ingredients);
    }

    @Override
    public ItemStack assemble(SimpleContainer simpleContainer, RegistryAccess registryAccess) {
        return this.resultItems.get(0).copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonNullIngredients = NonNullList.create();
        nonNullIngredients.addAll(ingredients.stream()
                .map(ItemStack::copy)
                .map(Ingredient::of)
                .toList()
        );
        return nonNullIngredients;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return codec.getOutputItems().get(0).copy();
    }

//    public NonNullList<ItemStack> getResultItems() {
//        return resultItems;
//    }

    @Override
    public ResourceLocation getId() {
        return this.recipeId;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public String toString() {
        return "CompressedMachineRecipe{" +
                "codec=" + codec +
                ", ingredients=" + ingredients +
                ", resultItems=" + resultItems +
                ", recipeId=" + recipeId +
                '}';
    }

    public static int getProcessingTime(ItemStack compressableModule, CompressedMachineCodec codec) {
        return getProcessingTime(
                CompressableItem.getCompressedLevel(compressableModule),
                codec.getMinTier(),
                codec.getMaxTier(),
                codec.getMinTierTicks(),
                codec.getMaxTierTicks()
        );
    }

    public static int getProcessingTime(int compressedLevel, int minTier, int maxTier, int minTierTicks, int maxTierTicks) {
        return MathUtil.map(compressedLevel, minTier, maxTier, minTierTicks, maxTierTicks);
    }

    public static class Type implements RecipeType<CompressedMachineRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = RECIPE_ID;
    }

    public static class Serializer implements RecipeSerializer<CompressedMachineRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(CompressedCopper.MODID, RECIPE_ID);

        @Override
        public CompressedMachineRecipe fromJson(ResourceLocation recipeId, JsonObject serializedRecipe) {
            Codec<CompressedMachineCodec> codecCodec = RecordCodecBuilder.create(instance -> instance.group(
                    ForgeRegistries.ITEMS.getCodec().fieldOf("module").forGetter(CompressedMachineCodec::getModule),
                    Codec.INT.optionalFieldOf("minTier", CompressableItem.MIN_COMPRESSED_LEVEL).forGetter(CompressedMachineCodec::getMinTier),
                    Codec.INT.optionalFieldOf("maxTier", CompressableItem.MAX_COMPRESSED_LEVEL).forGetter(CompressedMachineCodec::getMaxTier),
                    Codec.INT.fieldOf("minTierTicks").forGetter(CompressedMachineCodec::getMinTierTicks),
                    Codec.INT.fieldOf("maxTierTicks").forGetter(CompressedMachineCodec::getMaxTierTicks),
                    ItemStack.CODEC.listOf().fieldOf("inputItems").forGetter(CompressedMachineCodec::getInputItems),
                    ItemStack.CODEC.listOf().fieldOf("outputItems").forGetter(CompressedMachineCodec::getOutputItems)
            ).apply(instance, CompressedMachineCodec::new));

            DataResult<CompressedMachineCodec> parsed = codecCodec.parse(JsonOps.INSTANCE, serializedRecipe);
            Optional<CompressedMachineCodec> optionalCodec = parsed.result();

            if (optionalCodec.isEmpty()) {
                LogUtils.getLogger().error("Failed to parse compressed machine recipe");
                parsed.error().ifPresent(partialResult -> LogUtils.getLogger().error(partialResult.message().replaceAll("; ", ";\n")));
                return null;
            }

            CompressedMachineCodec codec = optionalCodec.get();
            if (codec.getMinTier() < CompressableItem.MIN_COMPRESSED_LEVEL) {
                LogUtils.getLogger().error("minTier {} must not be less than {}", codec.getMinTier(), CompressableItem.MIN_COMPRESSED_LEVEL);
            } else if (codec.getMinTier() > CompressableItem.MAX_COMPRESSED_LEVEL) {
                LogUtils.getLogger().error("minTier {} must not be greater than {}", codec.getMinTier(), CompressableItem.MAX_COMPRESSED_LEVEL);
            } else if (codec.getMaxTier() < CompressableItem.MIN_COMPRESSED_LEVEL) {
                LogUtils.getLogger().error("maxTier {} must not be less than {}", codec.getMaxTier(), CompressableItem.MIN_COMPRESSED_LEVEL);
            } else if (codec.getMaxTier() > CompressableItem.MAX_COMPRESSED_LEVEL) {
                LogUtils.getLogger().error("maxTier {} must not be greater than {}", codec.getMaxTier(), CompressableItem.MAX_COMPRESSED_LEVEL);
            } else if (codec.getMinTierTicks() < 1) {
                LogUtils.getLogger().error("minTierTicks {} must not be less than 1", codec.getMinTierTicks());
            } else if (codec.getMaxTierTicks() < 1) {
                LogUtils.getLogger().error("maxTierTicks {} must not be less than 1", codec.getMaxTierTicks());
            } else if (codec.getInputItems().isEmpty()){
                LogUtils.getLogger().error("inputItems must not be empty");
            } else if (codec.getOutputItems().isEmpty()){
                LogUtils.getLogger().error("outputItems must not be empty");
            } else {
                return new CompressedMachineRecipe(optionalCodec.get(), recipeId);
            }
            return null;
        }

        @Override
        public @Nullable CompressedMachineRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buf) {
            Item module = buf.readItem().getItem();
            int minTier = buf.readInt();
            int maxTier = buf.readInt();
            int minTierTicks = buf.readInt();
            int maxTierTicks = buf.readInt();
            int inputSize = buf.readVarInt();
            NonNullList<ItemStack> inputItems = NonNullList.create();
            for (int i = 0; i < inputSize; i++) {
                inputItems.add(buf.readItem());
            }
            int outputSize = buf.readVarInt();
            NonNullList<ItemStack> outputItems = NonNullList.create();
            for (int i = 0; i < outputSize; i++) {
                outputItems.add(buf.readItem());
            }
            CompressedMachineCodec codec = new CompressedMachineCodec(module, minTier, maxTier, minTierTicks, maxTierTicks, inputItems, outputItems);
            return new CompressedMachineRecipe(codec, resourceLocation);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, CompressedMachineRecipe recipe) {
            buf.writeItemStack(recipe.codec.getModule().getDefaultInstance(), false);
            buf.writeInt(recipe.codec.getMinTier());
            buf.writeInt(recipe.codec.getMaxTier());
            buf.writeInt(recipe.codec.getMinTierTicks());
            buf.writeInt(recipe.codec.getMaxTierTicks());
            buf.writeVarInt(recipe.codec.getInputItems().size());
            recipe.codec.getInputItems().stream().map(Ingredient::of).forEach(ingredient -> ingredient.toNetwork(buf));
            buf.writeVarInt(recipe.codec.getOutputItems().size());
            recipe.codec.getOutputItems().stream().map(Ingredient::of).forEach(ingredient -> ingredient.toNetwork(buf));
        }
    }
}
