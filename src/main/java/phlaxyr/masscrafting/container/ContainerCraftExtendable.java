package phlaxyr.masscrafting.container;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import phlaxyr.masscrafting.blocks.gui.BlockCraftGUI;

public abstract class ContainerCraftExtendable extends Container
{
	/** COPY-PASTA'D */
    /** The crafting matrix inventory, except adjustable. */
    private InventoryCrafting craftMatrix; 
    public InventoryCrafting getCraftMatrix() {
    	return craftMatrix;
    }
    
    public IInventory craftResult = new InventoryCraftResult();
    private final World worldObj;
    /** Position of the workbench */
    private final BlockPos pos;
    
    public abstract InventoryCrafting getMatrix();
    
    public ContainerCraftExtendable(InventoryPlayer playerInventory, World worldIn, BlockPos posIn)
    {
    	craftMatrix = getMatrix();
    	
    	int mtxHeight = craftMatrix.getHeight();
    	int mtxWidth = craftMatrix.getWidth();
        this.worldObj = worldIn;
        this.pos = posIn;
        this.addSlotToContainer(new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, 0, 124, 35));
        
        for (int i = 0; i < mtxHeight; ++i)
        {
            for (int j = 0; j < mtxWidth; ++j)
            {
                this.addSlotToContainer(new Slot(this.craftMatrix, j + i * craftMatrix.getHeight(), 30 + j * 18, 17 + i * 18));
            }
        }

        
        
        //main inventory
        for (int k = 0; k < 3; ++k)
        {
            for (int i1 = 0; i1 < 9; ++i1)
            {
                this.addSlotToContainer(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        //hotbar
        for (int l = 0; l < 9; ++l)
        {
            this.addSlotToContainer(new Slot(playerInventory, l, 8 + l * 18, 142));
        }

        this.onCraftMatrixChanged(this.craftMatrix);
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.worldObj));
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);

        /**
        if (!this.worldObj.isRemote)
        {
            for (int i = 0; i < 9; ++i)
            {
                ItemStack itemstack = this.craftMatrix.removeStackFromSlot(i);

                if (itemstack != null)
                {
                    playerIn.dropItem(itemstack, false);
                }
            }
        }
        */
    }

    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.worldObj.getBlockState(this.pos).getBlock() 
        		instanceof BlockCraftGUI ? withinVicinity(playerIn) : false;
    }

    /**
     * Take a stack from the specified inventory slot.
     */
    @Nullable
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 0)
            {
                if (!this.mergeItemStack(itemstack1, 10, 46, true))
                {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (index >= 10 && index < 37)
            {
                if (!this.mergeItemStack(itemstack1, 37, 46, false))
                {
                    return null;
                }
            }
            else if (index >= 37 && index < 46)
            {
                if (!this.mergeItemStack(itemstack1, 10, 37, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 10, 46, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(playerIn, itemstack1);
        }

        return itemstack;
    }

    /**
     * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in
     * is null for the initial slot that was double-clicked.
     */
    public boolean canMergeSlot(ItemStack stack, Slot slotIn)
    {
        return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
    }
    
    public boolean withinVicinity(EntityPlayer playerIn) {
    	 return playerIn.getDistanceSq(
    			 (double)this.pos.getX() + 0.5D, 
    			 (double)this.pos.getY() + 0.5D, 
    			 (double)this.pos.getZ() + 0.5D
    			 ) <= 64.0D;
    }
}