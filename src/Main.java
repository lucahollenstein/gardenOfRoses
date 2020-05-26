import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        // Uncomment this line if you want to read from a file
        // In.open("public/sample.in");
        // Out.compareTo("public/sample.out");

        //int t = In.readInt();
        Scanner sc = new Scanner(System.in);
        int t = sc.nextInt();
        for (int i = 0; i < t; i++) {
            testCase(sc);
        }

        // Uncomment this line if you want to read from a file
        // In.close();

    }

    public static void testCase(Scanner sc) {
        // Input using In.java class
        //int n = In.readInt();
        //int m = In.readInt();
        int n = sc.nextInt();
        int m = sc.nextInt();

        Point[] roses = new Point[n];
        Point[] weeds = new Point[m];

        for (int i = 0; i < n; i++) {
            //int x = In.readInt();
            //int y = In.readInt();
            int x = sc.nextInt();
            int y = sc.nextInt();
            roses[i] = new Point(x, y);

        }

        for (int i = 0; i < m; i++) {
            //int x = In.readInt();
            //int y = In.readInt();
            int x = sc.nextInt();
            int y = sc.nextInt();
            weeds[i] = new Point(x, y);
        }

        ArrayList<Point> hull = getConvexHull(roses);


        //now have to define for each weed if it is outside this hull or not
        String[] output = new String[m];
        for (int i = 0; i < m; i++) {
            if (isInside(hull, hull.size(), weeds[i])) {
                output[i] = "y";
            } else {
                output[i] = "n";
            }
        }
        for (int i = 0; i < m; i++) {
            System.out.print(output[i]);
        }
        System.out.println();
    }


    public static int leftOrRight(Point p, Point q, Point r) {
        int val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);

        if (val == 0) return 0;  // collinear

        return (val > 0) ? 1 : 2; // clock or counterclock wise
    }


    //return points which define the convexHull counterclockwise
    public static ArrayList<Point> getConvexHull(Point[] points) {
        //assumes that there are no two starting points (no two "least" x's)

        //didnt work out exception classsCast Exception
       // Arrays.sort(points);
        int n = points.length;

        //there must be at least 3 points
        if (n < 3) return null;

        ArrayList<Point> hull = new ArrayList<Point>();
        //since sorted array by x
        int start = 0;
        for(int i = 1; i < n; i++){
            if(points[i].x < points[start].x)
                start = i;
        }
        int p = start, q;
        do {
            hull.add(points[p]);
            q = (p + 1) % n;
            for (int i = 0; i < n; i++) {
                //if i i s more counterclockwise than current q, then update q
                if (leftOrRight(points[p], points[i], points[q]) == 2)
                    q = i;
            }
            //q is the most "left" now
            p = q;
        } while (p != start); // while we dont come to first point

        return hull;
    }

    //give the three colinear points p, 1 ,r , the function checks if point q lies on line segment pr
    static boolean onSegment(Point p, Point q, Point r) {
        if (q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x) &&
                q.y <= Math.max(p.y, r.y) && q.y >= Math.min(p.y, r.y)) {
            return true;
        }
        return false;
    }

    static boolean doIntersect(Point p1, Point q1, Point p2, Point q2) {
        // Find the four orientations needed for general and special cases
        int o1 = leftOrRight(p1, q1, p2);
        int o2 = leftOrRight(p1, q1, q2);
        int o3 = leftOrRight(p2, q2, p1);
        int o4 = leftOrRight(p2, q2, q1);

        // General case
        if (o1 != o2 && o3 != o4) {
            return true;
        }

        // Special Cases
        // p1, q1 and p2 are collinear and  p2 lies on segment p1q1
        if (o1 == 0 && onSegment(p1, p2, q1)) {
            return true;
        }

        // p1, q1 and p2 are collinear and q2 lies on segment p1q1
        if (o2 == 0 && onSegment(p1, q2, q1)) {
            return true;
        }

        // p2, q2 and p1 are collinear and p1 lies on segment p2q2
        if (o3 == 0 && onSegment(p2, p1, q2)) {
            return true;
        }

        // p2, q2 and q1 are collinear and q1 lies on segment p2q2
        if (o4 == 0 && onSegment(p2, q1, q2)) {
            return true;
        }

        // Doesn't fall in any of the above cases
        return false;
    }


    static boolean isInside(ArrayList<Point> polygon, int n, Point p) {
        // There must be at least 3 vertices in polygon[]
        if (n < 3) {
            return false;
        }

        // Create a point for line segment from p to infinite
        // Point extreme = new Point(100000, p.y);
        Point extreme = new Point(p.x + 10000, p.y + 10001);

        // Count intersections of the above line with sides of polygon
        int count = 0, i = 0;
        do {
            int next = (i + 1) % n;

            // Check if the line segment from 'p' to 'extreme' intersects with the line segment from 'polygon[i]' to 'polygon[next]'
            if (doIntersect(polygon.get(i), polygon.get(next), p, extreme)) {
                // If the point 'p' is colinear with line segment 'i-next', then check if it lies
                // on segment. If it lies, return true, otherwise false
                if (leftOrRight(polygon.get(i), p, polygon.get(next)) == 0) {
                    return onSegment(polygon.get(i), p, polygon.get(next));
                }

                count++;
            }
            i = next;
        } while (i != 0);

        // Return true if count is odd, false otherwise
        return (count % 2 == 1);
    }

    static class Point {

        int x;
        int y;

        final int RIGHT = 1;
        final int LEFT = -1;
        final int ZERO = 0;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int compareTo(Point o) {
            return Integer.compare(x, o.x);
        }

/*
    public int directionOfPoint(Point a, Point b, Point p){
        //subtracting coordinates of point a from b and p to make a as origin
        b.x -= a.x;
        b.y -= a.y;
        p.x -= a.x;
        p.y -= a.y;

        int crossProduct = b.x * p.y - b.y * p.x;
        //return right if crossproduct is positive
        if(crossProduct > 0)
            return RIGHT;
        //return left if crossproduct is negative
        if(crossProduct < 0)
            return LEFT;
        //return zero if crossprudct is zero
        return ZERO;
    }
*/
    }

}
