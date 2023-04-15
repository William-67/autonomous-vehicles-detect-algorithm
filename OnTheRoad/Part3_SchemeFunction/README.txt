This deliverable is implemented with Scheme

For the support and dominantPlane function, we require the eps value but it's not included as a parameter.
Therefore I add eps as parameter for these two functions

I am not sure it is the correct way of doing it; Therefore, I submit 2 files:

planeRANSACAlg: This is the file I add eps as parameter in 2 functions
RANSAC_backup: This is the file without eps, but at the top of the file I define eps 0.2

Both works after calling (planeRANSAC filename confidence percentage eps)
