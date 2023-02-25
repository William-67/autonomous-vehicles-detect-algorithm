import java.io.IOException;
import java.util.Iterator;

public class PlaneRANSAC {

    private double epsilon;

    private PointCloud pc;

    public PlaneRANSAC(PointCloud pc){

        this.pc = pc;

        epsilon = 1;// by default

    }

    public void setEps(double eps){
        epsilon = eps;
    }

    public double getEps(){
        return epsilon;
    }


    public int getNumberOfIterations(double confidence,
                                     double percentageOfPointsOnPlane){

        //k= log( 1 - C ) / log( 1- p^3 )

        double numerator, denominator;

        numerator = Math.log10(1-confidence);
        denominator= Math.log10(1-Math.pow(percentageOfPointsOnPlane,3));

        return (int)Math.round(numerator/denominator);

    }

    public void run(int numberOfIterations, String filename) throws IOException, CloneNotSupportedException {

        if (pc.getSize()==0){
            System.out.println("There is no point in the point cloud");
            return;
        }

        int bestSupport = 0;

        PointCloud bestPc = new PointCloud();

        PointCloud resultPc = new PointCloud();

        for (int i = 0; i < numberOfIterations; i++){

            int currentSupport = 0;

            PointCloud temp = (PointCloud) pc.clone();

            Point3D p1 = temp.getPoint();
            temp.remove(p1);

            Point3D p2 = temp.getPoint();
            temp.remove(p2);

            Point3D p3 = temp.getPoint();
            temp.remove(p3);

            Plane3D plane = new Plane3D(p1,p2,p3);

            PointCloud lastResult = (PointCloud) resultPc.clone();
            resultPc = new PointCloud();
            resultPc.addPoint(p1);
            resultPc.addPoint(p2);
            resultPc.addPoint(p3);

            for(int j =0; j < temp.getSize();j++){

                if (plane.getDistance(temp.getPoint(j)) < epsilon){
                    currentSupport++;
                    resultPc.addPoint(temp.getPoint(j));
                    temp.remove(temp.getPoint(j));
                    j--;
                }

            }

//            Iterator<Point3D> it = temp.iterator();
//            while (it.hasNext()){
//
//                Point3D aPoint = it.next();
//                if (plane.getDistance(aPoint) < epsilon){
//                    currentSupport++;
//                    resultPc.addPoint(aPoint);
//                    it.remove();
//                }
//            }

            if (currentSupport > bestSupport){

                bestSupport = currentSupport;
                bestPc = (PointCloud) temp.clone();

            }else{
                resultPc = lastResult;
            }

        }

        pc = (PointCloud) bestPc.clone();

        resultPc.save(filename);

    }

    public void saveTheRest(String filename){

        pc.save(filename);

    }

}
