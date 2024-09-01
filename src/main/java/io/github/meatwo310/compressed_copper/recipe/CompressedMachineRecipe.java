package io.github.meatwo310.compressed_copper.recipe;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.meatwo310.compressed_copper.CompressedCopper;
import io.github.meatwo310.compressed_copper.handler.fluid.FluidsInputHandler;
import io.github.meatwo310.compressed_copper.handler.item.ItemStackInputHandler;
import io.github.meatwo310.compressed_copper.item.CompressableItem;
import io.github.meatwo310.compressed_copper.util.MathUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CompressedMachineRecipe implements Recipe<NotContainer> {
    public static final String RECIPE_ID = "compressed_machine_recipe";

    public final CompressedMachineCodec codec;
    private final NonNullList<ItemStack> ingredients = NonNullList.create();
    private final NonNullList<ItemStack> resultItems = NonNullList.create();
    private final ResourceLocation recipeId;

    public CompressedMachineRecipe(CompressedMachineCodec compressedMachineCodec, ResourceLocation recipeId) {
        this.codec = compressedMachineCodec;
        this.ingredients.addAll(codec
                .inputItems()
                .stream()
                .map(ItemStack::copy)
                .toList()
        );
        this.resultItems.addAll(codec
                .outputItems()
                .stream()
                .map(ItemStack::copy)
                .toList()
        );
        this.recipeId = recipeId;
    }

    @Override
    public boolean matches(NotContainer notContainer, Level level) {
        if (level.isClientSide()) return false;

        MachineData machineData = notContainer.getMachineData();

        // check module compressed level
        ItemStack moduleItem = machineData.module().getStackInSlot(0);
        int moduleCompressedLevel = CompressableItem.getCompressedLevel(moduleItem);
        if (moduleCompressedLevel < this.codec.minTier() || moduleCompressedLevel > this.codec.maxTier()) return false;

        // check input items
        ItemStackInputHandler input = machineData.input();
        if (!input.hasStacks(this.ingredients)) return false;

        // check input fluids
        FluidsInputHandler fluidInput = machineData.fluidInput();
        if (!fluidInput.containsFluids(this.codec.inputFluids())) return false;

        return true;
    }

    @Override
    public ItemStack assemble(NotContainer simpleContainer, RegistryAccess registryAccess) {
//        return this.resultItems.get(0).copy();
        return ItemStack.EMPTY;
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
//        return codec.outputItems().get(0).copy();
        return ItemStack.EMPTY;
    }

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
        // TODO: Update this if necessary
        return "CompressedMachineRecipe{" +
                "codec=" + codec +
                ", ingredients=" + ingredients +
                ", resultItems=" + resultItems +
                ", recipeId=" + recipeId +
                '}';
    }

    public static int getProcessingTime(ItemStack compressibleModule, CompressedMachineCodec codec) {
        return getProcessingTime(
                CompressableItem.getCompressedLevel(compressibleModule),
                codec.minTier(),
                codec.maxTier(),
                codec.minTierTicks(),
                codec.maxTierTicks()
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
                    ForgeRegistries.ITEMS.getCodec().fieldOf("module").forGetter(CompressedMachineCodec::module),
                    Codec.INT.optionalFieldOf("minTier", CompressableItem.MIN_COMPRESSED_LEVEL).forGetter(CompressedMachineCodec::minTier),
                    Codec.INT.optionalFieldOf("maxTier", CompressableItem.MAX_COMPRESSED_LEVEL).forGetter(CompressedMachineCodec::maxTier),
                    Codec.INT.fieldOf("minTierTicks").forGetter(CompressedMachineCodec::minTierTicks),
                    Codec.INT.fieldOf("maxTierTicks").forGetter(CompressedMachineCodec::maxTierTicks),
                    ItemStack.CODEC.listOf().optionalFieldOf("inputItems", List.of()).forGetter(CompressedMachineCodec::inputItems),
                    ItemStack.CODEC.listOf().optionalFieldOf("outputItems", List.of()).forGetter(CompressedMachineCodec::outputItems),
                    FluidStack.CODEC.listOf().optionalFieldOf("inputFluids", List.of()).forGetter(CompressedMachineCodec::inputFluids),
                    FluidStack.CODEC.listOf().optionalFieldOf("outputFluids", List.of()).forGetter(CompressedMachineCodec::outputFluids)
            ).apply(instance, CompressedMachineCodec::new));

            DataResult<CompressedMachineCodec> parsed = codecCodec.parse(JsonOps.INSTANCE, serializedRecipe);
            Optional<CompressedMachineCodec> optionalCodec = parsed.result();

            if (optionalCodec.isEmpty()) {
                LogUtils.getLogger().error("Failed to parse compressed machine recipe");
                parsed.error().ifPresent(partialResult -> LogUtils.getLogger().error(partialResult.message().replaceAll("; ", ";\n")));
                return null;
            }

            CompressedMachineCodec codec = optionalCodec.get();
            if (codec.minTier() < CompressableItem.MIN_COMPRESSED_LEVEL) {
                LogUtils.getLogger().error("minTier {} must not be less than {}", codec.minTier(), CompressableItem.MIN_COMPRESSED_LEVEL);
            } else if (codec.minTier() > CompressableItem.MAX_COMPRESSED_LEVEL) {
                LogUtils.getLogger().error("minTier {} must not be greater than {}", codec.minTier(), CompressableItem.MAX_COMPRESSED_LEVEL);
            } else if (codec.maxTier() < CompressableItem.MIN_COMPRESSED_LEVEL) {
                LogUtils.getLogger().error("maxTier {} must not be less than {}", codec.maxTier(), CompressableItem.MIN_COMPRESSED_LEVEL);
            } else if (codec.maxTier() > CompressableItem.MAX_COMPRESSED_LEVEL) {
                LogUtils.getLogger().error("maxTier {} must not be greater than {}", codec.maxTier(), CompressableItem.MAX_COMPRESSED_LEVEL);
            } else if (codec.minTierTicks() < 1) {
                LogUtils.getLogger().error("minTierTicks {} must not be less than 1", codec.minTierTicks());
            } else if (codec.maxTierTicks() < 1) {
                LogUtils.getLogger().error("maxTierTicks {} must not be less than 1", codec.maxTierTicks());
            } else if (codec.inputItems().isEmpty() &&
                    codec.inputFluids().isEmpty()
            ) {
                LogUtils.getLogger().error("recipes must have at least one input");
            } else if (codec.outputItems().isEmpty() &&
                    codec.outputFluids().isEmpty()
            ) {
                LogUtils.getLogger().error("recipes must have at least one output");
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
            int inputItemsSize = buf.readVarInt();
            NonNullList<ItemStack> inputItems = NonNullList.create();
            for (int i = 0; i < inputItemsSize; i++) {
                inputItems.add(buf.readItem());
            }
            int outputItemsSize = buf.readVarInt();
            NonNullList<ItemStack> outputItems = NonNullList.create();
            for (int i = 0; i < outputItemsSize; i++) {
                outputItems.add(buf.readItem());
            }
            int inputFluidsSize = buf.readVarInt();
            NonNullList<FluidStack> inputFluids = NonNullList.create();
            for (int i = 0; i < inputFluidsSize; i++) {
                inputFluids.add(buf.readFluidStack());
            }
            int outputFluidsSize = buf.readVarInt();
            NonNullList<FluidStack> outputFluids = NonNullList.create();
            for (int i = 0; i < outputFluidsSize; i++) {
                outputFluids.add(buf.readFluidStack());
            }
            CompressedMachineCodec codec = new CompressedMachineCodec(module,
                    minTier,
                    maxTier,
                    minTierTicks,
                    maxTierTicks,
                    inputItems,
                    outputItems,
                    inputFluids,
                    outputFluids
            );
            return new CompressedMachineRecipe(codec, resourceLocation);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, CompressedMachineRecipe recipe) {
            buf.writeItemStack(recipe.codec.module().getDefaultInstance(), false);
            buf.writeInt(recipe.codec.minTier());
            buf.writeInt(recipe.codec.maxTier());
            buf.writeInt(recipe.codec.minTierTicks());
            buf.writeInt(recipe.codec.maxTierTicks());
            buf.writeVarInt(recipe.codec.inputItems().size());
            recipe.codec.inputItems().stream().map(Ingredient::of).forEach(ingredient -> ingredient.toNetwork(buf));
            buf.writeVarInt(recipe.codec.outputItems().size());
            recipe.codec.outputItems().stream().map(Ingredient::of).forEach(ingredient -> ingredient.toNetwork(buf));
            buf.writeVarInt(recipe.codec.inputFluids().size());
            recipe.codec.inputFluids().forEach(fluidStack -> fluidStack.writeToPacket(buf));
            buf.writeVarInt(recipe.codec.outputFluids().size());
            recipe.codec.outputFluids().forEach(fluidStack -> fluidStack.writeToPacket(buf));
        }
    }
}
