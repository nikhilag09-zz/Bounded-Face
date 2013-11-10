package boundedface;

/**
 *
 * @author utkarshjaiswal
 * @author nikhilagrawal
 */
public class Point{
    public double x;
    public double y;
    public LineSegment a;
    public LineSegment b;
    BoundedFace.EventType type;

    @Override
    public boolean equals(Object obj) {
    Point p = (Point)obj;
    if(this.type== p.type && Math.abs(this.x - p.x) < 0.00001 && Math.abs(this.y - p.y) <0.00001 && this.a.id == p.a.id){
       if(this.b != null && p.b != null) {
           return (this.b.id == p.b.id)?true:false;
       }
       else if(this.b == null && p.b == null) {
           return true;
       }
       
       return false;
    }
    else 
       return false;
    }
 
    
}
