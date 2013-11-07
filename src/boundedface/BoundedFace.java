package boundedface;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

/**
 *
 * @author utkarshjaiswal
 * @author nikhilagrawal
 */
public class BoundedFace {

    public enum EventType {

        Intersection, Start, End
    }
    TreeSet<Point> EventQueue;
    TreeSet<LineSegment> SweepStatus;
    List<LineSegment> LineSet;
    DCEL graph;
    Integer edgecount;
    Integer facecount;
    Integer vertexcount;
    Point currentEvent;
    double lambda;

    public BoundedFace(List<LineSegment> set) {
        this.LineSet = set;
        EventQueue = new TreeSet<Point>(new PointComparator());
        SweepStatus = new TreeSet<LineSegment>(new LineSegmentComparator());
        graph = new DCEL();
        vertexcount = 0;
        edgecount = 0;
        facecount = 0;
        initializeQueue();
    }

    public void startSweepLine() {
        while (!EventQueue.isEmpty()) {
            currentEvent = EventQueue.pollLast();
            lambda = currentEvent.y;
            switch (currentEvent.type) {
                case Start:
                    eventStart();
                    break;
                case Intersection:
                    eventIntersection();
                    break;
                case End:
                    eventEnd();
                    break;
                default:
                    System.out.println("Error occoured in switch case");
                    break;
            }
        }
        postprocessing();
    }

    public void eventStart() {
        Point check;
        LineSegment p1, p2;
        SweepStatus.add(currentEvent.a);
        p1 = SweepStatus.higher(currentEvent.a);
        p2 = SweepStatus.lower(currentEvent.a);
        
        if(p1!=null){
            check = p1.getIntersection(currentEvent.a);
            if (check != null && check.y < lambda) {
                EventQueue.add(check);
            }
        }
        check = currentEvent.a.getIntersection(p2);
        if (check != null && check.y < lambda) {
            EventQueue.add(check);
        }
    }

    public void eventEnd() {
        Point check;
        LineSegment p1, p2;
        p1 = SweepStatus.higher(currentEvent.a);
        p2 = SweepStatus.lower(currentEvent.a);
        if (p1 != null && p2 != null) {
            check = p2.getIntersection(p1);
            if (check != null && check.y < lambda) {
                EventQueue.add(check);
            }
        }
        SweepStatus.remove(currentEvent.a);
    }

    public void eventIntersection() {
        Vertex a;
        LineSegment l1,l2;
        l1= currentEvent.a;
        l2= currentEvent.b;
        
        a = new Vertex();
        a.id = vertexcount++;
        a.p = currentEvent;
        Edge edge1,edge2,edge3,edge4,tmp1,tmp2;
        Edge Edgel1down,Edgel1up,Edgel2down,Edgel2up;
        edge1= new Edge();
        edge2= new Edge();
        edge3= new Edge();
        edge4= new Edge();
        
        // Assigning next and previous for 4 new edges formed because of intersection
        edge1.id = edgecount++;
        edge2.id = edgecount++;
        edge3.id = edgecount++;
        edge4.id = edgecount++;
        edge1.twinEdgeId = edge2.id;
        edge2.twinEdgeId = edge1.id;
        edge3.twinEdgeId = edge4.id;
        edge4.twinEdgeId = edge3.id;
        edge1.startId = a.id;
        edge3.startId = a.id;
        edge2.endId = a.id;
        edge4.endId = a.id;
        
       
        // Assigning previous and next from the edge already in linesegment with point of intersection
        edge2.nextEdgeId = edge3.id;
        edge3.previousEdgeId = edge2.id;
        Edgel1down = graph.edgelist.get(l1.lastEdgeID);
        if(Edgel1down != null)
                Edgel1up = graph.edgelist.get(Edgel1down.twinEdgeId);
        else
            Edgel1up = null;
        Edgel2down = graph.edgelist.get(l2.lastEdgeID);
        if(Edgel2down != null)
            Edgel2up = graph.edgelist.get(Edgel2down.twinEdgeId);
        else
            Edgel2up = null;
        
        if(l1.lastEdgeID !=-1){
            Edgel1down.nextEdgeId = edge1.id;
            Edgel1down.endId = a.id;
            Edgel1up.startId = a.id;
            edge1.previousEdgeId = Edgel1down.id;
        }
        if(l2.lastEdgeID !=-1){
            Edgel2down.endId = a.id;
            Edgel2up.startId = a.id;
            Edgel2up.previousEdgeId = edge4.id;
            edge4.nextEdgeId = Edgel2up.id;
        }
        
        if(l2.lastEdgeID!=-1 && l1.lastEdgeID!=-1){
            Edgel2down.nextEdgeId = Edgel1up.id;
            Edgel1up.previousEdgeId = Edgel2down.id;
        }
        else if(l2.lastEdgeID == -1 && l1.lastEdgeID == -1){
            edge4.nextEdgeId = edge1.id;
            edge1.previousEdgeId = edge4.id;
        }
        else if(l1.lastEdgeID != -1){
            edge4.nextEdgeId = Edgel1up.id;
            Edgel1up.previousEdgeId = edge4.id;
        }
        else if(l2.lastEdgeID != -1){
            Edgel2down.nextEdgeId = edge1.id;
            edge1.previousEdgeId = Edgel2down.id;
        }
        /* next and previous assignment complete;*/
        
        // adding Edges and vertex in the graph
        graph.edgelist.put(edge1.id, edge1);
        graph.edgelist.put(edge2.id, edge2);
        graph.edgelist.put(edge3.id, edge3);
        graph.edgelist.put(edge4.id, edge4);
        graph.vertexlist.put(a.id, a);
        
        // Swaping the postion of line segment in height balanced tree
        LineSegment l1lower, l2higher;
        l1lower = SweepStatus.lower(l1);
        l2higher = SweepStatus.higher(l2);
        Point check;
        if(l2higher!=null){
            check = l2higher.getIntersection(l1);
            if (check != null && check.y < lambda) {
                EventQueue.add(check);
            }
        }
        check = l2.getIntersection(l1lower);
        if (check != null && check.y < lambda) {
            EventQueue.add(check);
        }
        l1.lastEdgeID = edge3.id;
        l2.lastEdgeID = edge1.id;
        SweepStatus.remove(l1);
        SweepStatus.add(l1);
        
    }
    public void postprocessing(){
        for (int i = 0; i < edgecount; i++) {
            boolean faceflag = true;
            Edge rootedge,lastedge,currentedge,twinedge,nextedge;
            rootedge = graph.edgelist.get(i);
            if(rootedge.visited)
                continue;
            rootedge.visited =true;
            if(rootedge.nextEdgeId !=-1){
                lastedge = rootedge;
                currentedge = graph.edgelist.get(rootedge.nextEdgeId);
                while(rootedge!=currentedge && faceflag){
                    currentedge.visited = true;
                    if(currentedge.nextEdgeId == -1){
                        twinedge = graph.edgelist.get(currentedge.twinEdgeId);
                        nextedge = graph.edgelist.get(twinedge.nextEdgeId);
                        lastedge.nextEdgeId = nextedge.id;
                        nextedge.previousEdgeId = lastedge.id;
                        twinedge.visited = true;
                        currentedge = nextedge;
                    }
                    else{
                        lastedge = currentedge;
                        currentedge = graph.edgelist.get(currentedge.nextEdgeId);
                    }
                    if(currentedge.id == rootedge.twinEdgeId){
                        faceflag = false;
                    }
                }
            }
            else{
                lastedge = graph.edgelist.get(rootedge.previousEdgeId);
                twinedge = graph.edgelist.get(rootedge.twinEdgeId);
                nextedge = graph.edgelist.get(twinedge.nextEdgeId);
                lastedge.nextEdgeId = nextedge.id;
                nextedge.previousEdgeId = lastedge.id;
                
                twinedge.visited = true;
                faceflag = false;
            }
            
            if(faceflag){
                Face f = new Face();
                f.Id = facecount++;
                f.edgeId = rootedge.id;
                graph.facelist.put(f.Id, f);
            }
        }
        System.out.println("Total faces = " + facecount );
    }

