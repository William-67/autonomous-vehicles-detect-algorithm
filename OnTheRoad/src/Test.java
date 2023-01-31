import java.io.IOException;

public class Test {


    public static void main(String[] args) throws IOException {

        for (int i = 1;  i < 4; i++){

            String filename = "PointCloud"+ i +".xyz";

            PointCloud pc = new PointCloud(filename);

            PlaneRANSAC planeRANSAC = new PlaneRANSAC(pc);

            //Assume 99% confidence, 10% support
            int iterationNumber = planeRANSAC.getNumberOfIterations(
                    0.99,0.99);

            planeRANSAC.setEps(45);

            for (int j = 1; j < 4;j++){

                String tempFileName = "PointCloud" + i + "_p" + j +".xyz";

                planeRANSAC.run(iterationNumber, tempFileName);

            }

            planeRANSAC.saveTheRest("PointCloud" + i + "_p0" +".xyz");

        }

    }
}
