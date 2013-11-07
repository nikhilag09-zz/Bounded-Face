/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package boundedface;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author utkarshjaiswal
 */
public class SVGCreator {
    
    static public void drawLinesAndFaces(List<LineSegment> lineset, DCEL graph, int facecount) {
        PrintWriter svgfile = null;
        try {
            svgfile = new PrintWriter("BoundedFace.svg", "UTF-8");
            svgfile.write("<!DOCTYPE html>\n<html>\n<body>\n\n<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n");
            
            for (LineSegment l : lineset) {
                svgfile.write("<line "
                        + "x1 = \"" + String.format("%.2f", l.start.x*10) + "\" "
                        + "y1 = \"" + String.format("%.2f", l.start.y*10) + "\" "
                        + "x2 = \"" + String.format("%.2f", l.end.x*10) + "\" "
                        + "y2 = \"" + String.format("%.2f", l.end.y*10) + "\" "
                        + "style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"
                        );
            }
            
            for (int i = 0; i < facecount; i++) {
                Edge root,nextedge;
                Vertex v;
                root = graph.edgelist.get(graph.facelist.get(i).edgeId);
                v = graph.vertexlist.get(root.startId);
                svgfile.write("<polygon points=\"");
                svgfile.write( String.format("%.2f",v.p.x*10) +"," + String.format("%.2f",v.p.y*10));
                nextedge = graph.edgelist.get(root.nextEdgeId);
                while(nextedge.id != root.id){
                    v = graph.vertexlist.get(nextedge.startId);
                    svgfile.write( " " + String.format("%.2f",v.p.x*10) +"," + String.format("%.2f",v.p.y*10));
                    
                    nextedge = graph.edgelist.get(nextedge.nextEdgeId);
                }
                svgfile.write("\" style=\"fill:lime;stroke-width:1\"/>\n");
            }
            
            svgfile.write("</svg>\n\n</body>\n</html>\n");
            svgfile.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SVGCreator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SVGCreator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            svgfile.close();
        }
    }
}
