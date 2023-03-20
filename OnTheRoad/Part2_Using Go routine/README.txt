There are two go files:
	1. planeRANSAC.go: this is the file for this project
	2. backup.go: this file is modified from Java(from project 1), which functions the same 
		as planeRANSAC.go. The only difference is, backup.go didn't use channel(pipeline). 
		This file is just in case if planeRANSAC.go does not function properly.


Both program runs with argument in cmd like this

"go run planeRANSAC.go pointcloud1.xyz 0.90 0.13 0.1"

For minimal number of threads(go routines), From the graph of pipeline given in part2.pdf, the number of thread
should be number of iteration based on confidence and percentage plus the whole pipeline(that is one thread) 

For optimal number of threads, I perform an experiment with different number of goroutines, I copied the
result in the other files


Fanin method reference : https://go.dev/blog/pipelines