    private void initializeQueue() {
        for (LineSegment p : LineSet) {
            EventQueue.add(p.start);
            EventQueue.add(p.end);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BoundedFace bface;
        List<LineSegment> linesegments;
        
        linesegments = initrandomline(10);
        
       bface = new BoundedFace(linesegments);
       
       bface.startSweepLine();
       
        for (int i = 0; i < bface.facecount; i++) {
            System.out.println(" Face " + (i+1) +" coordinates: ");
            Edge root,nextedge;
            Vertex v;
            root = bface.graph.edgelist.get(bface.graph.facelist.get(i).edgeId);
            v = bface.graph.vertexlist.get(root.startId);
                System.out.println( v.p.x +" , " + v.p.y);
            nextedge = bface.graph.edgelist.get(root.nextEdgeId);
            while(nextedge.id != root.id){
                v = bface.graph.vertexlist.get(nextedge.startId);
                System.out.println( v.p.x +" , " + v.p.y);
                nextedge = bface.graph.edgelist.get(nextedge.nextEdgeId);
            }
        }
       
        SVGCreator.drawLinesAndFaces(linesegments, bface.graph, bface.facecount);
    }
    
    public static List<LineSegment> initrandomline(int n){
        List<LineSegment> linesegments = new ArrayList<LineSegment>();
        Point start,end,tmp;
        LineSegment a;
        Random randomgenerator = new Random();
        
        
        //1
        while(n-- > 0 ){
            start = new Point();
            end = new Point();
            start.x = randomgenerator.nextInt(100);
            start.y = randomgenerator.nextInt(100);

            end.x = randomgenerator.nextInt(100);
            end.y = randomgenerator.nextInt(100);
            if(start.y<end.y){
                tmp = start;
                start =end;
                end = tmp;
            }

            start.type = EventType.Start;
            end.type = EventType.End;
            a = new LineSegment(start, end);
            start.a = a;
            end.a = a;
            linesegments.add(a);
        }
//        //2
//        
//        start = new Point();
//        end = new Point();
//        start.x = 2;
//        start.y = 5;
//        start.type = EventType.Start;
//        end.x = 1;
//        end.y = 1;
//        end.type = EventType.End;
//        a = new LineSegment(start, end);
//        linesegments.add(a);
//        start.a = a;
//        end.a = a;
//        //3
//        
//        start = new Point();
//        end = new Point();
//        start.x = 4;
//        start.y = 4;
//        start.type = EventType.Start;
//        end.x = 1;
//        end.y = 3;
//        end.type = EventType.End;
//        a = new LineSegment(start, end);
//        linesegments.add(a);
//        start.a = a;
//        end.a = a;
//        
        return linesegments;
    }

    class LineSegmentComparator implements Comparator<LineSegment> {

        @Override
        public int compare(LineSegment o1, LineSegment o2) {
            if(o1 != null && o1.equals(o2)) return 0;
            
            if ( (o1.getXIntersectionLambda(lambda) > o2.getXIntersectionLambda(lambda)) ||
                        (o1.getXIntersectionLambda(lambda) - o2.getXIntersectionLambda(lambda)) < 0.00001 ) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    class PointComparator implements Comparator<Point> {

        @Override
        public int compare(Point o1, Point o2) {
            if(o1 != null && o1.equals(o2)) return 0;
            
            if (o1.y > o2.y) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}