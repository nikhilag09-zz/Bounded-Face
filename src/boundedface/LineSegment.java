package boundedface;

/**
 *
 * @author utkarshjaiswal
 */
public class LineSegment {
    public Point start;
    public Point end;
    public double slope;
    public int lastEdgeID;
    public int id;
    boolean inf;

    @Override
    public boolean equals(Object obj) {
        LineSegment l = (LineSegment) obj;
        if(this.id == l.id)
            return true;
        return false;
    }
    
    public LineSegment(Point start, Point end,int id) {
        this.id = id;
        this.start = start;
        this.start.type = BoundedFace.EventType.Start;
        this.end = end;
        this.end.type = BoundedFace.EventType.End;
        try{
            slope = (start.y - end.y)/(start.x - end.x);
            inf = false;
        }
        catch(Exception e){
            inf=true;
        }
        this.lastEdgeID = -1;
    }
    
     public Point getIntersection(LineSegment o) {
        if(o==null) {
            return null;
        }
        
        Point p = new Point();
        p.type = BoundedFace.EventType.Intersection;
        
        if(inf && o.inf) {
            return null; // Parallel lines with inf slope.
        }
        
        if(slope == o.slope) {
            return null; // Parallel lines.
        }
        
        if(inf) {
            p.x = this.start.x;
            p.y = o.slope*p.x + (o.start.y - o.slope*o.start.x);
        }
        else if(o.inf) {   
            p.x = o.start.x;
            p.y = slope*p.x + (start.y - slope*start.x);
        }
        else {
            try {
                p.x = ((start.y - slope*start.x) - (o.start.y - o.slope * o.start.x))/(o.slope - slope);
            } catch (Exception e) {
                return null;
            }
            p.y = slope*p.x + (start.y - slope*start.x);
        }

        p.a=o;// left line.
        p.b =this; // right line.
        
        if(p.y<start.y && p.y >end.y && p.y<o.start.y && p.y >o.end.y ) {
            return p;
        }   
        return null;
    }
    
    
    public double getXIntersectionLambda(double lambda) {
        try{
            if(this.inf){
                return start.x;
            }
            return ((lambda - start.y)/ slope) + start.x;
        }
        catch(Exception e){
            return start.x; // will be removed just after addition 
        }
    }
}
