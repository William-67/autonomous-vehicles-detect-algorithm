import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

public class PointCloud {

    private LinkedList<Point3D> cloud;

    public PointCloud() {

        cloud = new LinkedList<>();

    }

    public int getSize(){
        return cloud.size();
    }

    public PointCloud(String filename) throws IOException {

        cloud = new LinkedList<>();

        Scanner reader = new Scanner(new FileReader(filename));

        String line = reader.nextLine();

        while (reader.hasNextLine()){

            line = reader.nextLine();

            String[] current = line.split("\t");

            //Test if the file has been read
//            for (int i = 0;i<3;i++){
//                System.out.print(current[i]);
//            }
//            System.out.println();
            //System.out.println(cloud.size())

            double[] currentDouble = new double[current.length];

            try{

                for (int i =0; i < current.length; i++){

                    currentDouble[i] = Double.parseDouble(current[i]);

                }

                Point3D p = new Point3D(currentDouble[0],currentDouble[1],currentDouble[2]);

                cloud.add(p);

            }catch (Exception e){

                System.out.println("Cannot convert non-double to double");

            }

        }

        reader.close();

    }

    public void remove(Point3D pt){

        cloud.remove(pt);

    }

    public void addPoint(Point3D pt){

        cloud.add(pt);

    }

    public Point3D getPoint(){

        Random random = new Random();

        int index = random.nextInt(cloud.size());

        return cloud.get(index);

    }

    public Point3D getPoint(int index){

        return cloud.get(index);

    }


    public void save(String filename){

        try{

            File file = new File(filename);

            if (file.createNewFile()) {
                System.out.println("File "+ filename + " created");
            } else {
                System.out.println("File already exist, new file will overwrite the old one");
            }

        } catch (IOException e) {

            System.out.println("File create unsuccessfully");

        }

        try {

            FileWriter writer = new FileWriter(filename,false);

            writer.write("X     Y     Z\n");

            for (Point3D temp : cloud){

                writer.write(temp.getX() + "   " +
                        temp.getY() + "   " +
                        temp.getZ() + "   \n");

            }

            writer.close();

        }catch (IOException e){

            System.out.println("unsuccessfully write to file");

        }

    }

    public Iterator<Point3D> iterator(){

        Iterator<Point3D> it = new Iterator<Point3D>() {

            private int index = 0;

            @Override
            public boolean hasNext() {

                return index < cloud.size();

            }

            @Override
            public Point3D next() {

                Point3D p =  cloud.get(index);

                index++;

                return p;

            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

        };

        return it;
    }


    // main function is for self-testing only

    public static void main(String[] args) {
        PointCloud cloud1 = new PointCloud();
        cloud1.addPoint(new Point3D(3,2,1));
        cloud1.addPoint(new Point3D(4,9,7));
        cloud1.addPoint(new Point3D(5,3,4));
        cloud1.addPoint(new Point3D(6,6,6));
        cloud1.addPoint(new Point3D(6,7,9));
        cloud1.addPoint(new Point3D(7,8,6));

        for (int i = 0; i< 3; i++) System.out.println(cloud1.getPoint());
        //There is a chance they get the same point

//        PointCloud cloud2 = cloud1;  //by test, cloud1 pass by value
//
//        cloud2.remove(new Point3D(4,9,7));
//        cloud2.remove(new Point3D(5,3,4));
//        cloud2.remove(new Point3D(6,6,6));
//        cloud2.remove(new Point3D(6,7,9));
//        cloud2.remove(new Point3D(7,8,6));
//        for (int i = 0; i< 3; i++) System.out.println(cloud1.getPoint());
//        for (int i = 0; i< 3; i++) System.out.println(cloud2.getPoint());

    }

}
