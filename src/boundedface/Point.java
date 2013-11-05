package boundedface;

/**
 *
 * @author utkarshjaiswal
 */
public class Point {
    public double x;
    public double y;
    public LineSegment a;
    public LineSegment b;
    BoundedFace.EventType type;

    @Override
    public boolean equals(Object obj) {
    Point p = (Point)obj;
    if(this.type== p.type && this.x == p.x && this.y == p.y && this.a == p.a && this.b == p.b )
       return true;
    else 
       return false;
    }
}
