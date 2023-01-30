import java.io.IOException;

public class Test {


    public static void main(String[] args) throws IOException {

        for (int i = 1;  i < 4; i++){

            String filename = "PointCloud"+ i +".xyz";

            PointCloud pc = new PointCloud(filename);

            PlaneRANSAC planeRANSAC = new PlaneRANSAC(pc);

            //Assume 99% confidence, 10% support
            int iterationNumber = planeRANSAC.getNumberOfIterations(
                    0.99,0.1);

            for (int j = 0; j < 3;j++){

                String tempFileName = "PointCloud1_p"+ j +".xyz";

                planeRANSAC.run(iterationNumber, tempFileName);

            }

        }

    }
}
