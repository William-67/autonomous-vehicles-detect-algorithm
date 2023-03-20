package main

import (
    "sync"
    "time"
    "math"
    "math/rand"
    "fmt"
    "os"
    "bufio"
    "strings"
    "strconv"
)

// var bestSupport Plane3DwSupport
var pointcloud []Point3D

type Point3D struct {
    X float64
    Y float64
    Z float64
}

type Plane3D struct {
    A float64
    B float64
    C float64
    D float64
}

type Plane3DwSupport struct {
    Plane3D
    SupportSize int
}

func ConvertStrToFloat64(str string) float64{

    num, err := strconv.ParseFloat(str, 64)
    if err != nil {
        fmt.Println(err)
        return 0.1
    }
    return num

}

func ReadXYZ(filename string) []Point3D {

    var pc []Point3D
    
    inF, err := os.Open(filename)
    if err != nil{
        fmt.Println("Unable to open file")
        return pc
    }
    defer inF.Close()

    scanner := bufio.NewScanner(inF)

    scanner.Scan()

    for scanner.Scan(){

        line := scanner.Text()

        values := strings.Split(line, "\t")

        var floValues []float64

        for _,str := range values{

            num, err := strconv.ParseFloat(str,64)
            if err != nil {
                fmt.Println(str)
                fmt.Println("Unable to convert string to float64")
                return pc
            }
            floValues = append(floValues, num)
        }

        pt := Point3D{floValues[0],floValues[1],floValues[2]}
        pc = append(pc,pt)

    }

    if err := scanner.Err(); err != nil{
        fmt.Println("Unable to read line")
        return pc
    }

    return pc
}


func SaveXYZ(filename string, points []Point3D) {

    writter, err := os.Create(filename)
    if err != nil {
        return 
    }

    defer writter.Close()

    for i,pt := range points{

        if i == 0{
            _, err = writter.WriteString("X\tY\tZ\n")
            if err != nil {
                return 
            }
        }

        str := fmt.Sprintf("%f\t%f\t%f\n",pt.X,pt.Y,pt.Z)
        _, err = writter.WriteString(str)
        if err != nil {
            return 
        }
    }
    return
}

func (p1 *Point3D) GetDistance(p2 *Point3D) float64 {

    return math.Sqrt( (p1.X-p2.X)*(p1.X-p2.X) + (p1.Y-p2.Y)*(p1.Y-p2.Y) + (p1.Z-p2.Z)*(p1.Z-p2.Z) )

}

func GetPlane(points []Point3D) Plane3D {

    p1 := points[0]
    p2 := points[1]
    p3 := points[2]

    a := (p2.Y - p1.Y) * (p3.Z - p1.Z) - (p3.Y - p1.Y) * (p2.Z - p1.Z);
    b := (p2.Z - p1.Z) * (p3.X - p1.X) - (p3.Z - p1.Z) * (p2.X - p1.X);
    c := (p2.X - p1.X) * (p3.Y - p1.Y) - (p3.X - p1.X) * (p2.Y - p1.Y);
    d := -(a * p1.X + b * p1.Y + c * p1.Z);

    plane := Plane3D{a,b,c,d}

    return plane

}

func GetNumberOfIterations(confidence float64, percentageOfPointsOnPlane float64) int{

    numerator := math.Log10(1 - confidence)
    denominator := math.Log10(1 - math.Pow(percentageOfPointsOnPlane,3))

    result := numerator/denominator
    return int(math.Round(result))

}

func GetSupport(plane Plane3D, points []Point3D, eps float64) Plane3DwSupport{

    support := 0 

    for _,pt := range points{

        numerator := math.Abs(plane.A * pt.X + plane.B * pt.Y + plane.C * pt.Z + plane.D)
        denominator := math.Sqrt(plane.A * plane.A + plane.B * plane.B + plane.C * plane.C)

        result := numerator/denominator

        if result < eps{
            support++
        }

    }

    planeSupport := Plane3DwSupport{plane,support}

    return planeSupport
}

func GetSupportingPoints(plane Plane3D, points []Point3D, eps float64) []Point3D{

    var pc []Point3D

    for _,pt := range points{

        numerator := math.Abs(plane.A * pt.X + plane.B * pt.Y + plane.C * pt.Z + plane.D)
        denominator := math.Sqrt(plane.A * plane.A + plane.B * plane.B + plane.C * plane.C)

        result := numerator/denominator

        if result < eps{
            pc = append(pc,pt)
        }

    }
    return pc

}

