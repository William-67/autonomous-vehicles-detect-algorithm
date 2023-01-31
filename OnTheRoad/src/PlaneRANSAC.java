import java.io.IOException;

public class PlaneRANSAC {

    private double epsilon;

    private PointCloud pc;

    public PlaneRANSAC(PointCloud pc){

        this.pc = pc;

        epsilon = 10;// by default

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

    public void run(int numberOfIterations, String filename) throws IOException {

        if (pc.getSize()==0){
            System.out.println("There is no point in the point cloud");
            return;
        }

        int bestSupport = 0;

        PointCloud bestPc = new PointCloud();
        Plane3D bestPlane = null;

        PointCloud resultPc = new PointCloud();

        for (int i = 0; i < numberOfIterations; i++){

            int currentSupport = 0;

            PointCloud temp = pc;

            Point3D p1 = temp.getPoint();
            temp.remove(p1);

            Point3D p2 = temp.getPoint();
            temp.remove(p2);

            Point3D p3 = temp.getPoint();
            temp.remove(p3);

            Plane3D plane = new Plane3D(p1,p2,p3);

            for(int j =0; j < temp.getSize();j++){

                if (plane.getDistance(temp.getPoint(j)) < epsilon){
                    currentSupport++;
                }

            }

            if (currentSupport > bestSupport){

                bestSupport = currentSupport;
                bestPc = temp;
                bestPlane = plane;

                if (resultPc.getSize() != 0){
                    resultPc = new PointCloud();
                }

                resultPc.addPoint(p1);
                resultPc.addPoint(p2);
                resultPc.addPoint(p3);

            }

        }

        pc = bestPc;

        for (int i =0; i < bestPc.getSize();i++){

            if (bestPlane.getDistance(bestPc.getPoint(i)) == 0){

                pc.remove(bestPc.getPoint(i));
                resultPc.addPoint(bestPc.getPoint(i));

            }

        }

        resultPc.save(filename);

    }

    public void saveTheRest(String filename){

        pc.save(filename);

    }

}
