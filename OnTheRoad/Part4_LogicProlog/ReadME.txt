The read_xyz_file given in guildline can not handle with the first line
since it cannot convert "x        y          z" to float.

Therefore when I test it, I delete the first line so that it can be done with no error.

I wrote the test case for four functions required:

test(plane, 1).
test(plane, 2).

test(support, 1).
test(support, 2).

test(ransac-number-of-iterations, 1).
test(ransac-number-of-iterations, 2). 

For testing random3Points, I wrote the test differently.

test(Times, PointsList). where Times means the number of time you want random3Points to be executed.

By default the testing pointlist will be [[1,2,3],[4,5,6],[7,8,9],[10,11,12],[13,14,15]]

Each time it will generate a list of three points, if running it 5 times, it will have a list
of five 3-random-points. The result of each list should be different.