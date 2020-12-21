package main.model;

/**
 * A cell is part of the grid. It can contain an agent, an item, or both an agent and an item.
 * agentOnTop is null unless there is an agent on the cell where an item is already located.
 */
public class Cell
{
    private CellContent cellContent;
    private Agent agentOnTop;

    public Cell(CellContent cellContent)
    {
        this.cellContent = cellContent;
        this.agentOnTop = null;
    }

    public Cell()
    {
        this.cellContent = null;
        this.agentOnTop = null;
    }

    public boolean placeAgentOnTop(Agent agent)
    {
        if(this.agentOnTop == null)
        {
            this.agentOnTop = agent;
            return true;
        }
        return false;
    }

    public void removeAgentFromTop()
    {
        this.agentOnTop = null;
    }

    public boolean hasAgentOnTop()
    {
        return this.agentOnTop != null;
    }

    public void removeContent()
    {
        this.cellContent = null;
    }

    public void setCellContent(CellContent cellContent)
    {
        this.cellContent = cellContent;
    }

    public boolean hasContent()
    {
        return this.cellContent != null;
    }

    public CellContent getCellContent()
    {
        return cellContent;
    }
}
