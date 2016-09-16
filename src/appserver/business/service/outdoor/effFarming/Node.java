package dk.aau.astep.appserver.business.service.outdoor.effFarming;

import dk.aau.astep.appserver.model.shared.Coordinate;

/**
 * Created by Zobair on 12-04-2016.
 */
public class Node extends AbstractNode {

    private Coordinate centerPoint;

    /**
     * Gets the centerpoint of the node
     *
     * @return Coordinate object
     */
    public Coordinate getCenterPoint() {
        return centerPoint;
    }

    /**
     * Initliazes a new instance of Node class.
     *
     * @param centerPoint of node
     */
    public Node(Coordinate centerPoint) {
        super();
        this.centerPoint = centerPoint;
    }
}