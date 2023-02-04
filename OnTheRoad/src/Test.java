import java.io.IOException;
import java.util.LinkedList;

public class Test {


    public static void main(String[] args) throws IOException, CloneNotSupportedException {

        for (int i = 1;  i < 4; i++){

            String filename = "PointCloud"+ i +".xyz";

            PointCloud pc = new PointCloud(filename);

            PlaneRANSAC planeRANSAC = new PlaneRANSAC(pc);

            //Assume 99% confidence, 10% support
            int iterationNumber = planeRANSAC.getNumberOfIterations(
                    0.99,0.95);

            planeRANSAC.setEps(1);

            for (int j = 1; j < 4;j++){

                String tempFileName = "PointCloud" + i + "_p" + j +".xyz";

                planeRANSAC.run(iterationNumber, tempFileName);

            }

            planeRANSAC.saveTheRest("PointCloud" + i + "_p0" +".xyz");

        }

    }

//    public void linkTest(){
//        LinkedList<Integer> strings = new LinkedList<>();
//
//        for (int i =0;i< 8;i++){
//            strings.add(i);
//        }
//
//        for (int i =0;i< strings.size();i++){
//            System.out.println(strings.get(i));
//            strings.remove(strings.get(i));
//            i--;
//        }
//    }

}
