package main.model;

public class Cell
{
    private CellContent cellContent;

    public Cell(CellContent cellContent)
    {
        this.cellContent = cellContent;
    }

    public CellContent getCellContent()
    {
        return cellContent;
    }
}
