/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package boundedface;

import java.util.HashMap;


/**
 *
 * @author utkarshjaiswal
 */
public class DCEL {
    HashMap<Integer, Vertex> vertexlist;
    HashMap<Integer, Edge> edgelist;
    HashMap<Integer, Face> facelist;

    public DCEL() {
        vertexlist = new HashMap<Integer, Vertex>();
        edgelist = new HashMap<Integer, Edge>();
        facelist = new  HashMap<Integer, Face>();
    }
}

class Vertex {
    Point p;
    Integer id;
}


class Edge {
    Integer id;
    Integer startId;
    Integer endId;
    Integer twinEdgeId;
    Integer nextEdgeId;
    Integer previousEdgeId;
    boolean visited;

    public Edge() {
        startId=-1;
        endId =-1;
        twinEdgeId=-1;
        nextEdgeId=-1;
        previousEdgeId=-1;
        visited = false;
    }
}

class Face {
    Integer Id;
    Integer edgeId;
    boolean bounded;
}