func RemovePlane(plane Plane3D, points []Point3D, eps float64) []Point3D{

    for i :=0; i < len(points); i++ {

        pt := points[i]

        numerator := math.Abs(plane.A * pt.X + plane.B * pt.Y + plane.C * pt.Z + plane.D)
        denominator := math.Sqrt(plane.A * plane.A + plane.B * plane.B + plane.C * plane.C)

        result := numerator/denominator

        if result < eps {
            points[i] = points[len(points) - 1]

            points = points[:len(points)-1]
            i--
        }

    }

    return points
}

func RandomPointGenerator( stop chan bool) chan Point3D{

    out := make(chan Point3D)
    go func() {

        for{

            select{

                case <- stop: 
                    return
                case out <- pointcloud[rand.Intn(len(pointcloud))]:

            }
            time.Sleep(time.Second)
        }

    }()

    return out
}

func main(){

    var wg sync.WaitGroup
    var mutex sync.Mutex

    filename := os.Args[1]
    confidence := ConvertStrToFloat64(os.Args[2]) 
    percentage := ConvertStrToFloat64(os.Args[3]) 
    epsilon := ConvertStrToFloat64(os.Args[4]) 

    pointcloud = ReadXYZ(filename)

    var bestSupport = Plane3DwSupport{Plane3D{0,0,0,0},0}

    numOfIteration := GetNumberOfIterations(confidence,percentage)

    wg.Add(numOfIteration)

    // for i := 0; i < 3; i++ {

    bestSupport = Plane3DwSupport{Plane3D{0,0,0,0},0}

    for j := 0; j < numOfIteration; j++ {

        go func(){
                
            stop := make(chan bool)

            a := <- RandomPointGenerator(stop)
            b := <- RandomPointGenerator(stop)
            c := <- RandomPointGenerator(stop)
            stop <- true


            time.Sleep(time.Second)
            // go func(out chan int) {

            //     for{
            //         out <- rand.Intn(len(pointcloud))
            //         time.Sleep(1)
            //     }

            // }(outPoints)

            // a := <- outPoints
            // b := <- outPoints
            // c := <- outPoints

            // defer close(outPoints)

            pointList := []Point3D{a,b,c}

            plane := GetPlane(pointList)

            support := GetSupport(plane, pointcloud, epsilon)

            //to avoid rewrite (Fan-in)
            mutex.Lock()

            if support.SupportSize > bestSupport.SupportSize{

                bestSupport = support

            }
            mutex.Unlock()
            wg.Done()

        }()
            
    }
    wg.Wait()

    supportPC := GetSupportingPoints(bestSupport.Plane3D, pointcloud, epsilon)
    removePC := RemovePlane(bestSupport.Plane3D,pointcloud,epsilon)

    SaveXYZ("_p.xyz", supportPC)
    SaveXYZ("_p0.xyz", removePC)



// // Test for all previous function 
//     p1 := Point3D{3,4,5}
//     p2 := Point3D{5,6,7}
//     p3 := Point3D{8,6,2}

//     p4 := Point3D{6,15,13}
//     p5 := Point3D{5.4,5.6,0.7}
//     p6 := Point3D{8.9,6.4,2.1}

//     fmt.Println("Test for GetDistance() between two points")

//     fmt.Println("The distance is: ", p1.GetDistance(&p2))

//     PointList := []Point3D{p1,p2,p3,p4,p5,p6}

//     plane := GetPlane(PointList)

//     eps := 1.7

//     fmt.Println("Test for GetPlane(): ", plane)

//     fmt.Println("Test for GetNumberOfIterations(): ", GetNumberOfIterations(0.95, 0.34))

//     fmt.Println("Test for GetSupport(): ", GetSupport(plane, PointList,eps))

//     fmt.Println("Test for GetSupportingPoints(): ", GetSupport(plane, PointList,eps))
//     fmt.Println("Test for RemovePlane(): ", RemovePlane(plane, PointList,eps))

//     pc2 := ReadXYZ("PointCloud1.xyz")

//     SaveXYZ("test.xyz", pc2)

}