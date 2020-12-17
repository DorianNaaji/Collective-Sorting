package main.model;

public class Item extends CellContent
{
    private ItemType itemType;

    public Item(ItemType type)
    {
        this.itemType = type;
    }

    public ItemType getItemType()
    {
        return itemType;
    }
}
