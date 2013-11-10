package boundedface;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
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
    ArrayList<LineSegment> SweepStatus;
    List<LineSegment> LineSet;
    DCEL graph;
    Integer edgecount;
    Integer facecount;
    Integer vertexcount;
    Integer linecount;
    Point currentEvent;
    double lambda;
    

    public BoundedFace() {
        linecount = 0;
        this.LineSet = initline();
        EventQueue = new TreeSet<Point>(new PointComparator());
        SweepStatus = new ArrayList<LineSegment>();
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
        LineSegment tmp=null;
        p1=null;p2=null;
        int i=0;
        for ( i = 0; i < SweepStatus.size(); i++) {
            tmp = SweepStatus.get(i);
            if(tmp.getXIntersectionLambda(lambda) > currentEvent.x ){
                p1 = tmp; 
                break;
            }
        }
        if(i>0){
            p2 = SweepStatus.get(i-1);
        }
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
        
        SweepStatus.add(i, currentEvent.a);
    }

    public void eventEnd() {
        Point check;
        LineSegment p1=null, p2=null;
        LineSegment tmp;
        
        int i=0;
        for ( i = 0; i < SweepStatus.size(); i++) {
            tmp = SweepStatus.get(i);
            if(tmp == currentEvent.a){
                SweepStatus.remove(i);
                break;
            }
        }
        if(i< SweepStatus.size()){
            p1= SweepStatus.get(i);
        }
        if(i>0){
            p2 = SweepStatus.get(i-1);
        }
        
        if (p1 != null && p2 != null) {
            check = p1.getIntersection(p2);
            if (check != null && check.y < lambda) {
                EventQueue.add(check);
            }
        }
   }

    public void eventIntersection() {
        Vertex a;
        LineSegment l1,l2;
        l1= currentEvent.a;
        l2= currentEvent.b;
        
        a = new Vertex();
        a.id = vertexcount++;
        a.p = currentEvent;
        System.out.println((vertexcount-1) + " " + a.p.x + " " + a.p.y );
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
        
        
        
        l1.lastEdgeID = edge3.id;
        l2.lastEdgeID = edge1.id;
        
        LineSegment tmp;
        
        int i=0;
        for ( i = 0; i < SweepStatus.size(); i++) {
            tmp = SweepStatus.get(i);
            if(tmp == l1){
                SweepStatus.set(i, l2);
                SweepStatus.set(i+1, l1);
                break;
            }
        }
        
        LineSegment l1higher =null, l2lower =null;
        if(i>0){
            l2lower = SweepStatus.get(i-1);
        }
        if(i<SweepStatus.size()-2){
            l1higher = SweepStatus.get(i+2);
        }
        Point check;
        if(l1higher!=null){
            check = l1higher.getIntersection(l1);
            if (check != null && check.y < lambda) {
                EventQueue.add(check);
            }
        }
        check = l2.getIntersection(l2lower);
        if (check != null && check.y < lambda) {
            EventQueue.add(check);
        }
    }
    public void postprocessing(){
        double area;
        Vertex a,b;
        for (int i = 0; i < edgecount; i++) {
            area=0;
            boolean faceflag = true;
            Edge rootedge,lastedge,currentedge,twinedge,nextedge;
            rootedge = graph.edgelist.get(i);
            if(rootedge.visited)
                continue;
            rootedge.visited =true;
            if(rootedge.nextEdgeId !=-1){
                lastedge = rootedge;
                currentedge = graph.edgelist.get(rootedge.nextEdgeId);
                a = graph.vertexlist.get(lastedge.startId);
                b = graph.vertexlist.get(currentedge.startId);
                area += (b.p.x - a.p.x)*(b.p.y + a.p.y);
                
                while(rootedge!=currentedge && faceflag){
                    if(currentedge.visited == true){
                        faceflag = false;
                        break;
                    }
                    if(currentedge.nextEdgeId == -1){
//                        currentedge.visited = true;
                        twinedge = graph.edgelist.get(currentedge.twinEdgeId);
                        nextedge = graph.edgelist.get(twinedge.nextEdgeId);
                        lastedge.nextEdgeId = nextedge.id;
                        nextedge.previousEdgeId = lastedge.id;
//                        twinedge.visited = true;
                        currentedge = nextedge;
                    }
                    else{
                        lastedge = currentedge;
                        currentedge = graph.edgelist.get(currentedge.nextEdgeId);
                        a = graph.vertexlist.get(lastedge.startId);
                        b = graph.vertexlist.get(currentedge.startId);
                        area += (b.p.x - a.p.x)*(b.p.y + a.p.y);
                        
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
                if(area > 0){
                    Face f = new Face();
                    f.Id = facecount++;
                    f.edgeId = rootedge.id;
                    graph.facelist.put(f.Id, f);
                }
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
       bface = new BoundedFace();
       
       bface.startSweepLine();
       
        for (int i = 0; i < bface.facecount; i++) {
            System.out.println(" Face " + (i+1) +" coordinates: ");
            Edge root,nextedge;
            Vertex v;
            root = bface.graph.edgelist.get(bface.graph.facelist.get(i).edgeId);
            v = bface.graph.vertexlist.get(root.startId);
                System.out.println( v.p.x +" , " + v.p.y +" " + v.id);
            nextedge = bface.graph.edgelist.get(root.nextEdgeId);
            while(nextedge.id != root.id){
                v = bface.graph.vertexlist.get(nextedge.startId);
                System.out.println( v.p.x +" , " + v.p.y + "  " + v.id);
                nextedge = bface.graph.edgelist.get(nextedge.nextEdgeId);
            }
        }
       
        SVGCreator.drawLinesAndFaces(bface.LineSet, bface.graph, bface.facecount,bface.vertexcount);
    }
    
    private List<LineSegment> initline(){
        System.out.println("1 : Random Lines");
        System.out.println("2 : User Specified Lines");
        System.out.println("3 : Hardcoded lines ");
        
        System.out.print("Enter your Choice: ");
        Scanner in = new Scanner(System.in);
          int n = in.nextInt();
        //1
        switch(n){
            case 1: return createRandomLine();                    
            case 2: return userSpecifiedLine();                    
            case 3: return hardCodedLine();                    
            default: System.out.println("Wrong Choice selected");
                     return null;
        }
        
        
    }
    private List<LineSegment> createRandomLine(){
        Scanner in = new Scanner(System.in);
        System.out.print("Enter the number of Random Lines : ");
        int n = in.nextInt();
        System.out.print("Enter Max Value of X coordinate : ");
        int xmax = in.nextInt();
        System.out.print("Enter Max Value of Y coordinate : ");
        int ymax = in.nextInt();
        List<LineSegment> linesegments = new ArrayList<LineSegment>();
        Point start,end,tmp;
        LineSegment a;
        Random randomgenerator = new Random();
        
        while(n-- > 0 ){
            start = new Point();
            end = new Point();
            start.x = randomgenerator.nextInt(xmax);
            start.y = randomgenerator.nextInt(ymax);

            end.x = randomgenerator.nextInt(xmax);
            end.y = randomgenerator.nextInt(ymax);
            if(start.y<end.y){
                tmp = start;
                start =end;
                end = tmp;
            }

            start.type = EventType.Start;
            end.type = EventType.End;
            a = new LineSegment(start, end,linecount++);
            start.a = a;
            end.a = a;
            linesegments.add(a);
        }
        return linesegments;
    }
    private List<LineSegment> userSpecifiedLine(){
        Scanner in = new Scanner(System.in);
        System.out.print("Enter the number of Lines : ");
        int n = in.nextInt();
        
        List<LineSegment> linesegments = new ArrayList<LineSegment>();
        Point start,end,tmp;
        LineSegment a;
        System.out.println("Enter The coordinates of end points of lines :");
        while(n-- > 0 ){
            start = new Point();
            end = new Point();
            start.x = in.nextInt();
            start.y = in.nextInt();

            end.x = in.nextInt();
            end.y = in.nextInt();
            if(start.y<end.y){
                tmp = start;
                start =end;
                end = tmp;
            }

            start.type = EventType.Start;
            end.type = EventType.End;
            a = new LineSegment(start, end,linecount++);
            start.a = a;
            end.a = a;
            linesegments.add(a);
        }
        return linesegments;
    }
    private List<LineSegment> hardCodedLine(){
        
        List<LineSegment> linesegments = new ArrayList<LineSegment>();
        Point start,end,tmp;
        LineSegment a;
        
        start = new Point();
        end = new Point();
        start.x = 20;
        start.y = 60;
        start.type = EventType.Start;
        end.x = 60;
        end.y = 30;
        end.type = EventType.End;
        a = new LineSegment(start, end,linecount++);
        linesegments.add(a);
        start.a = a;
        end.a = a;
        
        //2
        
        start = new Point();
        end = new Point();
        start.x = 40;
        start.y = 60;
        start.type = EventType.Start;
        end.x = 0;
        end.y = 20;
        end.type = EventType.End;
        a = new LineSegment(start, end,linecount++);
        linesegments.add(a);
        start.a = a;
        end.a = a;
        
        //3
        
        start = new Point();
        end = new Point();
        start.x = 10;
        start.y = 50;
        start.type = EventType.Start;
        end.x = 50;
        end.y = 20;
        end.type = EventType.End;
        a = new LineSegment(start, end,linecount++);
        linesegments.add(a);
        start.a = a;
        end.a = a;
        
        //4
        
        start = new Point();
        end = new Point();
        start.x = 60;
        start.y = 50;
        start.type = EventType.Start;
        end.x = 20;
        end.y = 10;
        end.type = EventType.End;
        a = new LineSegment(start, end,linecount++);
        linesegments.add(a);
        start.a = a;
        end.a = a;
        //5
        
        start = new Point();
        end = new Point();
        start.x = 0;
        start.y = 40;
        start.type = EventType.Start;
        end.x = 40;
        end.y = 10;
        end.type = EventType.End;
        a = new LineSegment(start, end,linecount++);
        linesegments.add(a);
        start.a = a;
        end.a = a;
        //6
        
        start = new Point();
        end = new Point();
        start.x = 50;
        start.y = 50;
        start.type = EventType.Start;
        end.x = 20;
        end.y = 30;
        end.type = EventType.End;
        a = new LineSegment(start, end,linecount++);
        linesegments.add(a);
        start.a = a;
        end.a = a;
        //7
        
        start = new Point();
        end = new Point();
        start.x = 30;
        start.y = 70;
        start.type = EventType.Start;
        end.x = 40;
        end.y = 0;
        end.type = EventType.End;
        a = new LineSegment(start, end,linecount++);
        linesegments.add(a);
        start.a = a;
        end.a = a;
        
        return linesegments;
    }

//    class LineSegmentComparator implements Comparator<LineSegment> {
//
//        @Override
//        public int compare(LineSegment o1, LineSegment o2) {
//            if(o1.equals(o2)) return 0;
//            
//            if ( (o1.getXIntersectionLambda(lambda) > o2.getXIntersectionLambda(lambda)) 
//                    ||  Math.abs(o1.getXIntersectionLambda(lambda) - o2.getXIntersectionLambda(lambda)) < 0.000000000001 
//                    ) {
//                return 1;
//            } else {
//                return -1;
//            }
//        }
//    }

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