/*
Author: Lixiong Wei
Description:
    This java file is to represent a 3D plane, by using the equation
    ax + by + cz + d =0
*/

import java.lang.Math;

public class Plane3D {

    private double a;
    private double b;
    private double c;
    private double d;


    public Plane3D(Point3D p1, Point3D p2, Point3D p3) {
        /*
        Methodology:

        p1（x1,y1,z1），p2(x2,y2,z2)，p3(x3,y3,z3)
        p1p2（x2-x1,y2-y1,z2-z1), p1p3(x3-x1,y3-y1,z3-z1)

        n = p1p2 x p1p3 cross product

        |   i      j      k   |
        | x2-x1  y2-y1  z2-z1 | = (a,b,c)
        | x3-x1  y3-y1  z3-z1 |

        a = (y2-y1)*(z3-z1) - (y3-y1)*(z2-z1)
        b = (z2-z1)*(x3-x1) - (x2-x1)*(z3-z1)
        c = (x2-x1)*(y3-y1) - (y2-y1)*(x3-x1)
        d = -(ax1 + by1 + cz1)
        */

        a = (p2.getY() - p1.getY()) * (p3.getZ() - p1.getZ()) - (p3.getY() - p1.getY()) * (p2.getZ() - p1.getZ());
        b = (p2.getZ() - p1.getZ()) * (p3.getX() - p1.getX()) - (p3.getZ() - p1.getZ()) * (p2.getX() - p1.getX());
        c = (p2.getX() - p1.getX()) * (p3.getY() - p1.getY()) - (p3.getX() - p1.getX()) * (p2.getY() - p1.getY());
        d = -(a * p1.getX() + b * p1.getY() + c * p1.getZ());

    }

    public Plane3D(double a, double b, double c, double d) {

        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;

    }

    public double getDistance(Point3D pt){

        double numerator = Math.abs(a * pt.getX() + b * pt.getY() + c * pt.getZ() + d);
        double denominator = Math.sqrt(a * a + b * b + c * c);

        return numerator/denominator;
    }

    //getter

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }

    public double getD() {
        return d;
    }

    @Override
    public String toString() {
        if (d>0){

            return a+"x + " + b + "y + "+ c +"z + " + d +" = 0";

        }else{

            return a+"x + " + b + "y + "+ c +"z " + d +" = 0";

        }

    }

    public static void main(String[] args) {


        //Unit test
        Point3D p1 = new Point3D(1,1,1);
        Point3D p2 = new Point3D(1,2,0);
        Point3D p3 = new Point3D(-1,2,1);
        Point3D p4 = new Point3D(5,2,7);
        Point3D p5 = new Point3D(1,2,0);
        Plane3D plane3D = new Plane3D(p1,p2,p3);

        //expected : 1.0x + 2.0y + 2.0z -5.0 = 0
        System.out.println(plane3D);

        //expected : 6.0 ;
        System.out.println(plane3D.getDistance(p5)==0);

    }

}
