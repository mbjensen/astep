package dk.aau.astep.appserver.business.service.outdoor.effFarming;

import java.util.ArrayList;

/**
 * Created by Zobair on 12-04-2016.
 *
 * Abstract node class, NB always call constructor from child
 */
public abstract class AbstractNode {
    private ArrayList<AbstractNode> neighbours;

    /**
     * Get list of neighbours
     * @return arraylist neighbours
     */
    public ArrayList<AbstractNode> getNeighbours() {
        return neighbours;
    }

    /**
     * Adds a neighbour in the list of neighbours
     * @param neighbour to add
     * @return True if success otherwise false
     */
    public boolean addNeighbour(AbstractNode neighbour){
        return neighbours.add(neighbour);
    }

    /**
     * Gets the count of neighbours using arraylist.size
     * @return Number of neighbours
     */
    public int getNeighbourCount() {
        return neighbours.size();
    }

    /**
     * Initializes variables for abstract class
     */
    public AbstractNode() {
        this.neighbours = new ArrayList<>();
    }
}
